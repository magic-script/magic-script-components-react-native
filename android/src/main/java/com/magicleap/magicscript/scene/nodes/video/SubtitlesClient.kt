package com.magicleap.magicscript.scene.nodes.video

import android.content.Context
import android.webkit.URLUtil
import okhttp3.*
import java.io.*

class SubtitlesClient(
        private val okHttpClient: OkHttpClient,
        private val context: Context
) {

    fun downloadSubtitleFile(url: String, onSuccess: (String) -> Unit) {
        val subtitleFile = File(context.cacheDir, URLUtil.guessFileName(url, null, null))
        if (subtitleFile.exists()) {
            onSuccess(subtitleFile.absolutePath)
        }
        val callback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                throw e
            }

            override fun onResponse(call: Call, response: Response) {
                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null
                try {
                    inputStream = response.body!!.byteStream()
                    outputStream = FileOutputStream(subtitleFile, false)
                    copyFile(inputStream, outputStream)
                    onSuccess(subtitleFile.absolutePath!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    closeStreams(inputStream, outputStream)
                }
            }

        }
        val request = Request.Builder().url(url)
                .build()
        okHttpClient.newCall(request).enqueue(callback)
    }

    private fun closeStreams(vararg closeables: Closeable?) {
        for (stream in closeables) {
            try {
                stream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    private fun copyFile(inputStream: InputStream, outputStream: OutputStream) {
        val BUFFER_SIZE = 1024
        val buffer = ByteArray(BUFFER_SIZE)
        var length: Int
        while (inputStream.read(buffer).also { length = it } != -1) {
            outputStream.write(buffer, 0, length)
        }
    }
}