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

import android.graphics.Typeface
import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.font.FontParams
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.font.FontStyle
import com.magicleap.magicscript.font.FontWeight
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.utils.read

open class UiDropdownListItemNode(
    initProps: ReadableMap,
    private val fontProvider: FontProvider
) : TransformNode(initProps, false, false) {

    companion object {
        const val PROP_ID = "id"
        const val PROP_LABEL = "label"
        const val PROP_SELECTED = "selected"

        const val MAX_CHARACTERS_UNLIMITED = 0
    }

    var onSelectionChangeRequest: ((select: Boolean) -> Unit)? = null

    var id: Int = 0
        private set

    var label: String = ""
        private set

    var selected = false

    /**
     * The [PROP_SELECTED] value does not always have effect (e.g. when multi select mode is off
     * then only one item can be selected at the time)
     */
    val selectRequested get() = properties.read(PROP_SELECTED) ?: false

    var maxCharacters = MAX_CHARACTERS_UNLIMITED

    private val typefaceNormal: Typeface by lazy { fontProvider.provideFont() }

    private val typefaceBold: Typeface by lazy {
        val boldFontParams = FontParams(FontWeight.EXTRA_BOLD, FontStyle.NORMAL, false)
        fontProvider.provideFont(boldFontParams)
    }

    val typeface: Typeface
        get() = if (selected) {
            typefaceBold
        } else {
            typefaceNormal
        }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setId(props)
        setLabel(props)
        setSelected(props)
    }

    private fun setLabel(props: Bundle) {
        props.read<String>(PROP_LABEL)?.let { lbl ->
            this.label = lbl
        }
    }

    private fun setId(props: Bundle) {
        if (props.containsKey(PROP_ID)) {
            this.id = props.getString(PROP_ID)?.toInt() ?: 0
        }
    }

    private fun setSelected(props: Bundle) {
        val select = props.read<Boolean>(PROP_SELECTED) ?: return
        onSelectionChangeRequest?.invoke(select)
    }

}
