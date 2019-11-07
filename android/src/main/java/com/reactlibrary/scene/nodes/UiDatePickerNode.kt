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

import android.annotation.TargetApi
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.ArViewManager
import com.reactlibrary.R
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.formatter.VerySimpleDateFormat
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.views.DatePickerDialogProvider
import com.reactlibrary.utils.*
import kotlinx.android.synthetic.main.date_picker.view.*
import java.util.*


open class UiDatePickerNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    private val datePickerDialogProvider: DatePickerDialogProvider
) : UiNode(initProps, context, viewRenderableLoader) {

    companion object {
        const val PROP_LABEL = "label"
        const val PROP_LABEL_SIDE = "labelSide"
        const val PROP_DEFAULT_DATE = "defaultDate"
        const val PROP_YEAR_MIN = "yearMin"
        const val PROP_YEAM_MAX = "yearMax"
        const val PROP_DATE = "date"
        const val PROP_COLOR = "color"
        const val PROP_DATE_FORMAT = "dateFormat"

        const val LABEL_SIDE_LEFT = "left"
        const val LABEL_SIDE_TOP = "top"

        const val DATE_FORMAT_DEFAULT = "MM/dd/yyyy"
        const val DEFAULT_TEXT_SIZE = 0.025f //in meters
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
            view.date_value.setText(
                dateFormat.format(year, monthOfYear, dayOfMonth),
                TextView.BufferType.EDITABLE
            )
        }

    private val onDateChangedListener: (DatePicker, Int, Int, Int) -> Unit =
        { _, year, monthOfYear, dayOfMonth ->
            onDateChanged?.invoke("$monthOfYear/$dayOfMonth/$year")
        }

    init {
        properties.putDefaultString(PROP_LABEL_SIDE, LABEL_SIDE_TOP)
        properties.putDefaultDouble(PROP_YEAR_MIN, DEFAULT_MAX_MIN_YEAR)
        properties.putDefaultDouble(PROP_YEAM_MAX, DEFAULT_MAX_MIN_YEAR)
        properties.putDefaultString(PROP_DATE_FORMAT, DATE_FORMAT_DEFAULT)
    }

    override fun setupView() {
        super.setupView()

        view.date_title.textSize = Utils.metersToFontPx(DEFAULT_TEXT_SIZE, view.context).toFloat()
        view.date_value.textSize = Utils.metersToFontPx(DEFAULT_TEXT_SIZE, view.context).toFloat()
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        applyLabel(props)
        applyLabelSide(props)
        applyDateFormat(props)
        applyDefaultDate(props)
        applyDate(props)
        applyMinMaxYear(props)
        applyTextColor(props)
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

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.date_picker, null)

    }

    override fun provideDesiredSize(): Vector2 =
        Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)

    protected open fun provideActivityContext() = ArViewManager.getActivityRef().get() as Context

    private fun createDatePickerDialog(): DatePickerDialog =
        datePickerDialogProvider.provideDatePickerDialog(provideActivityContext())
            .apply {
                setOnDateSetListener(onDateSetListener)
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    datePicker.setOnDateChangedListener(onDateChangedListener)
                }
            }

    private fun applyLabel(props: Bundle) {
        if (props.containsKey(PROP_LABEL)) {
            view.date_title.text = props.getString(PROP_LABEL)
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

    private fun applyDateFormat(props: Bundle) {
        if (props.containsKey(PROP_DATE_FORMAT)) {
            props.getString(PROP_DATE_FORMAT).let { format ->
                dateFormat = VerySimpleDateFormat(format, Locale.getDefault())
                view.date_value.hint = format
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

    private fun applyTextColor(props: Bundle) {
        if (props.containsKey(PROP_COLOR)) {
            PropertiesReader.readColor(props, PROP_COLOR)?.let {
                view.date_value.setTextColor(it)
            }
        }
    }
}