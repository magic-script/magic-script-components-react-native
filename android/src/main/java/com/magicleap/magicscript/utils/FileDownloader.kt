/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.magicleap.magicscript.utils

import android.content.Context
import android.webkit.URLUtil
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.net.URL


class FileDownloader(private val context: Context) {

    private val threads = mutableMapOf<String, Thread>()

    fun downloadFile(path: String?, result: (File) -> Unit): Boolean {
        try {
            if (path == null) {
                return false
            }
            val url = URL(path)

            val thread = Thread(
                    Runnable {
                        try {
                            val ucon = url.openConnection().apply {
                                readTimeout = 5000
                                connectTimeout = 10000
                            }

                            val inputStream = ucon.getInputStream()
                            val bufferedInputStream = BufferedInputStream(inputStream, 1024 * 5)

                            val internalStorage = context.getDir("filesdir", Context.MODE_PRIVATE)
                            val guessFileName = URLUtil.guessFileName(path, null, null)
                            val file = File("$internalStorage/$guessFileName")

                            if (file.exists()) {
                                file.delete()
                            }
                            file.createNewFile()

                            file.copyInputStreamToFile(bufferedInputStream)

                            bufferedInputStream.close()
                            fileDownloaded(path, result, file)
                        } catch (e: Exception) {
                            logMessage(e.toString(), true)
                        }
                    }
            )
            threads[path] = thread
            thread.start()

        } catch (e: Exception) {
            logMessage(e.toString(), true)
            return false
        }

        return true
    }

    fun onDestroy() {
        try {
            threads.values.forEach { it.interrupt() }
        } catch (e: SecurityException) {
            logMessage(e.toString(), true)
        }
        threads.clear()
    }

    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        inputStream.use { input ->
            this.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }

    private fun fileDownloaded(
            path: String,
            result: (File) -> Unit,
            file: File
    ) {
        result(file)
        try {
            threads[path]?.interrupt()
        } catch (e: SecurityException) {
            logMessage(e.toString(), true)
        }
        threads.remove(path)
    }
}