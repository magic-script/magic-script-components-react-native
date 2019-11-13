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

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.formatter.VerySimpleDateFormat
import com.reactlibrary.scene.nodes.base.UiDateTimePickerBaseNode
import com.reactlibrary.scene.nodes.views.DateTimePickerDialogProvider
import com.reactlibrary.utils.*
import kotlinx.android.synthetic.main.date_time_picker.view.*
import java.util.*


open class UiDatePickerNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    datePickerDialogProvider: DateTimePickerDialogProvider
) : UiDateTimePickerBaseNode(initProps, context, viewRenderableLoader, datePickerDialogProvider) {

    companion object {
        const val PROP_DEFAULT_DATE = "defaultDate"
        const val PROP_YEAR_MIN = "yearMin"
        const val PROP_YEAM_MAX = "yearMax"
        const val PROP_DATE = "date"
        const val PROP_DATE_FORMAT = "dateFormat"

        const val DATE_FORMAT_DEFAULT = "MM/dd/yyyy"
        const val DEFAULT_MAX_MIN_YEAR = -1.0
    }

    var onDateChanged: ((date: String) -> Unit)? = null
    var onDateConfirmed: ((date: String) -> Unit)? = null
    private var maxYear: Int = -1
    private var minYear: Int = -1
    private lateinit var dateFormat: VerySimpleDateFormat
    private var defaultDate: Date? = null
    private var date: Date? = null

    protected val onDateSetListener: (DatePicker, Int, Int, Int) -> Unit =
        { _, year, month, dayOfMonth ->
            val monthOfYear = month + 1
            onDateConfirmed?.invoke("$month/$monthOfYear/$year")
            view.value.setText(
                dateFormat.format(year, monthOfYear, dayOfMonth),
                TextView.BufferType.EDITABLE
            )
        }

    private val onDateChangedListener: (DatePicker, Int, Int, Int) -> Unit =
        { _, year, monthOfYear, dayOfMonth ->
            onDateChanged?.invoke("$monthOfYear/$dayOfMonth/$year")
        }

    init {
        properties.putDefaultDouble(PROP_YEAR_MIN, DEFAULT_MAX_MIN_YEAR)
        properties.putDefaultDouble(PROP_YEAM_MAX, DEFAULT_MAX_MIN_YEAR)
        properties.putDefaultString(PROP_DATE_FORMAT, DATE_FORMAT_DEFAULT)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        applyDateFormat(props)
        applyDefaultDate(props)
        applyDate(props)
        applyMinMaxYear(props)
    }

    override fun onViewClick() {
        val initDate = when {
            date != null -> date!!
            defaultDate != null -> defaultDate!!
            else -> Date()
        }

        createDatePickerDialog().apply {
            updateMinMaxYear(minYear, maxYear)
            updateDate(initDate)
        }.show()
    }

    private fun createDatePickerDialog(): DatePickerDialog =
        dialogProvider.provideDatePickerDialog(provideActivityContext())
            .apply {
                setOnDateSetListener(onDateSetListener)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    datePicker.setOnDateChangedListener(onDateChangedListener)
                }
            }

    private fun applyDateFormat(props: Bundle) {
        if (props.containsKey(PROP_DATE_FORMAT)) {
            props.getString(PROP_DATE_FORMAT).let { format ->
                dateFormat = VerySimpleDateFormat(format, Locale.getDefault())
                view.value.hint = format
            }
        }
    }

    private fun applyDefaultDate(props: Bundle) {
        if (props.containsKey(PROP_DEFAULT_DATE)) {
            defaultDate = dateFormat.parse(props.getString(PROP_DEFAULT_DATE))
        }
    }

    private fun applyDate(props: Bundle) {
        if (props.containsKey(PROP_DATE)) {
            date = dateFormat.parse(props.getString(PROP_DATE))
        }
    }

    private fun applyMinMaxYear(props: Bundle) {
        minYear = props.getDouble(PROP_YEAR_MIN).toInt()
        maxYear = props.getDouble(PROP_YEAM_MAX).toInt()
    }
}