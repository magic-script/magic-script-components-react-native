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

@objc open class UiScrollBarNode: UiNode {

    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }

    // Gets an indication between 0 and 1 for the size of the thumb in respect to the track.
    // It returns 1 to mean the thumb is as long as its track.
    // It returns a value of .5f to mean the thumb is half as long as its track.
    @objc var thumbSize: CGFloat = 0.5 {
        didSet { setNeedsLayout() }
    }

    // Gets an indication between 0 and 1 for the position of the thumb along the track.
    // It returns 0 to mean the thumb is at the left/upper most possible position along the track.
    // It returns 1 to mean the thumb is at the right/bottom most possible position along the track.
    @objc var thumbPosition: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }

    @objc override func setupNode() {
        super.setupNode()
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let thumbSize = Convert.toCGFloat(props["thumbSize"]) {
            self.thumbSize = thumbSize
        }
    }

    @objc override func _calculateSize() -> CGSize {
        return CGSize.zero
    }

    @objc override func updateLayout() {

    }
}
