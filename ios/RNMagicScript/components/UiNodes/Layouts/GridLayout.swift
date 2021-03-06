//
//  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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
    var alignmentByIndex: [Int : Alignment] = [:] {
        didSet { invalidate() }
    }
    @objc var paddingByIndex: [Int : UIEdgeInsets] = [:] {
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

    func hitTest(ray: Ray, node: UiNode) -> HitTestResult? {
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
        guard elementIndex < gridDescriptor.children.count else { return (node: node, point: hitPoint) }
        let hitNode = gridDescriptor.children[elementIndex].childNodes[0] as? TransformNode
        return hitNode?.hitTest(ray: ray) ?? (node: node, point: hitPoint)
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
        let direction = getFlowDirection()
        for i in 0..<gridDescriptor.children.count {
            let result = getLocalPositionAndScaleForChild(at: i, desc: gridDescriptor, flowDirection: direction)
            gridDescriptor.children[i].position = SCNVector3(origin.x + result.position.x, origin.y - result.position.y, 0)
            gridDescriptor.children[i].scale = SCNVector3(result.scale, result.scale, 1)
        }
    }
}

// MARK: - Helpers
extension GridLayout {
    enum FlowDirection {
        case horizontal
        case vertical
    }
    
    typealias HorizontalBounds = (x: CGFloat, width: CGFloat, paddingWidth: CGFloat)
    typealias VerticalBounds = (y: CGFloat, height: CGFloat, paddingHeight: CGFloat)

    func getFlowDirection() -> FlowDirection {
        return (columns > 0) ? .vertical : .horizontal
    }
    
    func getCurrentColumns() -> Int {
        if columns > 0 { return columns }
        
        let itemsCount = getCurrentItemNodes().count
        let rowsCount: Int = max(rows, 1)
        return ((itemsCount - 1) / rowsCount) + 1
    }
    
    func getCurrentRows() -> Int {
        if columns == 0 { return max(rows, 1) }

        let itemsCount = getCurrentItemNodes().count
        return ((itemsCount - 1) / columns) + 1
    }
    
    fileprivate func getAlignmentForItem(at index: Int) -> Alignment {
        return alignmentByIndex[index] ?? defaultItemAlignment
    }
    
    fileprivate func getPaddingForItem(at index: Int) -> UIEdgeInsets {
        return paddingByIndex[index] ?? defaultItemPadding
    }
    
    fileprivate func getPaddingSizeForItem(at index: Int) -> CGSize {
        let padding = getPaddingForItem(at: index)
        return getPaddingSizeForPadding(padding)
    }
    
    fileprivate func getPaddingSizeForPadding(_ padding: UIEdgeInsets) -> CGSize {
        return CGSize(width: padding.left + padding.right, height: padding.top + padding.bottom)
    }

