//
//  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

import Nimble

func urlForRelativePath(_ path: String) -> URL? {
    let url: URL = URL(fileURLWithPath: path)
    let resource: String = url.deletingPathExtension().lastPathComponent
    let ext: String = url.pathExtension
    let subdirectory: String? = nil//url.deletingLastPathComponent().path
    return Bundle.main.url(forResource: resource, withExtension: ext, subdirectory: subdirectory)
}
