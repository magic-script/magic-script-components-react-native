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

package com.reactlibrary.scene.nodes.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.ArViewManager
import com.reactlibrary.R
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.utils.*
import kotlinx.android.synthetic.main.date_time_picker.view.*

open class UiDateTimePickerBaseNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    protected val dialogProvider: DialogProvider
) : UiNode(initProps, context, viewRenderableLoader) {

    companion object {
        const val PROP_LABEL = "label"
        const val PROP_LABEL_SIDE = "labelSide"
        const val PROP_COLOR = "color"

        const val LABEL_SIDE_LEFT = "left"
        const val LABEL_SIDE_TOP = "top"

        const val DEFAULT_TEXT_SIZE = 0.025f //in meters
    }

    init {
        properties.putDefault(PROP_LABEL_SIDE, LABEL_SIDE_TOP)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        applyLabel(props)
        applyLabelSide(props)
        applyTextColor(props)
    }

    override fun setupView() {
        super.setupView()

        view.title.textSize =
            Utils.metersToFontPx(DEFAULT_TEXT_SIZE, view.context).toFloat()
        view.value.textSize =
            Utils.metersToFontPx(DEFAULT_TEXT_SIZE, view.context).toFloat()
        view.value.setOnClickListener { this.onViewClick() }
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.date_time_picker, null)

    }

    override fun provideDesiredSize(): Vector2 =
        Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)

    protected open fun provideActivityContext() = ArViewManager.getActivityRef().get() as Context

    private fun applyLabel(props: Bundle) {
        if (props.containsKey(PROP_LABEL)) {
            view.title.text = props.getString(PROP_LABEL)
            setNeedsRebuild()
        }
    }

    private fun applyLabelSide(props: Bundle) {
        if (props.containsKey(PROP_LABEL_SIDE)) {
            val labelSide = props.getString(PROP_LABEL_SIDE)
            if (labelSide == LABEL_SIDE_LEFT) {
                (view as LinearLayout).orientation = LinearLayout.HORIZONTAL
            }
            if (labelSide == LABEL_SIDE_TOP) {
                (view as LinearLayout).orientation = LinearLayout.VERTICAL
            }

            setNeedsRebuild()
        }
    }

    private fun applyTextColor(props: Bundle) {
        if (props.containsKey(PROP_COLOR)) {
            PropertiesReader.readColor(props, PROP_COLOR)?.let {
                view.value.setTextColor(it)
            }
        }
    }
}