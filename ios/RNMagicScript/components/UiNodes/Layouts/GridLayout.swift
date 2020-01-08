//
//  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

import Foundation
import SceneKit

@objc open class GridLayout: NSObject {
    @objc var width: CGFloat = 0 {
        didSet { invalidate() }
    }
    @objc var height: CGFloat = 0 {
        didSet { invalidate() }
    }
    @objc var columns: Int = 0 {
        didSet { invalidate() }
    }
    @objc var rows: Int = 0 {
        didSet { invalidate() }
    }
    @objc var defaultItemAlignment: Alignment = Alignment.topLeft {
        didSet { invalidate() }
    }
    @objc var defaultItemPadding: UIEdgeInsets = UIEdgeInsets.zero {
        didSet { invalidate() }
    }
    @objc var skipInvisibleItems: Bool = false {
        didSet { invalidate() }
    }

    var itemsCount: Int {
        return container.childNodes.count
    }

    var recalculateNeeded: Bool {
        return gridDescriptor == nil
    }

    let container: SCNNode = SCNNode()
    fileprivate var gridDescriptor: GridLayoutDescriptor?

    deinit {
        container.removeFromParentNode()
    }

    @objc func invalidate() {
        gridDescriptor = nil
    }

    @objc func getItem(at index: Int) -> TransformNode? {
        guard index >= 0 && index < container.childNodes.count else { return nil }
        return container.childNodes[index].childNodes.first as? TransformNode
    }

    @objc func addItem(_ item: TransformNode) {
        let proxyNode = SCNNode()
        proxyNode.name = item.name
        container.addChildNode(proxyNode)
        proxyNode.addChildNode(item)
        invalidate()
    }

    @discardableResult
    @objc func removeItem(_ item: TransformNode) -> Bool {
        if let proxyNode = item.parent,
            let parent = proxyNode.parent, parent == container {
            proxyNode.removeFromParentNode()
            item.removeFromParentNode()
            invalidate()
            return true
        }

        return false
    }

    @objc func hitTest(ray: Ray, node: UiNode) -> TransformNode? {
        guard let gridDescriptor = gridDescriptor else { return nil }
        guard let hitPoint = node.getHitTestPoint(ray: ray) else { return nil }
        let gridBounds = node.getBounds()
        let localPoint = CGPoint(x: CGFloat(hitPoint.x) - gridBounds.minX, y: gridBounds.height - (CGFloat(hitPoint.y) - gridBounds.minY))
        guard localPoint.x >= 0 && localPoint.x <= gridBounds.width,
            localPoint.y >= 0 && localPoint.y <= gridBounds.height else { return nil }

        let columnsBounds = gridDescriptor.columnsBounds
        let rowsBounds = gridDescriptor.rowsBounds

        var columnIndex = 0
        for (index, bound) in columnsBounds.enumerated() {
            if localPoint.x < bound.x + bound.width {
                columnIndex = index
                break
            }
        }

        var rowIndex = 0
        for (index, bound) in rowsBounds.enumerated() {
            if localPoint.y < bound.y + bound.height {
                rowIndex = index
                break
            }
        }

        let elementIndex = rowIndex * gridDescriptor.columns + columnIndex
        let hitNode = gridDescriptor.children[elementIndex].childNodes[0] as? TransformNode
        return hitNode?.hitTest(ray: ray) ?? node
    }

    @objc func recalculateIfNeeded() {
        if gridDescriptor == nil {
            gridDescriptor = calculateGridDescriptor()
        }
    }

    @objc func getSize() -> CGSize {
        recalculateIfNeeded()
        return gridDescriptor?.realSize ?? CGSize.zero
    }

    @objc func updateLayout() {
        guard let gridDescriptor = gridDescriptor else { return }

        let size = gridDescriptor.realSize
        let origin = CGPoint(x: -0.5 * size.width, y: 0.5 * size.height)
        for i in 0..<gridDescriptor.children.count {
            let result = getLocalPositionAndScaleForChild(at: i, desc: gridDescriptor)
            gridDescriptor.children[i].position = SCNVector3(origin.x + result.position.x, origin.y - result.position.y, 0)
            gridDescriptor.children[i].scale = SCNVector3(result.scale, result.scale, 1)
        }
    }
}

