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
import ARKit
import UIKit
import WebKit

@objc open class UiWebViewNode: UiNode {
    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }
    @objc var url: URL? {
        didSet {
            if let url = url {
                let request = URLRequest(url: url)
                self.webView.load(request)
                setNeedsLayout()
            }
        }
    }
    @objc var width: CGFloat = 0.0 {
        didSet { setNeedsLayout() }
    }
    @objc var height: CGFloat = 0.0 {
        didSet { setNeedsLayout() }
    }

    fileprivate var webView: WKWebView!
    fileprivate var webNode: SCNNode!

    @objc override func setupNode() {
        super.setupNode()

        assert(webNode == nil, "Node must not be initialized!")
        let planeGeometry = SCNPlane(width: 1.0, height: 1.0)
        planeGeometry.firstMaterial?.lightingModel = .constant
//        planeGeometry.firstMaterial?.diffuse.contents = UIColor.init(white: 1, alpha: 0)
        planeGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        webNode = SCNNode(geometry: planeGeometry)
        contentNode.addChildNode(webNode)

        self.webView = WKWebView(frame: CGRect(x: 0, y: 0, width: 480, height: 480))
        self.webView.navigationDelegate = self

//        DispatchQueue.main.async {
//            let view = UIWebView(frame: CGRect(x: 0, y: 0, width: 640, height: 480))
//            let request = URLRequest(url: URL(string: "https://www.apple.com")!)
//
//            view.loadRequest(request)
//            planeGeometry.firstMaterial?.diffuse.contents = view
//            self.webView = view
//        }
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let url = Convert.toFileURL(props["url"]) {
            self.url = url
        }

        if let width = Convert.toCGFloat(props["width"]) {
            self.width = width
        }

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }
    }

    @objc override func _calculateSize() -> CGSize {
        return CGSize(width: width, height: height)
    }

    @objc override func updateLayout() {
        if let planeGeometry = webNode.geometry as? SCNPlane {
//            let layer = self.webView?.layer
//            let image = self.webView.takeScreenshot()
            DispatchQueue.main.async() {
                planeGeometry.firstMaterial?.diffuse.contents = self.webView?.layer
            }
        }
    }
}

extension UiWebViewNode: UIWebViewDelegate, WKNavigationDelegate {
//    public func webViewDidFinishLoad(_ webView: UIWebView) {
//        setNeedsLayout()
//        layoutIfNeeded()
//    }

    public func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        setNeedsLayout()
        layoutIfNeeded()
    }
}

extension UIView {
    func takeScreenshot() -> UIImage {
        UIGraphicsBeginImageContextWithOptions(bounds.size, false, UIScreen.main.scale)
        drawHierarchy(in: bounds, afterScreenUpdates: true)
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return image ?? UIImage()
    }
}
