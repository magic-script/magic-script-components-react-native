//
//  Bundle.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 06/08/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import Foundation

extension Bundle {
    static func resourcesBundle() -> Bundle? {
        guard let resourcesBundleURL = Bundle.main.url(forResource: "RNMagicScriptResources", withExtension: "bundle") else { return nil }
        return Bundle(url: resourcesBundleURL)
    }
}
