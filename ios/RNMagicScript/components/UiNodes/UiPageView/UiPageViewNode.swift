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

import SceneKit

@objc open class UiPageViewNode: UiNode {
    @objc var width: CGFloat {
        get { return pageLayout.width }
        set { pageLayout.width = newValue; setNeedsLayout() }
    }
    @objc var height: CGFloat {
        get { return pageLayout.height }
        set { pageLayout.height = newValue; setNeedsLayout() }
    }
    @objc var defaultPageAlignment: Alignment {
        get { return pageLayout.defaultItemAlignment }
        set { pageLayout.defaultItemAlignment = newValue; setNeedsLayout() }
    }
    @objc var defaultPagePadding: UIEdgeInsets {
        get { return pageLayout.defaultItemPadding }
        set { pageLayout.defaultItemPadding = newValue; setNeedsLayout() }
    }
    @objc var visiblePage: Int = -1 {
        didSet { setNeedsLayout() }
    }

    var pagesCount: Int { return pages.count }
    fileprivate var pages: [TransformNode] = []
    fileprivate var pageLayout = GridLayout()

    @objc func getPage(at index: Int) -> TransformNode? {
        guard index >= 0 && index < pages.count else { return nil }
        return pages[index]
    }

    @objc func getVisiblePage() -> TransformNode? {
        return getPage(at: visiblePage)
    }

    @objc override func setupNode() {
        super.setupNode()
        contentNode.addChildNode(pageLayout.container)
        pageLayout.columns = 1
        pageLayout.rows = 1
    }

    @objc override func hitTest(ray: Ray) -> BaseNode? {
        return pageLayout.hitTest(ray: ray, node: self)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let width = Convert.toCGFloat(props["width"]) {
            self.width = width
        }

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }

        if let defaultPageAlignment = Convert.toAlignment(props["defaultPageAlignment"]) {
            self.defaultPageAlignment = defaultPageAlignment
        }

        if let defaultPagePadding = Convert.toPadding(props["defaultPagePadding"]) {
            self.defaultPagePadding = defaultPagePadding
        }

        if let visiblePage = Convert.toInt(props["visiblePage"]) {
            self.visiblePage = visiblePage
        }
    }

    @discardableResult
    @objc override func addChild(_ child: TransformNode) -> Bool {
        pages.append(child)
        setNeedsLayout()
        return true
    }

    @objc override func removeChild(_ child: TransformNode) {
        if let index = pages.firstIndex(of: child) {
            pages.remove(at: index)
            setNeedsLayout()
        }
    }

    @objc override func _calculateSize() -> CGSize {
        return pageLayout.getSize()
    }

    @objc override func updateLayout() {
        updateVisiblePage(visiblePage)
        
        // Invoke getSize to make sure the grid's sizes are calcualted and cached in gridDesc.
        _ = getSize()
        pageLayout.updateLayout()
    }

    @objc override func setNeedsLayout() {
        super.setNeedsLayout()
        pageLayout.invalidate()
    }

    fileprivate func updateVisiblePage(_ pageIndex: Int) {
        if let currentPage = pageLayout.getItem(at: 0) {
            if let currentPageIndex = pages.firstIndex(of: currentPage) {
                if currentPageIndex == pageIndex {
                    // Do not change current page to the same page.
                    return
                }
            }

            // Hide current page
            pageLayout.removeItem(currentPage)
        }

        guard pageIndex >= 0 && pageIndex < pages.count else { return }

        let newPage = pages[pageIndex]
        pageLayout.addItem(newPage)
    }

    @objc override func enumerateTransformNodes(_ block: (TransformNode) -> Void) {
        pages.forEach { (page) in
            if page.parent == nil {
                page.enumerateTransformNodes(block)
            }
        }
        super.enumerateTransformNodes(block)
    }
}

