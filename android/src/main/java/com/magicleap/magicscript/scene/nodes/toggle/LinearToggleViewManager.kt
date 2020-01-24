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

package com.magicleap.magicscript.scene.nodes.toggle

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.icons.ToggleIconsProvider
import com.magicleap.magicscript.utils.Utils

class LinearToggleViewManager(
    private val fontProvider: FontProvider,
    private val toggleIconsProvider: ToggleIconsProvider
) : ToggleViewManager {

    companion object {
        const val DEFAULT_WIDTH_TO_HEIGHT_RATIO = 2F
        const val RADIO_WIDTH_TO_HEIGHT_RATIO = 1F
        const val CHECKBOX_WIDTH_TO_HEIGHT_RATIO = 1F

        // spacing from text relative to icon width
        const val DEFAULT_SPACING_RATIO = 1F
        const val RADIO_SPACING_RATIO = 0.82F
        const val CHECKBOX_SPACING_RATIO = 0.82F
    }

    private var isActive = false
    private var toggleType: String = UiToggleNode.TYPE_DEFAULT
    private var textView: TextView? = null
    private var imageView: ImageView? = null

    override fun setupToggleView(context: Context, toggleConfig: ToggleConfig) {
        this.toggleType = toggleConfig.toggleType
        this.textView = toggleConfig.textView
        this.imageView = toggleConfig.imageView

        toggleConfig.container.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            (this as LinearLayout).orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER

            if (toggleType == UiToggleNode.TYPE_DEFAULT) {
                addView(textView)
                addView(imageView)
            } else {
                addView(imageView)
                addView(textView)
            }
        }

        val iconHeightPx = Utils.metersToPx(toggleConfig.toggleHeight, context)
        val iconWidthPx = iconHeightPx * getWidthToHeightRatio(toggleType).toInt()
        // text to toggle spacing
        val spacing = (getSpacingRatio() * iconWidthPx).toInt()

        toggleConfig.imageView.layoutParams =
            LinearLayout.LayoutParams(iconWidthPx, iconHeightPx).apply {
                if (toggleType == UiToggleNode.TYPE_DEFAULT) {
                    leftMargin = spacing
                } else {
                    rightMargin = spacing
                }
            }

        toggleConfig.imageView.setOnClickListener {
            toggleConfig.onToggleClickListener()
        }
        refreshImage()

        toggleConfig.textView.typeface = fontProvider.provideFont()
    }

    override fun setActive(active: Boolean) {
        isActive = active
        refreshImage()
    }

    override fun setText(text: String) {
        textView?.text = text
    }

    override fun setTextSize(textSizePx: Int) {
        textView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx.toFloat())
    }

    override fun setTextColor(color: Int) {
        textView?.setTextColor(color)
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

    private fun refreshImage() {
        val iconId = toggleIconsProvider.provideIconId(toggleType, isActive)
        imageView?.setImageResource(iconId)
    }

    private fun getSpacingRatio(): Float {
        return when (toggleType) {
            UiToggleNode.TYPE_CHECKBOX -> CHECKBOX_SPACING_RATIO
            UiToggleNode.TYPE_RADIO -> RADIO_SPACING_RATIO
            else -> DEFAULT_SPACING_RATIO
        }
    }

}