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

package com.magicleap.magicscript.scene.nodes.audio

import android.content.Context
import android.net.Uri
import android.webkit.URLUtil
import com.magicleap.magicscript.utils.logMessage
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.net.URL

class UriAudioProvider(private val context: Context) :
    AudioFileProvider {

    companion object {
        private const val READ_TIMEOUT = 5000
        private const val CONNECT_TIMEOUT = 10000
    }

    private val threads = mutableMapOf<String, Thread>()

    override fun provideFile(uri: Uri, result: (File) -> Unit) {
        val thread = Thread(
            Runnable {
                try {
                    val inputStream: InputStream =
                        if (uri.toString().startsWith("android.resource")) {
                            getLocalInputStream(uri)
                        } else if (uri.toString().startsWith("content://")) {
                            getLocalContentStream(uri)
                        } else {
                            getRemoteInputStream(uri)
                        }

                    val bufferedInputStream = BufferedInputStream(inputStream, 1024 * 5)

                    val internalStorage = context.filesDir
                    val guessFileName = URLUtil.guessFileName(uri.toString(), null, null)
                    val file = File("$internalStorage/$guessFileName")

                    if (file.exists()) {
                        file.delete()
                    }
                    file.createNewFile()

                    file.copyInputStreamToFile(bufferedInputStream)

                    bufferedInputStream.close()
                    fileDownloaded(uri.path, result, file)
                } catch (e: Exception) {
                    logMessage("Error during reading Audio File $e", true)
                }
            }
        )
        threads[uri.path] = thread
        thread.start()
    }

    private fun getRemoteInputStream(uri: Uri): InputStream {
        val url = URL(uri.toString())
        val connection = url.openConnection().apply {
            readTimeout = READ_TIMEOUT
            connectTimeout = CONNECT_TIMEOUT
        }

        return connection.getInputStream()
    }

    private fun getLocalInputStream(uri: Uri): InputStream {
        val filename = uri.path.toString().split("/").last()

        val resources = context.resources
        val identifier =
            resources.getIdentifier(filename, "raw", context.packageName)

        return resources.openRawResource(identifier)
    }

    private fun getLocalContentStream(uri: Uri): InputStream {
        return context.contentResolver.openInputStream(uri)
    }

    override fun onDestroy() {
        threads.values.forEach { it.interrupt() }
        threads.clear()
    }

    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        inputStream.use { input ->
            this.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }

    private fun fileDownloaded(path: String, result: (File) -> Unit, file: File) {
        result(file)
        threads[path]?.interrupt()
        threads.remove(path)
    }
}