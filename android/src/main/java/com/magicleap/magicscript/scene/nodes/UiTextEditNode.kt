/*
 * Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.ArViewManager
import com.magicleap.magicscript.R
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.ar.renderable.ViewRenderableLoader
import com.magicleap.magicscript.font.FontParams
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.scene.nodes.base.UiNode
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.scene.nodes.props.ScrollBarVisibility
import com.magicleap.magicscript.scene.nodes.views.InputDialogBuilder
import com.magicleap.magicscript.utils.*
import kotlinx.android.synthetic.main.text_edit.view.*
import java.lang.Integer.max
import java.lang.Integer.min

open class UiTextEditNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    nodeClipper: Clipper,
    private val fontProvider: FontProvider
) : UiNode(initProps, context, viewRenderableLoader, nodeClipper) {

    companion object {
        // properties
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
        const val PROP_TEXT = "text"
        const val PROP_HINT = "hint"
        const val PROP_HINT_COLOR = "hintColor"
        const val PROP_TEXT_SIZE = "textSize"
        const val PROP_TEXT_ALIGNMENT = "textAlignment"
        const val PROP_TEXT_COLOR = "textColor"
        const val PROP_CHARACTERS_SPACING = "charSpacing"
        const val PROP_CHARACTERS_LIMIT = "charLimit"
        const val PROP_LINE_SPACING = "lineSpacing" // spacing multiplier
        const val PROP_PASSWORD = "password"
        const val PROP_MULTILINE = "multiline"
        const val PROP_TEXT_PADDING = "textPadding"
        const val PROP_SCROLLING = "scrolling"
        const val PROP_TEXT_ENTRY_MODE = "textEntry"
        const val PROP_SCROLLBAR_VISIBILITY = "scrollBarVisibility"
        const val PROP_SELECTED_BEGIN = "selectedBegin"
        const val PROP_SELECTED_END = "selectedEnd"

        const val ENTRY_MODE_NORMAL = "normal"
        const val ENTRY_MODE_EMAIL = "email"
        const val ENTRY_MODE_NUMERIC = "numeric"
        const val DEFAULT_TEXT_SIZE = 0.0298 // in meters
        const val DEFAULT_ALIGNMENT = "top-left" // view alignment (pivot)
        const val DEFAULT_SCROLLING = false // scrolling disabled
        const val DEFAULT_CHARACTERS_SPACING = 0.00499
        const val DEFAULT_CHARACTERS_LIMIT = 0.0 // indefinite
        val DEFAULT_TEXT_PADDING = arrayListOf(0.003, 0.003, 0.003, 0.003)

        const val CURSOR_BLINK_INTERVAL = 400L // in ms
    }

    var onTextChangedListener: ((text: String) -> Unit)? = null

    private var cursorVisible = false
    private var text = ""
    private var hint = ""
    private val mainHandler = Handler(Looper.getMainLooper())
    private var textColor = context.getColor(R.color.text_color_default)
    private var hintColor = context.getColor(R.color.text_color_hint)
    private var textGravityVertical: Int = Gravity.CENTER_VERTICAL
    private var textGravityHorizontal: Int = Gravity.LEFT
    private var isSelected = false

    private val cursorAnimationRunnable = object : Runnable {
        override fun run() {
            refreshVisibleText()
            cursorVisible = !cursorVisible
            mainHandler.postDelayed(this, CURSOR_BLINK_INTERVAL)
        }
    }

    init {
        // set default values of properties
        properties.putDefault(PROP_TEXT_SIZE, DEFAULT_TEXT_SIZE)
        properties.putDefault(PROP_TEXT_PADDING, DEFAULT_TEXT_PADDING)
        properties.putDefault(PROP_ALIGNMENT, DEFAULT_ALIGNMENT)
        properties.putDefault(PROP_SCROLLBAR_VISIBILITY, ScrollBarVisibility.AUTO)
        properties.putDefault(PROP_CHARACTERS_SPACING, DEFAULT_CHARACTERS_SPACING)
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.text_edit, null)
    }

    override fun provideDesiredSize(): Vector2 {
        val width = properties.getDouble(PROP_WIDTH, WRAP_CONTENT_DIMENSION.toDouble())
        val height = properties.getDouble(PROP_HEIGHT, WRAP_CONTENT_DIMENSION.toDouble())
        return Vector2(width.toFloat(), height.toFloat())
    }

    override fun setupView() {
        super.setupView()

        val fontParams = FontParams.fromBundle(properties)
        if (fontParams.style == null && fontParams.weight == null) {
            // setting a default typeface
            view.text_edit.typeface = fontProvider.provideFont()
        }
        view.text_edit.setSingleLine() // single line by default
        setupViewListeners()
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        if (props.containsKey(PROP_WIDTH) || props.containsKey(PROP_HEIGHT)) {
            setNeedsRebuild()
        }
        setText(props)
        setHint(props)
        setHintColor(props)
        setTextSize(props)
        setTextAlignment(props)
        setTextColor(props)
        setCharactersSpacing(props)
        setLineSpacing(props)
        setMultiline(props)
        setTextPadding(props)
        setFontParams(props)
        setScrollBarVisibility(props)
        setSelection(props)
    }

    private fun setText(props: Bundle) {
        val text = props.getString(PROP_TEXT)
        if (text != null) {
            this.text = text
            refreshVisibleText()
        }
    }

    private fun setHint(props: Bundle) {
        val hint = props.getString(PROP_HINT)
        if (hint != null) {
            this.hint = hint
            refreshVisibleText()
        }
    }

    private fun setHintColor(props: Bundle) {
        val color = props.readColor(PROP_HINT_COLOR)
        if (color != null) {
            this.hintColor = color
            view.text_edit.setHintTextColor(hintColor)
        }
    }

    private fun setTextSize(props: Bundle) {
        if (props.containsKey(PROP_TEXT_SIZE)) {
            val sizeMeters = props.getDouble(PROP_TEXT_SIZE).toFloat()
            setTextSize(sizeMeters)
        }
    }

    private fun setTextSize(sizeMeters: Float) {
        val size = Utils.metersToFontPx(sizeMeters, view.context).toFloat()
        view.text_edit.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    private fun setTextAlignment(props: Bundle) {
        if (props.containsKey(PROP_TEXT_ALIGNMENT)) {
            val textAlignment = props.getString(PROP_TEXT_ALIGNMENT)
            textGravityHorizontal = when (textAlignment) {
                "left" -> {
                    Gravity.LEFT
                }
                "center" -> {
                    Gravity.CENTER_HORIZONTAL
                }
                "right" -> {
                    Gravity.RIGHT
                }
                else -> {
                    Gravity.LEFT
                }
            }
            view.text_edit.gravity = textGravityVertical or textGravityHorizontal
        }
    }

    private fun setTextColor(props: Bundle) {
        val color = props.readColor(PROP_TEXT_COLOR)
        if (color != null) {
            this.textColor = color
            refreshVisibleText()
        }
    }

    private fun setCharactersSpacing(props: Bundle) {
        if (props.containsKey(PROP_CHARACTERS_SPACING)) {
            val spacing = props.getDouble(PROP_CHARACTERS_SPACING)
            setCharactersSpacing(spacing)
        }
    }

    private fun setCharactersSpacing(spacing: Double) {
        view.text_edit.letterSpacing = spacing.toFloat()
    }

    private fun setLineSpacing(props: Bundle) {
        if (props.containsKey(PROP_LINE_SPACING)) {
            val spacingMultiplier = props.getDouble(PROP_LINE_SPACING).toFloat()
            view.text_edit.setLineSpacing(0F, spacingMultiplier)
        }
    }

    private fun setMultiline(props: Bundle) {
        if (props.containsKey(PROP_MULTILINE)) {
            val isMultiline = props.getBoolean(PROP_MULTILINE)
            view.text_edit.setSingleLine(!isMultiline)
            textGravityVertical = if (isMultiline) Gravity.TOP else Gravity.CENTER_VERTICAL
            view.text_edit.gravity = textGravityVertical or textGravityHorizontal
            adjustBackground()
        }
    }

    private fun setTextPadding(props: Bundle) {
        val padding = props.read<Padding>(PROP_TEXT_PADDING)
        if (padding != null) {
            val top = Utils.metersToPx(padding.top, view.context)
            val right = Utils.metersToPx(padding.right, view.context)
            val bottom = Utils.metersToPx(padding.bottom, view.context)
            val left = Utils.metersToPx(padding.left, view.context)
            view.text_edit.setPadding(left, top, right, bottom)
        }
    }

    private fun setFontParams(props: Bundle) {
        val fontParams = FontParams.fromBundle(props)

        if (fontParams.style != null || fontParams.weight != null) {
            view.text_edit.typeface = fontProvider.provideFont(fontParams.style, fontParams.weight)
        }

        fontParams.allCaps?.let {
            view.text_edit.isAllCaps = it
        }

        fontParams.fontSize?.let {
            setTextSize(it.toFloat())
        }

        fontParams.tracking?.let {
            setCharactersSpacing(it)
        }
    }

    private fun setScrollBarVisibility(props: Bundle) {
        if (props.containsKey(PROP_SCROLLBAR_VISIBILITY)) {
            when (props.getString(PROP_SCROLLBAR_VISIBILITY)) {
                ScrollBarVisibility.AUTO -> {
                    view.sv_text_edit.isVerticalScrollBarEnabled = true
                    view.sv_text_edit.isScrollbarFadingEnabled = true
                }
                ScrollBarVisibility.ALWAYS -> {
                    view.sv_text_edit.isVerticalScrollBarEnabled = true
                    view.sv_text_edit.isScrollbarFadingEnabled = false
                }
                ScrollBarVisibility.OFF -> {
                    view.sv_text_edit.isVerticalScrollBarEnabled = false
                }
            }
        }
    }

    override fun onViewClick() {
        super.onViewClick()

        // disabling parent view is not sufficient
        if (!properties.getBoolean(PROP_ENABLED)) {
            return
        }
        val activity = ArViewManager.getActivityRef().get()
        if (activity != null) {
            isSelected = true
            startCursorAnimation()
            showBorder()
            view.text_edit.setTextColor(textColor)
            showInputDialog(activity)
            onFocusGainedListener?.invoke()
        }
    }

    private fun setupViewListeners() {
        // override touch event to disable scroll if needed
        view.sv_text_edit.setOnTouchListener { _, _ ->
            return@setOnTouchListener !properties.getBoolean(PROP_SCROLLING, DEFAULT_SCROLLING)
        }
    }

    private fun startCursorAnimation() {
        cursorAnimationRunnable.run()
    }

    private fun stopCursorAnimation() {
        mainHandler.removeCallbacks(cursorAnimationRunnable)
        cursorVisible = false
    }

    private fun showInputDialog(context: Context) {
        val multiline = properties.getBoolean(PROP_MULTILINE)
        val builder = InputDialogBuilder(context, multiline, isPassword())
        builder.setInputText(text)

        val charsLimit = properties.getDouble(PROP_CHARACTERS_LIMIT, DEFAULT_CHARACTERS_LIMIT)
        if (charsLimit > DEFAULT_CHARACTERS_LIMIT) {
            builder.setMaxCharacters(charsLimit.toInt())
        }

        when (properties.getString(PROP_TEXT_ENTRY_MODE)) {
            ENTRY_MODE_EMAIL -> {
                builder.setEntryMode(InputDialogBuilder.EntryMode.EMAIL)
            }
            ENTRY_MODE_NUMERIC -> {
                builder.setEntryMode(InputDialogBuilder.EntryMode.NUMERIC)
            }
            ENTRY_MODE_NORMAL -> {
                builder.setEntryMode(InputDialogBuilder.EntryMode.NORMAL)
            }
        }

        builder.setOnSubmitListener { input ->
            if (input != text) {
                text = input
                onTextChangedListener?.invoke(input)
            }
        }
        val begin = properties.read(PROP_SELECTED_BEGIN) ?: 0
        val end = properties.read(PROP_SELECTED_END) ?: 0
        if (begin != end) {
            builder.setSelection(begin, end)
        }

        builder.setOnCloseListener {
            isSelected = false
            stopCursorAnimation()
            refreshVisibleText()
            hideBorder()
            onFocusLostListener?.invoke()
        }

        builder.show()
    }

    private fun showBorder() {
        adjustBackground()

        // add some padding because of rounded corners
        val multiline = properties.getBoolean(PROP_MULTILINE)
        val resources = context.resources
        val paddingHorizontal = if (multiline) {
            resources.getDimensionPixelSize(R.dimen.textedit_border_padding_horiz_big)
        } else {
            resources.getDimensionPixelSize(R.dimen.textedit_border_padding_horiz)
        }
        val paddingVertical = if (multiline) {
            resources.getDimensionPixelSize(R.dimen.textedit_border_padding_vertical_big)
        } else {
            resources.getDimensionPixelSize(R.dimen.textedit_border_padding_vertical)
        }
        view.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
    }

    private fun hideBorder() {
        adjustBackground()
        view.setPadding(0, 0, 0, 0)
    }

    private fun adjustBackground() {
        if (isSelected) {
            view.background = context.getDrawable(R.drawable.text_edit_background_active)
        } else if (properties.getBoolean(PROP_MULTILINE)) {
            view.background = context.getDrawable(R.drawable.text_edit_background)
        } else {
            view.setBackgroundResource(0) // no background
        }
    }

    private fun refreshVisibleText() {
        if (isSelected) {
            // preserve space (transparent color) for cursor (in case of center or right alignment)
            val cursorColor = if (cursorVisible) textColor else Color.TRANSPARENT
            val textWithCursor = getMaskedText() + "|"
            val spannable = SpannableString(textWithCursor)
            spannable.setSpan(
                ForegroundColorSpan(cursorColor),
                textWithCursor.length - 1,
                textWithCursor.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            view.text_edit.setText(spannable, TextView.BufferType.EDITABLE)
            view.text_edit.setTextColor(textColor) // clear hint color
            return
        }

        if (text.isEmpty()) { // display hint
            view.text_edit.hint = hint
            return
        }

        view.text_edit.setText(getMaskedText(), TextView.BufferType.EDITABLE)
        view.text_edit.setTextColor(textColor)

        setSelection(properties)
    }

    private fun getMaskedText(): String {
        return if (isPassword()) {
            "*".repeat(text.length)
        } else {
            text
        }
    }

    private fun isPassword(): Boolean {
        return properties.getBoolean(PROP_PASSWORD, false)
    }

    private fun setSelection(props: Bundle) {
        if (props.containsAll(PROP_SELECTED_BEGIN, PROP_SELECTED_END)) {
            var begin = props.read(PROP_SELECTED_BEGIN) ?: 0
            var end = props.read(PROP_SELECTED_END) ?: 0

            begin = min(max(0, begin), text.length)
            end = min(max(0, end), text.length)

            view.text_edit.setSelection(begin, end)
        }
    }

}