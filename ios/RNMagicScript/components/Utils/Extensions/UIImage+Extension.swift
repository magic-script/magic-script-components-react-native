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

import UIKit

extension UIImage {
    static func image(from colors: [UIColor], size: Int) -> UIImage? {
        let tileSize = CGSize(width: size, height: size)
        let canvasWidth: CGFloat = CGFloat(colors.count) * tileSize.width
        let canvasHeight: CGFloat = tileSize.height
        UIGraphicsBeginImageContextWithOptions(CGSize(width: canvasWidth, height: canvasHeight), true, 0.0)
        let context = UIGraphicsGetCurrentContext()

        var rect = CGRect(origin: CGPoint.zero, size: tileSize)
        for c in colors {
            context?.setFillColor(c.cgColor)
            context?.fill(rect)
            rect = rect.offsetBy(dx: tileSize.width, dy: 0)
        }

        let img: UIImage? = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return img
    }
    
    static func gradientImage(withSize size: CGSize, colors: [CGColor]) -> UIImage {
        let gradientLayer = CAGradientLayer()
        gradientLayer.type = .axial
        gradientLayer.startPoint = CGPoint(x: 0.0, y: 0.5)
        gradientLayer.endPoint = CGPoint(x: 1.0, y: 0.5)
        gradientLayer.frame = CGRect(x: 0.0, y: 0.0, width: size.width, height: size.height)
        gradientLayer.colors = colors
        
        UIGraphicsBeginImageContextWithOptions(gradientLayer.bounds.size, true, 0)
        gradientLayer.render(in: UIGraphicsGetCurrentContext()!)
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return image!
    }
}
