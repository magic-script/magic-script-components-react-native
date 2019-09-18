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

import Foundation

//sourcery: AutoMockable
protocol Downloading {
    func downloadModel(modelURL: URL, completion: @escaping (_ localURL: URL?) -> (Void))
}

class ModelDownloader: Downloading {

    var fileManager: FileManaging! = FileManager.default
    var urlSession: URLSessioning! = URLSession.shared
    fileprivate var downloadTask: URLSessionDownloadingTask?
    fileprivate var dataTask: URLSessionDataTask?

    func downloadModel(modelURL: URL, completion: @escaping (_ localURL: URL?) -> (Void)) {
        downloadTask?.cancel()

        if modelURL.isFileURL {
            completion(modelURL)
            return
        }

        downloadTask = urlSession.downloadTask_(with: modelURL) { [weak self] (tmpURL, response, error) in
            self?.downloadTask = nil
            guard let tmpURL = tmpURL else {
                DispatchQueue.main.async() { completion(nil) }
                return
            }

            do {
                guard let strongSelf = self else { return }
                let documentsURL = strongSelf.fileManager.urls(for: .cachesDirectory, in: .userDomainMask).first!
                let localURL: URL = documentsURL.appendingPathComponent(modelURL.lastPathComponent)
                try? strongSelf.fileManager.removeItem(at: localURL)
                try strongSelf.fileManager.copyItem(at: tmpURL, to: localURL)
                DispatchQueue.main.async() { completion(localURL) }
            } catch (let writeError) {
                print("Error writing model file: \(writeError)")
                DispatchQueue.main.async() { completion(nil) }
            }
        }
        downloadTask?.resume()
    }
}

