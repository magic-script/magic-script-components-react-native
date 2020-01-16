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

package com.magicleap.magicscript.scene.nodes.dropdown

import android.content.Context
import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.utils.read

open class UiDropdownListItemNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    private val fontProvider: FontProvider
) : TransformNode(initProps, false, false) {

    companion object {
        const val PROP_ID = "id"
        const val PROP_LABEL = "label"
        const val PROP_SELECTED = "selected"

        private const val PADDING_FACTOR = 1.2F
    }

    var onSelectionChanged: ((selected: Boolean) -> Unit)? = null

    var id: Int = 0
        private set

    var label: String = ""
        private set

    var isSelected = false
        private set(value) {
            field = value
            adjustFont()
        }

    var maxCharacters = 0 // 0 means unlimited
        set(value) {
            field = value
            //  setNeedsRebuild()
        }

    fun select(silently: Boolean = false) {
        if (!isSelected) {
            isSelected = true
            if (!silently) {
                onSelectionChanged?.invoke(true)
            }
        }
    }

    fun deselect(silently: Boolean = false) {
        if (isSelected) {
            isSelected = false
            if (!silently) {
                onSelectionChanged?.invoke(false)
            }
        }
    }

    /*
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

     */

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setText(props)
        setId(props)
        setSelected(props)
    }

    private fun setText(props: Bundle) {
        props.read<String>(PROP_LABEL)?.let { lbl ->
            this.label = lbl
            //  val limited = if (maxCharacters > 0) lbl.limited(maxCharacters) else lbl
            //props.putString(PROP_TEXT, limited)
        }
    }

    /*
    override fun onViewClick() {
        super.onViewClick()
        if (isSelected) {
            deselect()
        } else {
            select()
        }
    }

     */

    private fun setId(props: Bundle) {
        if (props.containsKey(PROP_ID)) {
            this.id = props.getString(PROP_ID)?.toInt() ?: 0
        }
    }

    private fun setSelected(props: Bundle) {
        val selected: Boolean? = props.read(PROP_SELECTED)
        if (selected == true) {
            select()
        } else if (selected == false) {
            deselect()
        }
    }

    private fun adjustFont() {
        /*
        val fontParams = readFontParams(properties, "fontParams")
        if (isSelected) {
            val boldParams = if (fontParams != null) {
                FontParams(FontWeight.EXTRA_BOLD, fontParams.style, fontParams.allCaps)
            } else {
                FontParams(FontWeight.EXTRA_BOLD, FontStyle.NORMAL, false)
            }
            (view as TextView).typeface = fontProvider.provideFont(boldParams)
        } else {
            (view as TextView).typeface = fontProvider.provideFont(fontParams)
        }

         */
    }

}
