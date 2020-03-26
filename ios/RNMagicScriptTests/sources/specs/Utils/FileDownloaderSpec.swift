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

import Quick
import Nimble
@testable import RNMagicScriptHostApplication

class FileDownloaderSpec: QuickSpec {
    override func spec() {
        describe("FileDownloader") {
            var fileManagerMock: FileManagingMock!
            var urlSessionMock: URLSessioningMock!
            var sut: FileDownloader!

            beforeEach {
                fileManagerMock = FileManagingMock()
                urlSessionMock = URLSessioningMock()
                sut = FileDownloader()
                sut.fileManager = fileManagerMock
                sut.urlSession = urlSessionMock
            }

            context("when file URL provided") {
                it("should return it (should don't download anything)") {
                    let url: URL = URL(fileURLWithPath: "./pathToFile")
                    sut.download(remoteURL: url) { inputURL -> (Void) in
                        expect(inputURL).to(equal(url))
                    }
                    fileManagerMock.verify(.urls(for: .any, in: .any), count: 0)
                    fileManagerMock.verify(.removeItem(at: .any), count: 0)
                    fileManagerMock.verify(.copyItem(at: .any, to: .any), count: 0)

                    urlSessionMock.verify(.downloadTask_(with: .any, completionHandler: .any), count: 0)
                }
            }

            context("when remote URL provided") {
                it("should request downloadTask from URLSession") {
                    let url: URL = URL(string: "https://path.to.com")!
                    let localURL: URL = URL(fileURLWithPath: "./pathToFile/")

                    let downloadTask = URLSessionDownloadingTaskMock()
                    urlSessionMock.given(.downloadTask_(with: .any, completionHandler: .any, willReturn: downloadTask))
                    urlSessionMock.perform(.downloadTask_(with: .any, completionHandler: .any, perform: { (_, completionHandler: @escaping (URL?, URLResponse?, Error?) -> Void) in
                        return completionHandler(URL(string: "https://info.info"), nil, nil)
                    }))

                    fileManagerMock.given(.urls(for: .any, in: .any, willReturn: [localURL]))

                    sut.download(remoteURL: url) { inputURL -> (Void) in }
                    urlSessionMock.verify(.downloadTask_(with: .any, completionHandler: .any), count: 1)

                    fileManagerMock.verify(.urls(for: .any, in: .any), count: 1)
                    fileManagerMock.verify(.removeItem(at: .any), count: 1)
                    fileManagerMock.verify(.copyItem(at: .any, to: .any), count: 1)
                }
            }
        }
    }
}
