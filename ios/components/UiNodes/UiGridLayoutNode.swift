//
//  UiGridLayout.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 24/07/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import SceneKit

@objc class UiGridLayoutNode: UiNode {

    @objc var columns: Int = 0 {
        didSet { setNeedsLayout() }
    }
    @objc var rows: Int = 0 {
        didSet { setNeedsLayout() }
    }
    //@objc var size: CGSize = CGSize.zero
    @objc var defaultItemAlignment: Alignment = Alignment.centerCenter {
        didSet { setNeedsLayout() }
    }
    @objc var defaultItemPadding: UIEdgeInsets = UIEdgeInsets.zero {
        didSet { setNeedsLayout() }
    }
    @objc var skipInvisibleItems: Bool = false {
        didSet { setNeedsLayout() }
    }

    fileprivate var gridDesc: GridLayoutDescriptor?

    @objc override func setupNode() {
        super.setupNode()
//        setDebugMode(true)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let columns = Convert.toInt(props["columns"]) {
            self.columns = columns
        }

        if let rows = Convert.toInt(props["rows"]) {
            self.rows = rows
        }

//        if let size = Convert.toCGSize(props["size"]) {
//            self.size = size
//        }
        if let defaultItemAlignment = Convert.toAlignment(props["defaultItemAlignment"]) {
            self.defaultItemAlignment = defaultItemAlignment
        }

        if let skipInvisibleItems = Convert.toBool(props["skipInvisibleItems"]) {
            self.skipInvisibleItems = skipInvisibleItems
        }
    }

    @objc override func addChild(_ child: TransformNode) {
        let proxyNode = SCNNode()
        proxyNode.name = child.name
        contentNode.addChildNode(proxyNode)
        proxyNode.addChildNode(child)
        setNeedsLayout()
    }

    @objc override func removeChild(_ child: TransformNode) {
        if let proxyNode = child.parent,
            let parent = proxyNode.parent, parent == contentNode {
            proxyNode.removeFromParentNode()
            child.removeFromParentNode()
            setNeedsLayout()
        }
    }

    @objc override func _calculateSize() -> CGSize {
        gridDesc = calculateGridDescriptor()
        return gridDesc?.size ?? CGSize.zero
    }

    @objc override func updateLayout() {
        // Invoke getSize to make sure the grid's sizes are calcualted and cached in gridDesc.
        let _ = getSize()
        guard let gridDesc = gridDesc else { return }

        // TODO: include padding
        let origin = CGPoint(x: -0.5 * gridDesc.size.width, y: 0.5 * gridDesc.size.height)
        for i in 0..<gridDesc.children.count {
            let pos: CGPoint = getLocalPositionForChild(at: i, desc: gridDesc)
            gridDesc.children[i].position = SCNVector3(origin.x + pos.x, origin.y - pos.y, CGFloat(position.z))
        }
    }

    fileprivate func getLocalPositionForChild(at index: Int, desc: GridLayoutDescriptor) -> CGPoint {
        let colId: Int = index % desc.columns
        let rowId: Int = index / desc.columns
        let childNodeSize: CGSize = desc.cellSizes[index]
        let columnBounds = desc.columnsBounds[colId]
        let rowBounds = desc.rowsBounds[rowId]

        let localCenter = CGPoint(x: columnBounds.x + 0.5 * columnBounds.width, y: rowBounds.y + 0.5 * rowBounds.height)
        let offset: CGPoint = defaultItemAlignment.offset
        let gridItemAlignmentOffset = CGPoint(
            x: (columnBounds.width - childNodeSize.width) * offset.x,
            y: (rowBounds.height - childNodeSize.height) * offset.y
        )

        // Ignore children nodes alignment
        let node: TransformNode = desc.children[index].childNodes[0] as! TransformNode
        let nodePos: SCNVector3 = node.contentNode.position
        let itemInternalAlignmentOffset = CGPoint(x: CGFloat(-nodePos.x), y: CGFloat(-nodePos.y))

        let localPositionX = localCenter.x + (itemInternalAlignmentOffset.x - gridItemAlignmentOffset.x)
        let localPositionY = localCenter.y - (itemInternalAlignmentOffset.y - gridItemAlignmentOffset.y)

        return CGPoint(x: localPositionX, y: localPositionY)
    }

    fileprivate func calculateGridDescriptor() -> GridLayoutDescriptor? {
        let filteredChildren: [SCNNode] = contentNode.childNodes.filter { ($0.childNodes.first is TransformNode) }
        let children: [SCNNode] = skipInvisibleItems ? filteredChildren.filter { ($0.childNodes[0] as! TransformNode).visible } : filteredChildren
        let nodes: [TransformNode] = children.map { $0.childNodes[0] as! TransformNode }
        guard !nodes.isEmpty else { return nil }

        var cellSizes: [CGSize] = []
        nodes.forEach { (node) in
            node.updateLayout()
            cellSizes.append(node.getSize())
        }

        let itemsCount: Int = cellSizes.count
        let rowsCount: Int = (columns > 0) ? ((itemsCount - 1) / columns) + 1 : max(rows, 1)
        let columnsCount: Int = (columns > 0) ? columns : ((itemsCount - 1) / rowsCount) + 1

        let columnsBounds = getColumnsBounds(for: cellSizes, columnsCount: columnsCount, rowsCount: rowsCount)
        let rowsBounds = getRowsBounds(for: cellSizes, columnsCount: columnsCount, rowsCount: rowsCount)

        let totalWidth: CGFloat = columnsBounds.reduce(0) { $0 + $1.width }
        let totalHeight: CGFloat = rowsBounds.reduce(0) { $0 + $1.height }
        let size = CGSize(width: totalWidth, height: totalHeight)
        return GridLayoutDescriptor(children: children, cellSizes: cellSizes, columns: columnsCount, rows: rowsCount, columnsBounds: columnsBounds, rowsBounds: rowsBounds, size: size)
    }
}

// Helpers
extension UiGridLayoutNode {
    fileprivate func getColumnsBounds(for cellSizes: [CGSize], columnsCount: Int, rowsCount: Int) -> [(x: CGFloat, width: CGFloat)] {
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
        let size: CGSize
    }
}
