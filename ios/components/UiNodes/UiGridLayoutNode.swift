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
        didSet { layoutNeeded = true }
    }
    @objc var rows: Int = 0 {
        didSet { layoutNeeded = true }
    }
    //@objc var size: CGSize = CGSize.zero
    @objc var defaultItemAlignment: Int = 0 {
        didSet { layoutNeeded = true }
    }
    @objc var defaultItemPadding: UIEdgeInsets = UIEdgeInsets.zero {
        didSet { layoutNeeded = true }
    }
    @objc var skipInvisibleItems: Bool = false {
        didSet { layoutNeeded = true }
    }

    fileprivate var layoutNeeded: Bool = false

    @objc override func setupNode() {
        super.setupNode()
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

        updateLayout()
    }

    @objc override func addChild(_ child: TransformNode) {
        let proxyNode = SCNNode()
        proxyNode.name = child.name
        addChildNode(proxyNode)
        proxyNode.addChildNode(child)
        layoutNeeded = true

    }

    @objc override func removeChild(_ child: TransformNode) {
        if let proxyNode = child.parent,
            let parent = proxyNode.parent, parent == self {
            proxyNode.removeFromParentNode()
            child.removeFromParentNode()
            layoutNeeded = true
        }
    }

    @objc override func updateLayout() {
        guard layoutNeeded else { return }
        layoutNeeded = false

        guard !childNodes.isEmpty else { return }

        var cellSizes: [CGSize] = []
        childNodes.forEach { (child) in
            let node: TransformNode = child.childNodes[0] as! TransformNode
            node.updateLayout()
            cellSizes.append(node.getSize())
        }

        let itemsCount: Int = cellSizes.count
        let rowsCount: Int = (columns > 0) ? itemsCount / columns : 1
        let columnsCount: Int = itemsCount / rowsCount

        let columnsWidth = getColumnsWidth(for: cellSizes, columnsCount: columnsCount, rowsCount: rowsCount)
        let rowsHeight = getRowsHeight(for: cellSizes, columnsCount: columnsCount, rowsCount: rowsCount)

        // TODO: include padding
        // TODO: include item alignment
        let minX: CGFloat = 0
        let minY: CGFloat = 0
        let gridWidth: CGFloat = columnsWidth.reduce(0, +)
        let gridHeight: CGFloat = rowsHeight.reduce(0, +)
        for i in 0..<cellSizes.count {
            let colId: Int = i % columnsCount
            let rowId: Int = i / columnsCount
            let x: CGFloat = minX + 0.5 * columnsWidth[colId] // TODO: still missing offset of all pevious columns
            let y: CGFloat = minY + 0.5 * rowsHeight[rowId] // TODO: still missing offset of all pevious rows
            childNodes[i].position = SCNVector3(x, y, CGFloat(position.z))
        }
    }
}

// Helpers
extension UiGridLayoutNode {
    fileprivate func getColumnsWidth(for cellSizes: [CGSize], columnsCount: Int, rowsCount: Int) -> [CGFloat] {
        var columnsWidth: [CGFloat] = []
        for c in 0..<columnsCount {
            var width: CGFloat = 0
            for r in 0..<rowsCount {
                let index: Int = r * columnsCount + c
                guard index < cellSizes.count else { break }
                let size = cellSizes[index]
                width = max(width, size.width)
            }
            columnsWidth.append(width)
        }

        return columnsWidth
    }

    fileprivate func getRowsHeight(for cellSizes: [CGSize], columnsCount: Int, rowsCount: Int) -> [CGFloat] {
        var rowsHeight: [CGFloat] = []
        for r in 0..<rowsCount {
            var height: CGFloat = 0
            for c in 0..<columnsCount {
                let index: Int = r * columnsCount + c
                guard index < cellSizes.count else { break }
                let size = cellSizes[index]
                height = max(height, size.height)
            }
            rowsHeight.append(height)
        }

        return rowsHeight
    }
}