// MARK: - Helpers
extension GridLayout {
    fileprivate func getLocalPositionAndScaleForChild(at index: Int, desc: GridLayoutDescriptor) -> (position: CGPoint, scale: CGFloat) {
        let colId: Int = index % desc.columns
        let rowId: Int = index / desc.columns
        let cellSize: CGSize = desc.cellSizes[index]
        let defaultItemPaddingSize = CGSize(width: defaultItemPadding.left + defaultItemPadding.right, height: defaultItemPadding.top + defaultItemPadding.bottom)
        let childNodeSize: CGSize = cellSize - defaultItemPaddingSize
        let columnBounds = desc.columnsBounds[colId]
        let rowBounds = desc.rowsBounds[rowId]

        let columnContentWidth = columnBounds.width - defaultItemPaddingSize.width
        let rowContnetHeight = rowBounds.height - defaultItemPaddingSize.height
        let gridSlotCenter = CGPoint(x: columnBounds.x + defaultItemPadding.left + 0.5 * columnContentWidth,
                                     y: rowBounds.y + defaultItemPadding.top + 0.5 * rowContnetHeight)

        let scale: CGFloat = Math.clamp(min(columnContentWidth / childNodeSize.width, rowContnetHeight / childNodeSize.height), 0, 1)

        let deltaWidth: CGFloat = columnContentWidth - childNodeSize.width * scale
        let deltaHeight: CGFloat = rowContnetHeight - childNodeSize.height * scale
        let offset: CGPoint = defaultItemAlignment.shiftDirection
        let gridItemAlignmentOffset = CGPoint(
            x: max(0, deltaWidth) * offset.x,
            y: max(0, deltaHeight) * offset.y
        )

        // Get item's local center (based on pivot and alignment)
        let node: TransformNode = desc.children[index].childNodes[0] as! TransformNode
        let itemBounds = node.getBounds(parentSpace: true)
        let itemCenterOffset = CGPoint(x: itemBounds.midX, y: itemBounds.midY) * scale

        let localPositionX = gridSlotCenter.x - itemCenterOffset.x - gridItemAlignmentOffset.x
        let localPositionY = gridSlotCenter.y + itemCenterOffset.y + gridItemAlignmentOffset.y
        return (position: CGPoint(x: localPositionX, y: localPositionY), scale: scale)
    }

    fileprivate func calculateGridDescriptor() -> GridLayoutDescriptor? {
        let filteredChildren: [SCNNode] = container.childNodes.filter { ($0.childNodes.first is TransformNode) }
        let children: [SCNNode] = skipInvisibleItems ? filteredChildren.filter { ($0.childNodes[0] as! TransformNode).visible } : filteredChildren
        let nodes: [TransformNode] = children.map { $0.childNodes[0] as! TransformNode }
        guard !nodes.isEmpty else { return nil }

        let defaultItemPaddingSize = CGSize(width: defaultItemPadding.left + defaultItemPadding.right, height: defaultItemPadding.top + defaultItemPadding.bottom)
        var cellSizes: [CGSize] = []
        nodes.forEach { (node) in
            let nodeSize = node.getSize()
            node.updateLayout()
            cellSizes.append(nodeSize + defaultItemPaddingSize)
        }

        let itemsCount: Int = cellSizes.count
        let rowsCount: Int = (columns > 0) ? ((itemsCount - 1) / columns) + 1 : max(rows, 1)
        let columnsCount: Int = (columns > 0) ? columns : ((itemsCount - 1) / rowsCount) + 1

        let columnsBounds = getColumnsBounds(for: cellSizes, columnsCount: columnsCount, rowsCount: rowsCount, paddingSize: defaultItemPaddingSize)
        let rowsBounds = getRowsBounds(for: cellSizes, columnsCount: columnsCount, rowsCount: rowsCount, paddingSize: defaultItemPaddingSize)

        let totalWidth: CGFloat = columnsBounds.reduce(0) { $0 + $1.width }
        let totalHeight: CGFloat = rowsBounds.reduce(0) { $0 + $1.height }
        let estimatedSize = CGSize(width: totalWidth, height: totalHeight)
        let realWidth: CGFloat = (width > 0) ? width : estimatedSize.width
        let realHeight: CGFloat = (height > 0) ? height : estimatedSize.height
        let realSize = CGSize(width: realWidth, height: realHeight)
        return GridLayoutDescriptor(children: children, cellSizes: cellSizes, columns: columnsCount, rows: rowsCount, columnsBounds: columnsBounds, rowsBounds: rowsBounds, estimatedSize: estimatedSize, realSize: realSize)
    }

