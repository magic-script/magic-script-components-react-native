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

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.utils.Utils
import com.magicleap.magicscript.utils.limited

class UiDropdownListItemNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    fontProvider: FontProvider
) : UiTextNode(initProps, context, viewRenderableLoader, fontProvider) {

    companion object {
        const val PROP_ID = "id"
        const val PROP_LABEL = "label"

        private const val PADDING_FACTOR = 1.2F
    }

    var onSelectedListener: (() -> Unit)? = null

    var id: Int = 0
        private set

    var isSelected = false
        set(value) {
            field = value
            val bgColor = if (value) Color.GRAY else Color.TRANSPARENT
            view.setBackgroundColor(bgColor)
        }

    var maxCharacters = 0 // 0 means unlimited
        set(value) {
            field = value
            setNeedsRebuild()
        }

    override fun setupView() {
        super.setupView()

        val textSize = properties.getDouble(PROP_TEXT_SIZE, DEFAULT_TEXT_SIZE).toFloat()
        val textSizePx = Utils.metersToPx(textSize, context)
        val padding = (textSizePx * PADDING_FACTOR).toInt()
        view.setPadding(padding, padding, padding, padding)

        if (maxCharacters > 0) {
            val textView = view as TextView
            textView.text = textView.text.toString().limited(maxCharacters)
        }
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setId(props)
    }

    override fun setText(props: Bundle) {
        if (props.containsKey(PROP_LABEL)) {
            val label = props.getString(PROP_LABEL)!!
            val limited = if (maxCharacters > 0) label.limited(maxCharacters) else label
            props.putString(PROP_TEXT, limited)
        }
        super.setText(props)
    }

    override fun onViewClick() {
        super.onViewClick()
        onSelectedListener?.invoke()
        isSelected = !isSelected
    }

    private fun setId(props: Bundle) {
        if (props.containsKey(PROP_ID)) {
            this.id = props.getDouble(PROP_ID).toInt()
        }
    }
}
