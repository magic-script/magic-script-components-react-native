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

#if os(iOS) || os(tvOS) || os(watchOS)
import UIKit.UIImage
typealias Image = UIImage
#elseif os(OSX)
import AppKit.NSImage
typealias Image = NSImage
#endif

public enum ImageAsset: String {
    case dropdownListBackground = "dropdown_list_background"
    case spinner = "spinner"
    case spinnerProgressBegin = "spinner_begin"
    case spinnerProgressEnd = "spinner_end"
    case textEditBackground = "text_edit_background"
    case toggleOn = "toggle_on"
    case toggleOff = "toggle_off"

    var image: Image {
        let bundle = Bundle.resourcesBundle()!
        #if os(iOS) || os(tvOS)
        let image = Image(named: rawValue, in: bundle, compatibleWith: UIScreen.main.traitCollection)
        #elseif os(OSX)
        let image = bundle.image(forResource: rawValue)
        #elseif os(watchOS)
        let image = Image(named: rawValue)
        #endif
        guard let result = image else { fatalError("Unable to load image \"\(rawValue)\".") }
        return result
    }
}

extension Image {
    convenience init!(asset: ImageAsset) {
        #if os(iOS) || os(tvOS)
        let bundle = Bundle.resourcesBundle()
        self.init(named: asset.rawValue, in: bundle, compatibleWith: nil)
        #elseif os(OSX) || os(watchOS)
        self.init(named: asset.rawValue)
        #endif
    }
}
