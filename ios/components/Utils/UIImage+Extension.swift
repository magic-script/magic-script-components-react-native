//
//  UIImage+Extension.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 01/08/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
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
}
