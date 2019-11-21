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

package com.reactlibrary.scene.nodes

import android.app.TimePickerDialog
import android.content.Context
import android.os.BaseBundle
import android.os.Bundle
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.formatter.VerySimpleDateFormat
import com.reactlibrary.scene.nodes.base.UiDateTimePickerBaseNode
import com.reactlibrary.scene.nodes.views.DialogProviderImpl
import com.reactlibrary.utils.DialogProvider
import com.reactlibrary.utils.getHour
import com.reactlibrary.utils.getMinute
import com.reactlibrary.utils.putDefault
import kotlinx.android.synthetic.main.date_time_picker.view.*
import java.util.*

open class UiTimePickerNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    dialogProvider: DialogProvider
) : UiDateTimePickerBaseNode(initProps, context, viewRenderableLoader, dialogProvider) {

    companion object {
        const val PROP_DEFAULT_TIME = "defaultTime"
        const val PROP_TIME = "time"
        const val PROP_TIME_FORMAT = "timeFormat"

        const val TIME_FORMAT_DEFAULT = "HH:mm:ss"
    }

    var onTimeChanged: ((time: String) -> Unit)? = null
    var onTimeConfirmed: ((time: String) -> Unit)? = null

    private lateinit var timeFormat: VerySimpleDateFormat
    private var defaultTime: Date? = null
    private var time: Date? = null

    protected val onTimeSetListener: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            onTimeConfirmed?.invoke("$hourOfDay:$minute")
            view.value.setText(
                timeFormat.format(hourOfDay, minute),
                TextView.BufferType.EDITABLE
            )
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
    }

    override fun onViewClick() {
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
        }.show()
    }

    private fun applyTimeFormat(props: Bundle) {
        if (props.containsKey(PROP_TIME_FORMAT)) {
            props.getString(PROP_TIME_FORMAT)?.let { format ->
                timeFormat = VerySimpleDateFormat(format, Locale.getDefault())
                view.value.hint = format
            }
        }
    }

    private fun applyDefaultTime(props: BaseBundle) {
        if (props.containsKey(PROP_DEFAULT_TIME)) {
            props.getString(PROP_DEFAULT_TIME)?.let {
                time = timeFormat.parse(it)
            }
        }
    }

    private fun applyTime(props: Bundle) {
        if (props.containsKey(PROP_TIME)) {
            props.getString(PROP_TIME)?.let {
                time = timeFormat.parse(it)
            }
        }
    }
}