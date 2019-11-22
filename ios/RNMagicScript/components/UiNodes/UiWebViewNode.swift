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
import SafariServices

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
    @objc var action: WebViewAction = .reload {
        didSet {
            switch action {
            case .back:
                webView.goBack()
            case .forward:
                webView.goForward()
            case .reload:
                webView.reload()
            }
        }
    }
    @objc var scrollBy: CGSize = CGSize.zero

    fileprivate var webView: WKWebView!
    fileprivate var webNode: SCNNode!
    fileprivate var lastScreenshot: UIImage?

    @objc override var canHaveFocus: Bool {
        return enabled
    }

    @objc override func enterFocus() {
        super.enterFocus()
        guard hasFocus else { return }

        presentSafariViewController()
    }

    deinit {
        removeWebViewFromHierarchy()
    }

    @objc override func setupNode() {
        super.setupNode()

        assert(webNode == nil, "Node must not be initialized!")
        let planeGeometry = SCNPlane(width: 1.0, height: 1.0)
        planeGeometry.firstMaterial?.lightingModel = .constant
        planeGeometry.firstMaterial?.diffuse.contents = UIColor.darkGray
        planeGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        webNode = SCNNode(geometry: planeGeometry)
        contentNode.addChildNode(webNode)

        webView = WKWebView(frame: CGRect(x: 0, y: 0, width: 0, height: 0))
        webView.navigationDelegate = self
        webView.translatesAutoresizingMaskIntoConstraints = false
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

        if let action = Convert.toWebViewAction(props["action"]) {
            self.action = action
        }

        if let scrollBy = Convert.toCGSize(props["scrollBy"]) {
            self.scrollBy = scrollBy
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
    }

    fileprivate func updateScreenshot() {
        updateWebViewFrame()
        lastScreenshot = (width > 0 && height > 0) ? webView.takeScreenshot() : nil
    }

    fileprivate func presentSafariViewController() {
        guard let url = url else { return }
        let safariVC = SFSafariViewController(url: url)
        safariVC.dismissButtonStyle = .close
        safariVC.delegate = self
        UIApplication.shared.keyWindow?.rootViewController?.present(safariVC, animated: true, completion: nil)
    }

    fileprivate func addWebViewToHierarchy() {
//        if let rootView = UIApplication.shared.keyWindow?.rootViewController?.view, webView.superview == nil {
//            rootView.addSubview(webView)
//            rootView.sendSubviewToBack(webView)
//            webView.setNeedsLayout()
//            webView.layoutIfNeeded()
//        }
    }

    fileprivate func removeWebViewFromHierarchy() {
//        webView.removeFromSuperview()
    }

    @objc fileprivate func reloadScreenshot(after delay: TimeInterval, removeFromHierarchy: Bool = false) {
        let deadlineTime = DispatchTime.now() + .milliseconds(Int(delay * 1000))
        DispatchQueue.main.asyncAfter(deadline: deadlineTime) { [weak self] in
            self?.updateScreenshot()
            self?.setNeedsLayout()
            self?.layoutIfNeeded()
            if removeFromHierarchy {
                self?.removeWebViewFromHierarchy()
            }
        }
    }
}

extension UiWebViewNode: WKNavigationDelegate {
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
        addWebViewToHierarchy()
        updateWebViewFrame()
    }

    public func webView(_ webView: WKWebView, didReceiveServerRedirectForProvisionalNavigation navigation: WKNavigation!) {
        print("webView:didReceiveServerRedirectForProvisionalNavigation")
    }

    public func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        print("webView:didCommit:navigation")
    }

    public func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        reloadScreenshot(after: 0.1, removeFromHierarchy: true)
        print("webView:didFailProvisionalNavigation:withError: \(error)")
    }

    public func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        reloadScreenshot(after: 0.1)
        reloadScreenshot(after: 1.5)
        reloadScreenshot(after: 3.0, removeFromHierarchy: true)
        print("webView:didFinish:navigation")
    }

    public func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        reloadScreenshot(after: 0.1, removeFromHierarchy: true)
        print("webView:didFail:navigation:withError: \(error)")
    }

    public func webViewWebContentProcessDidTerminate(_ webView: WKWebView) {
        reloadScreenshot(after: 0.1, removeFromHierarchy: true)
        print("webViewWebContentProcessDidTerminate")
    }
}

extension UiWebViewNode: SFSafariViewControllerDelegate {
    public func safariViewControllerDidFinish(_ controller: SFSafariViewController) {
        leaveFocus()
    }
}
