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
    static fileprivate let maxSnaptshotSize: CGFloat = 1024
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
        didSet { updateWebViewFrame(); setNeedsLayout() }
    }
    @objc var height: CGFloat = 0.0 {
        didSet { updateWebViewFrame(); setNeedsLayout() }
    }

    fileprivate var webView: WKWebView!
    fileprivate var webNode: SCNNode!
    fileprivate var lastScreenshot: UIImage?

    @objc override func setupNode() {
        super.setupNode()

        assert(webNode == nil, "Node must not be initialized!")
        let planeGeometry = SCNPlane(width: 1.0, height: 1.0)
        planeGeometry.firstMaterial?.lightingModel = .constant
//        planeGeometry.firstMaterial?.diffuse.contents = UIColor.init(white: 1, alpha: 0)
        planeGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        webNode = SCNNode(geometry: planeGeometry)
        contentNode.addChildNode(webNode)

        self.webView = WKWebView(frame: CGRect(x: 0, y: 0, width: 0, height: 0))
        self.webView.navigationDelegate = self
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
            planeGeometry.width = width
            planeGeometry.height = height
            planeGeometry.firstMaterial?.diffuse.contents = lastScreenshot
        }
    }

    fileprivate func updateWebViewFrame() {
        guard width > 0, height > 0 else { return }
        let factor: CGFloat = UiWebViewNode.maxSnaptshotSize / max(width, height)
        webView.frame = CGRect(x: 0, y: 0, width: round(factor * width), height: round(factor * height))
        print("updateWebViewFrame: \(webView.frame.size)")
    }

    fileprivate func updateScreenshot() {
        updateWebViewFrame()
        lastScreenshot = (width > 0 && height > 0) ? webView.takeScreenshot() : nil
        print("Screenshot taken: \(lastScreenshot)")
    }
}

extension UiWebViewNode: UIWebViewDelegate, WKNavigationDelegate {
    public func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        print("webView:decidePolicyForNavigationAction:decisionHandler")
        decisionHandler(.allow)
    }

    @available(iOS 13.0, *)
    public func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, preferences: WKWebpagePreferences, decisionHandler: @escaping (WKNavigationActionPolicy, WKWebpagePreferences) -> Void) {
        print("webView:decidePolicyFor:preferences")
        let preferences = WKWebpagePreferences()
        preferences.preferredContentMode = .recommended
        decisionHandler(.allow, preferences)
    }

    public func webView(_ webView: WKWebView, decidePolicyFor navigationResponse: WKNavigationResponse, decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
        print("webView:decidePolicyForNavigationResponse:decisionHandler")
        decisionHandler(.allow)
    }

    public func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        print("webView:didStartProvisionalNavigation")
    }

    public func webView(_ webView: WKWebView, didReceiveServerRedirectForProvisionalNavigation navigation: WKNavigation!) {
        print("webView:didReceiveServerRedirectForProvisionalNavigation")
    }

    public func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        print("webView:didCommit:navigation")
    }

    public func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        print("webView:didFailProvisionalNavigation:withError: \(error)")
    }

    public func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        updateScreenshot()
        setNeedsLayout()
        layoutIfNeeded()
        print("webView:didFinish:navigation")
    }

    public func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        print("webView:didFail:navigation:withError: \(error)")
    }

    public func webViewWebContentProcessDidTerminate(_ webView: WKWebView) {
        print("webViewWebContentProcessDidTerminate")
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