    fileprivate func getLocalPositionAndScaleForChild(at index: Int, desc: GridLayoutDescriptor, flowDirection: FlowDirection) -> (position: CGPoint, scale: CGFloat) {
        let colId: Int = (flowDirection == .vertical) ? index % desc.columns : index / desc.rows
        let rowId: Int = (flowDirection == .vertical) ? index / desc.columns : index % desc.rows
        let cellSize: CGSize = desc.cellSizes[index]
        let itemPadding = getPaddingForItem(at: index)
        let itemPaddingSize = getPaddingSizeForPadding(itemPadding)
        let childNodeSize: CGSize = cellSize - itemPaddingSize
        let columnBounds = desc.columnsBounds[colId]
        let rowBounds = desc.rowsBounds[rowId]

        let columnContentWidth = columnBounds.width - itemPaddingSize.width
        let rowContnetHeight = rowBounds.height - itemPaddingSize.height
        let gridSlotCenter = CGPoint(x: columnBounds.x + itemPadding.left + 0.5 * columnContentWidth,
                                     y: rowBounds.y + itemPadding.top + 0.5 * rowContnetHeight)

        let scale: CGFloat = Math.clamp(min(columnContentWidth / childNodeSize.width, rowContnetHeight / childNodeSize.height), 0, 1)

        let deltaWidth: CGFloat = columnContentWidth - childNodeSize.width * scale
        let deltaHeight: CGFloat = rowContnetHeight - childNodeSize.height * scale
        let offset: CGPoint = getAlignmentForItem(at: index).shiftDirection
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
    
    fileprivate func getCurrentItemNodes() -> [SCNNode] {
        let filteredChildren: [SCNNode] = container.childNodes.filter { ($0.childNodes.first is TransformNode) }
        let children: [SCNNode] = skipInvisibleItems ? filteredChildren.filter { ($0.childNodes[0] as! TransformNode).visible } : filteredChildren
        return children
    }

    fileprivate func calculateGridDescriptor() -> GridLayoutDescriptor? {
        let children: [SCNNode] = getCurrentItemNodes()
        let nodes: [TransformNode] = children.map { $0.childNodes[0] as! TransformNode }
        guard nodes.isNotEmpty else { return nil }

        var cellSizes: [CGSize] = []
        for (index, node) in nodes.enumerated() {
            let nodeSize = node.getSize()
            node.updateLayout()
            let paddingSize = getPaddingSizeForItem(at: index)
            cellSizes.append(nodeSize + paddingSize)
        }

        let itemsCount: Int = cellSizes.count
        let rowsCount: Int = (columns > 0) ? ((itemsCount - 1) / columns) + 1 : max(rows, 1)
        let columnsCount: Int = (columns > 0) ? columns : ((itemsCount - 1) / rowsCount) + 1
        let direction = getFlowDirection()
        let columnsBounds = getColumnsBounds(for: cellSizes, columnsCount: columnsCount, rowsCount: rowsCount, flowDirection: direction)
        let rowsBounds = getRowsBounds(for: cellSizes, columnsCount: columnsCount, rowsCount: rowsCount, flowDirection: direction)

        let totalWidth: CGFloat = columnsBounds.reduce(0) { $0 + $1.width }
        let totalHeight: CGFloat = rowsBounds.reduce(0) { $0 + $1.height }
        let estimatedSize = CGSize(width: totalWidth, height: totalHeight)
        let realWidth: CGFloat = (width > 0) ? width : estimatedSize.width
        let realHeight: CGFloat = (height > 0) ? height : estimatedSize.height
        let realSize = CGSize(width: realWidth, height: realHeight)
        return GridLayoutDescriptor(children: children, cellSizes: cellSizes, columns: columnsCount, rows: rowsCount, columnsBounds: columnsBounds, rowsBounds: rowsBounds, estimatedSize: estimatedSize, realSize: realSize)
    }

    fileprivate func getColumnsBounds(for cellSizes: [CGSize], columnsCount: Int, rowsCount: Int, flowDirection: FlowDirection) -> [HorizontalBounds] {
        var columnsBounds: [HorizontalBounds] = []
        var x: CGFloat = 0
        for c in 0..<columnsCount {
            var width: CGFloat = 0
            var paddingWidth: CGFloat = 0
            for r in 0..<rowsCount {
                let index: Int = (flowDirection == .vertical) ? (r * columnsCount + c) : (c * rowsCount + r)
                guard index < cellSizes.count else { break }
                let size = cellSizes[index]
                width = max(width, size.width)
                let padding = getPaddingForItem(at: index)
                paddingWidth = max(paddingWidth, padding.left + padding.right)
            }
            columnsBounds.append((x: x, width: width, paddingWidth: paddingWidth))
            x += width
        }

        if width < 0.00001 {
            return columnsBounds
        }

        let totalPaddingWidth: CGFloat = columnsBounds.reduce(0) { $0 + $1.paddingWidth }
        let preferredWidth: CGFloat = columnsBounds.reduce(0) { $0 + $1.width } - totalPaddingWidth
        let scale: CGFloat = max(0, width - totalPaddingWidth) / preferredWidth
        var distributedColumnsBounds: [HorizontalBounds] = []
        x = 0
        for bounds in columnsBounds {
            let distributedWidth = (bounds.width - bounds.paddingWidth) * scale + bounds.paddingWidth
            distributedColumnsBounds.append((x: x, width: distributedWidth, paddingWidth: bounds.paddingWidth))
            x += distributedWidth
        }
        return distributedColumnsBounds
    }

    fileprivate func getRowsBounds(for cellSizes: [CGSize], columnsCount: Int, rowsCount: Int, flowDirection: FlowDirection) -> [VerticalBounds] {
        var rowsBounds: [VerticalBounds] = []
        var y: CGFloat = 0
        for r in 0..<rowsCount {
            var height: CGFloat = 0
            var paddingHeight: CGFloat = 0
            for c in 0..<columnsCount {
                let index: Int = (flowDirection == .vertical) ? (r * columnsCount + c) : (c * rowsCount + r)
                guard index < cellSizes.count else { break }
                let size = cellSizes[index]
                height = max(height, size.height)
                let padding = getPaddingForItem(at: index)
                paddingHeight = max(paddingHeight, padding.top + padding.bottom)
            }
            rowsBounds.append((y: y, height: height, paddingHeight: paddingHeight))
            y += height
        }

        if height < 0.00001 {
            return rowsBounds
        }

        let totalPaddingHeight: CGFloat = rowsBounds.reduce(0) { $0 + $1.paddingHeight }
        let preferredHeight: CGFloat = rowsBounds.reduce(0) { $0 + $1.height } - totalPaddingHeight
        let scale: CGFloat = max(0, height - totalPaddingHeight) / preferredHeight
        var distributedRowsBounds: [VerticalBounds] = []
        y = 0
        for bounds in rowsBounds {
            let distributedHeight = (bounds.height - bounds.paddingHeight) * scale + bounds.paddingHeight
            distributedRowsBounds.append((y: y, height: distributedHeight, paddingHeight: bounds.paddingHeight))
            y += distributedHeight
        }
        return distributedRowsBounds
    }

    fileprivate struct GridLayoutDescriptor {
        let children: [SCNNode]
        let cellSizes: [CGSize]
        let columns: Int
        let rows: Int
        let columnsBounds: [HorizontalBounds]
        let rowsBounds: [VerticalBounds]
        let estimatedSize: CGSize
        let realSize: CGSize
    }
}