    fileprivate func getColumnsBounds(for cellSizes: [CGSize], columnsCount: Int, rowsCount: Int, paddingSize: CGSize) -> [(x: CGFloat, width: CGFloat)] {
        var columnsBounds: [(x: CGFloat, width: CGFloat)] = []
        var x: CGFloat = 0
        for c in 0..<columnsCount {
            var width: CGFloat = 0
            for r in 0..<rowsCount {
                let index: Int = r * columnsCount + c
                guard index < cellSizes.count else { break }
                let size = cellSizes[index]
                width = max(width, size.width)
            }
            columnsBounds.append((x: x, width: width))
            x += width
        }

        if width < 0.00001 {
            return columnsBounds
        }

        let paddingWidth = CGFloat(columnsCount) * paddingSize.width
        let preferredWidth: CGFloat = columnsBounds.reduce(0) { $0 + $1.width } - paddingWidth
        let scale: CGFloat = max(0, width - paddingWidth) / preferredWidth
        var distributedColumnsBounds: [(x: CGFloat, width: CGFloat)] = []
        x = 0
        for bounds in columnsBounds {
            let distributedWidth = (bounds.width - paddingSize.width) * scale + paddingSize.width
            distributedColumnsBounds.append((x: x, width: distributedWidth))
            x += distributedWidth
        }
        return distributedColumnsBounds
    }

    fileprivate func getRowsBounds(for cellSizes: [CGSize], columnsCount: Int, rowsCount: Int, paddingSize: CGSize) -> [(y: CGFloat, height: CGFloat)] {
        var rowsBounds: [(y: CGFloat, height: CGFloat)] = []
        var y: CGFloat = 0
        for r in 0..<rowsCount {
            var height: CGFloat = 0
            for c in 0..<columnsCount {
                let index: Int = r * columnsCount + c
                guard index < cellSizes.count else { break }
                let size = cellSizes[index]
                height = max(height, size.height)
            }
            rowsBounds.append((y: y, height: height))
            y += height
        }

        if height < 0.00001 {
            return rowsBounds
        }

        let paddingHeight = CGFloat(rowsCount) * paddingSize.height
        let preferredHeight: CGFloat = rowsBounds.reduce(0) { $0 + $1.height } - paddingHeight
        let scale: CGFloat = max(0, height - paddingHeight) / preferredHeight
        var distributedRowsBounds: [(y: CGFloat, height: CGFloat)] = []
        y = 0
        for bounds in rowsBounds {
            let distributedHeight = (bounds.height - paddingSize.height) * scale + paddingSize.height
            distributedRowsBounds.append((y: y, height: distributedHeight))
            y += distributedHeight
        }
        return distributedRowsBounds
    }

    fileprivate struct GridLayoutDescriptor {
        let children: [SCNNode]
        let cellSizes: [CGSize]
        let columns: Int
        let rows: Int
        let columnsBounds: [(x: CGFloat, width: CGFloat)]
        let rowsBounds: [(y: CGFloat, height: CGFloat)]
        let estimatedSize: CGSize
        let realSize: CGSize
    }
}
