/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.magicleap.magicscript.scene.nodes.dropdown

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.magicleap.magicscript.utils.limited

class DropdownItemsAdapter(
    context: Context,
    @LayoutRes private val layoutRes: Int,
    items: List<UiDropdownListItemNode>
) : ArrayAdapter<UiDropdownListItemNode>(context, layoutRes, items) {

    // in pixels
    var textSizePx = 24

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(layoutRes, null)
        view as TextView

        getItem(position)?.let { item ->
            view.text = if (item.maxCharacters != UiDropdownListItemNode.MAX_CHARACTERS_UNLIMITED) {
                item.label.limited(item.maxCharacters)
            } else {
                item.label
            }
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx.toFloat())
            view.typeface = item.typeface

            val paddingHorizontal = textSizePx
            val paddingVertical = paddingHorizontal / 2
            view.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
        }

        return view
    }

}