/*
 *  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

import android.app.TimePickerDialog
import android.content.Context
import android.os.BaseBundle
import android.os.Bundle
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.ar.renderable.ViewRenderableLoader
import com.magicleap.magicscript.scene.nodes.base.UiDateTimePickerBaseNode
import com.magicleap.magicscript.scene.nodes.views.DialogProvider
import com.magicleap.magicscript.utils.VerySimpleDateFormat
import com.magicleap.magicscript.utils.getHour
import com.magicleap.magicscript.utils.getMinute
import com.magicleap.magicscript.utils.putDefault
import kotlinx.android.synthetic.main.date_time_picker.view.*
import java.util.*

open class UiTimePickerNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    nodeClipper: Clipper,
    dialogProvider: DialogProvider
) : UiDateTimePickerBaseNode(
    initProps,
    context,
    viewRenderableLoader,
    nodeClipper,
    dialogProvider
) {

    companion object {
        const val PROP_DEFAULT_TIME = "defaultTime"
        const val PROP_TIME = "time"
        const val PROP_TIME_FORMAT = "timeFormat"

        const val TIME_FORMAT_DEFAULT = "HH:mm:ss"
    }

    var onTimeChanged: ((time: String) -> Unit)? = null
    var onTimeConfirmed: ((time: String) -> Unit)? = null

    private lateinit var timeFormat: VerySimpleDateFormat
    private var defaultTimeFormat = VerySimpleDateFormat(TIME_FORMAT_DEFAULT, Locale.getDefault())
    private var defaultTime: Date? = null
    private var time: Date? = null

    protected val onTimeSetListener: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            onTimeConfirmed?.invoke("$hourOfDay:$minute")
            view.value.setText(
                timeFormat.format(hourOfDay, minute),
                TextView.BufferType.EDITABLE
            )
            showing = false
        }

    protected val onTimeChangeListener: (Int, Int) -> Unit = { hourOfDay: Int, minute: Int ->
        onTimeChanged?.invoke("$hourOfDay:$minute")
    }


    init {
        properties.putDefault(PROP_TIME_FORMAT, TIME_FORMAT_DEFAULT)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        applyTimeFormat(props)
        applyDefaultTime(props)
        applyTime(props)
        applyShowHint(props)
    }

    override fun onViewClick() {
        if (!showing) {
            showing = true
            val initTime = when {
                time != null -> time!!
                defaultTime != null -> defaultTime!!
                else -> Date()
            }

            dialogProvider.provideTimePickerDialog(
                provideActivityContext(),
                onTimeSetListener,
                timeFormat.is24h
            ).apply {
                time = initTime
                onTimeChange = onTimeChangeListener
                updateTime(initTime.getHour(), initTime.getMinute())
                setOnDismissListener { showing = false }
            }.show()
        }
    }

    private fun applyTimeFormat(props: Bundle) {
        if (props.containsKey(PROP_TIME_FORMAT)) {
            props.getString(PROP_TIME_FORMAT)?.let { format ->
                timeFormat =
                    VerySimpleDateFormat(
                        format,
                        Locale.getDefault()
                    )
            }
        }
    }

    private fun applyDefaultTime(props: BaseBundle) {
        if (props.containsKey(PROP_DEFAULT_TIME)) {
            props.getString(PROP_DEFAULT_TIME)?.let {
                time = defaultTimeFormat.parse(it)
                view.value.setText(timeFormat.format(time), TextView.BufferType.EDITABLE)
            }
        }
    }

    private fun applyTime(props: Bundle) {
        if (props.containsKey(PROP_TIME)) {
            props.getString(PROP_TIME)?.let {
                time = defaultTimeFormat.parse(it)
                view.value.setText(timeFormat.format(time), TextView.BufferType.EDITABLE)
            }
        }
    }

    private fun applyShowHint(props: Bundle) {
        if (props.getBoolean(PROP_SHOW_HINT)) {
            view.value.hint = timeFormat.pattern
        } else {
            if (defaultTime == null) {
                defaultTime = Date()
            }
            view.value.hint = timeFormat.format(defaultTime)
        }
    }
}