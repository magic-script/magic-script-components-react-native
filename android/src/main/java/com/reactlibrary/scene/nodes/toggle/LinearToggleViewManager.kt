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

package com.reactlibrary.scene.nodes.toggle

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import com.reactlibrary.font.FontProvider
import com.reactlibrary.utils.Utils

class LinearToggleViewManager(private val fontProvider: FontProvider) : ToggleViewManager {

    companion object {
        const val DEFAULT_WIDTH_TO_HEIGHT_RATIO = 2F
        const val RADIO_WIDTH_TO_HEIGHT_RATIO = 1F
        const val CHECKBOX_WIDTH_TO_HEIGHT_RATIO = 1F

        // spacing from text (relative to switch width)
        const val SWITCH_SPACING_RATIO = 0.75F
    }

    override fun setupToggleView(context: Context, toggleConfig: ToggleViewManager.ToggleConfig) {
        toggleConfig.container.apply {
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
            (this as LinearLayout).orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER

            if (toggleConfig.toggleType == UiToggleNode.TYPE_DEFAULT) {
                addView(toggleConfig.textView)
                addView(toggleConfig.imageView)
            } else {
                addView(toggleConfig.imageView)
                addView(toggleConfig.textView)
            }
        }

        val iconHeightPx = Utils.metersToPx(toggleConfig.toggleHeight, context)
        val iconWidthPx = iconHeightPx * getWidthToHeightRatio(toggleConfig.toggleType).toInt()
        // text to toggle spacing
        val spacing = (SWITCH_SPACING_RATIO * iconWidthPx).toInt()

        toggleConfig.imageView.layoutParams = LinearLayout.LayoutParams(iconWidthPx, iconHeightPx).apply {
            if (toggleConfig.toggleType == UiToggleNode.TYPE_DEFAULT) {
                leftMargin = spacing
            } else {
                rightMargin = spacing
            }
        }

        toggleConfig.textView.typeface = fontProvider.provideFont()
    }

    override fun getToggleWidth(toggleType: String, toggleHeight: Float): Float {
        return toggleHeight * getWidthToHeightRatio(toggleType)
    }

    private fun getWidthToHeightRatio(toggleType: String): Float {
        return when (toggleType) {
            UiToggleNode.TYPE_RADIO -> RADIO_WIDTH_TO_HEIGHT_RATIO
            UiToggleNode.TYPE_CHECKBOX -> CHECKBOX_WIDTH_TO_HEIGHT_RATIO
            else -> DEFAULT_WIDTH_TO_HEIGHT_RATIO
        }
    }

}