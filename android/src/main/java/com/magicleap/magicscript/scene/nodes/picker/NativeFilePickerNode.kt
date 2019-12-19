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

package com.magicleap.magicscript.scene.nodes.picker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.ActivityResultObserver
import com.magicleap.magicscript.R
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.icons.IconsRepository
import com.magicleap.magicscript.scene.nodes.UiButtonNode
import com.magicleap.magicscript.utils.read
import java.util.*

class NativeFilePickerNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    fontProvider: FontProvider,
    iconsRepo: IconsRepository
) : UiButtonNode(initProps, context, viewRenderableLoader, fontProvider, iconsRepo),
    ActivityResultObserver {

    companion object {
        const val PROP_FILE_TYPE = "fileType"
        const val PROP_CHOOSER_TITLE = "chooserTitle"

        const val FILE_TYPE_ALL = "*/*"
        const val CHOOSER_TITLE_DEFAULT = "Choose a file"
    }

    private val REQUEST_CODE: Int = UUID.randomUUID().hashCode() and 0xFF

    var onFileSelected: ((filePath: String) -> Unit)? = null

    private lateinit var fileType: String
    private lateinit var chooserTitle: String

    @SuppressLint("InflateParams")
    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.file_picker, null)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        fileType = props.read(PROP_FILE_TYPE) ?: FILE_TYPE_ALL
        chooserTitle = props.read(PROP_CHOOSER_TITLE) ?: CHOOSER_TITLE_DEFAULT
    }

    override fun onViewClick() {
        super.onViewClick()

        val getContent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = fileType
        }
        val chooser = Intent.createChooser(getContent, chooserTitle)
        (context as ReactApplicationContext).startActivityForResult(chooser, REQUEST_CODE, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_CODE) {
            onFileSelected?.invoke(data.dataString)
        }
    }
}