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

        if let skipInvisibleItems = Convert.toBool(props["skipInvisibleItems"]) {
            self.skipInvisibleItems = skipInvisibleItems
        }
    }

    @objc override func addChild(_ child: TransformNode) {
        let proxyNode = SCNNode()
        proxyNode.name = child.name
        addChildNode(proxyNode)
        proxyNode.addChildNode(child)
        setNeedsLayout()
    }

    @objc override func removeChild(_ child: TransformNode) {
        if let proxyNode = child.parent,
            let parent = proxyNode.parent, parent == self {
            proxyNode.removeFromParentNode()
            child.removeFromParentNode()
            setNeedsLayout()
        }
    }

    @objc override func getSize() -> CGSize {
        let filteredChildren: [SCNNode] = childNodes.filter { ($0.childNodes.first is TransformNode) }
        let children: [SCNNode] = skipInvisibleItems ? filteredChildren.filter { ($0.childNodes[0] as! TransformNode).visible } : filteredChildren
        let nodes: [TransformNode] = children.map { $0.childNodes[0] as! TransformNode }
        guard !nodes.isEmpty else { return CGSize.zero }

        var cellSizes: [CGSize] = []
        nodes.forEach { (node) in
            node.updateLayout()
            cellSizes.append(node.getSize())
        }

        let itemsCount: Int = cellSizes.count
        let rowsCount: Int = (columns > 0) ? itemsCount / columns : 1
        let columnsCount: Int = itemsCount / rowsCount

        let columnsBounds = getColumnsBounds(for: cellSizes, columnsCount: columnsCount, rowsCount: rowsCount)
        let rowsBounds = getRowsBounds(for: cellSizes, columnsCount: columnsCount, rowsCount: rowsCount)

        let totalWidth: CGFloat = columnsBounds.reduce(0) { (result, bounds) -> CGFloat in
            return result + bounds.width
        }

        let totalHeight: CGFloat = rowsBounds.reduce(0) { (result, bounds) -> CGFloat in
            return result + bounds.height
        }

        return CGSize(width: totalWidth, height: totalHeight)
    }

    @objc override func updateLayout() {
        let filteredChildren: [SCNNode] = childNodes.filter { ($0.childNodes.first is TransformNode) }
        let children: [SCNNode] = skipInvisibleItems ? filteredChildren.filter { ($0.childNodes[0] as! TransformNode).visible } : filteredChildren
        let nodes: [TransformNode] = children.map { $0.childNodes[0] as! TransformNode }
        guard !nodes.isEmpty else { return }

        var cellSizes: [CGSize] = []
        nodes.forEach { (node) in
            node.updateLayout()
            cellSizes.append(node.getSize())
        }

        let itemsCount: Int = cellSizes.count
        let rowsCount: Int = (columns > 0) ? itemsCount / columns : 1
        let columnsCount: Int = itemsCount / rowsCount

        let columnsBounds = getColumnsBounds(for: cellSizes, columnsCount: columnsCount, rowsCount: rowsCount)
        let rowsBounds = getRowsBounds(for: cellSizes, columnsCount: columnsCount, rowsCount: rowsCount)

        // TODO: include padding
        // TODO: include item alignment
        let minX: CGFloat = -0.5 * (columnsBounds.last!.x + columnsBounds.last!.width)
        let minY: CGFloat = -0.5 * (rowsBounds.last!.y + rowsBounds.last!.height)
        for i in 0..<cellSizes.count {
            let colId: Int = i % columnsCount
            let rowId: Int = i / columnsCount
            let colBound = columnsBounds[colId];
            let rowBound = rowsBounds[rowId];
            let nodeSize = cellSizes[i]
            let x: CGFloat = minX + colBound.x + 0.5 * (colBound.width - nodeSize.width)
            let y: CGFloat = minY + rowBound.y + 0.5 * (rowBound.height - nodeSize.height)
//            print("[\(colId),\(rowId)] = \(nodeSize) :: [\(colBound.width), \(rowBound.height)")
            children[i].position = SCNVector3(x, y, CGFloat(position.z))
        }
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
}
