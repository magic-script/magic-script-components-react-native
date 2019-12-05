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
    @objc var defaultItemAlignment: Alignment = Alignment.centerCenter {
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

    @objc func recalculate() {
        gridDescriptor = calculateGridDescriptor()
    }

    @objc func getSize() -> CGSize {
        if gridDescriptor == nil {
            recalculate()
        }

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
        let childNodeSize: CGSize = desc.cellSizes[index]
        let columnBounds = desc.columnsBounds[colId]
        let rowBounds = desc.rowsBounds[rowId]

        let columnContentWidth = columnBounds.width - (defaultItemPadding.left + defaultItemPadding.right)
        let rowContnetHeight = rowBounds.height - (defaultItemPadding.top + defaultItemPadding.bottom)
        let localCenter = CGPoint(x: columnBounds.x + defaultItemPadding.left + 0.5 * columnContentWidth,
                                  y: rowBounds.y + defaultItemPadding.top + 0.5 * rowContnetHeight)

        let deltaWidth: CGFloat = columnBounds.width - childNodeSize.width
        let deltaHeight: CGFloat = rowBounds.height - childNodeSize.height
        let offset: CGPoint = defaultItemAlignment.shiftDirection
        let gridItemAlignmentOffset = CGPoint(
            x: max(0, deltaWidth) * offset.x,
            y: max(0, deltaHeight) * offset.y
        )
        let scale: CGFloat = Math.clamp(min(columnBounds.width / childNodeSize.width, rowBounds.height / childNodeSize.height), 0, 1)

        // Ignore children nodes alignment
        let node: TransformNode = desc.children[index].childNodes[0] as! TransformNode
        let nodePos: SCNVector3 = node.contentNode.position
        let itemInternalAlignmentOffset = CGPoint(x: CGFloat(-nodePos.x), y: CGFloat(-nodePos.y))

        let itemBounds = node.getBounds()
        let itemBoundsCenterOffset = CGPoint(x: itemBounds.midX, y: itemBounds.midY)

        let localPositionX = localCenter.x + (itemInternalAlignmentOffset.x - gridItemAlignmentOffset.x) - itemBoundsCenterOffset.x
        let localPositionY = localCenter.y - (itemInternalAlignmentOffset.y - gridItemAlignmentOffset.y) - itemBoundsCenterOffset.y

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
            node.updateLayout()
            cellSizes.append(node.getSize() + defaultItemPaddingSize)
        }

        let itemsCount: Int = cellSizes.count
        let rowsCount: Int = (columns > 0) ? ((itemsCount - 1) / columns) + 1 : max(rows, 1)
        let columnsCount: Int = (columns > 0) ? columns : ((itemsCount - 1) / rowsCount) + 1

        let columnsBounds = getColumnsBounds(for: cellSizes, columnsCount: columnsCount, rowsCount: rowsCount)
        let rowsBounds = getRowsBounds(for: cellSizes, columnsCount: columnsCount, rowsCount: rowsCount)

        let totalWidth: CGFloat = columnsBounds.reduce(0) { $0 + $1.width }
        let totalHeight: CGFloat = rowsBounds.reduce(0) { $0 + $1.height }
        let estimatedSize = CGSize(width: totalWidth, height: totalHeight)
        let realWidth: CGFloat = (width > 0) ? width : estimatedSize.width
        let realHeight: CGFloat = (height > 0) ? height : estimatedSize.height
        let realSize = CGSize(width: realWidth, height: realHeight)
        return GridLayoutDescriptor(children: children, cellSizes: cellSizes, columns: columnsCount, rows: rowsCount, columnsBounds: columnsBounds, rowsBounds: rowsBounds, estimatedSize: estimatedSize, realSize: realSize)
    }
    
    fileprivate func getColumnsBounds(for cellSizes: [CGSize], columnsCount: Int, rowsCount: Int) -> [(x: CGFloat, width: CGFloat)] {
        guard width < 0.00001 else {
            var columnsBounds: [(x: CGFloat, width: CGFloat)] = []
            let columnWidth: CGFloat = width / CGFloat(columnsCount)
            for c in 0..<columnsCount {
                columnsBounds.append((x: CGFloat(c) * columnWidth, width: columnWidth))
            }
            return columnsBounds
        }

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

        return columnsBounds
    }

    fileprivate func getRowsBounds(for cellSizes: [CGSize], columnsCount: Int, rowsCount: Int) -> [(y: CGFloat, height: CGFloat)] {
        guard height < 0.00001 else {
            var rowsBounds: [(y: CGFloat, height: CGFloat)] = []
            let rowHeight: CGFloat = height / CGFloat(rowsCount)
            for r in 0..<rowsCount {
                rowsBounds.append((y: CGFloat(r) * rowHeight, height: rowHeight))
            }
            return rowsBounds
        }

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

        return rowsBounds
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
