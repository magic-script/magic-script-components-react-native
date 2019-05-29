//
//  MLImageNode.swift
//  SceneKitDemo
//
//  Created by Pawel Leszkiewicz on 23/05/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit

@objc class MLImageNode: SCNNode {

    @objc var image: UIImage? {
        get { return planeGeometry.materials.first?.diffuse.contents as? UIImage }
        set { planeGeometry.materials.first?.diffuse.contents = newValue }
    }
    
    @objc var size: CGSize {
        get { return CGSize(width: planeGeometry.width, height: planeGeometry.height) }
        set { planeGeometry.width = newValue.width; planeGeometry.height = newValue.height; updateImageSize() }
    }

    fileprivate var planeGeometry: SCNPlane!

    @objc override init() {
        super.init()
        setupNode()
    }

    @objc required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupNode()
    }

    fileprivate func setupNode() {
        planeGeometry = SCNPlane(width: 1, height: 1)
        addChildNode(SCNNode(geometry: planeGeometry))
        updateImageSize()
    }

    fileprivate func updateImageSize() {
        setBBox(visible: true, forceUpdate: true)
    }
}
