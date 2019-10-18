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

package com.reactlibrary.scene.nodes

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
import android.widget.LinearLayout
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.ArViewManager
import com.reactlibrary.R
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.font.FontProvider
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.views.InputDialogBuilder
import com.reactlibrary.utils.*
import kotlinx.android.synthetic.main.text_edit.view.*

open class UiTextEditNode(initProps: ReadableMap,
                          context: Context,
                          viewRenderableLoader: ViewRenderableLoader,
                          private val fontProvider: FontProvider
) : UiNode(initProps, context, viewRenderableLoader) {

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
        const val PROP_FONT_PARAMS = "fontParameters"
        const val PROP_TEXT_ENTRY_MODE = "textEntry"
        const val PROP_SCROLLBAR_VISIBILITY = "scrollBarVisibility"

        const val ENTRY_MODE_NORMAL = "normal"
        const val ENTRY_MODE_EMAIL = "email"
        const val ENTRY_MODE_NUMERIC = "numeric"
        const val DEFAULT_TEXT_SIZE = 0.0298 // in meters
        const val DEFAULT_ALIGNMENT = "top-left" // view alignment (pivot)
        const val DEFAULT_SCROLLING = false // scrolling disabled
        const val DEFAULT_CHARACTERS_SPACING = 0.00499
        const val DEFAULT_CHARACTERS_LIMIT = 0.0 // indefinite
        const val SCROLLBAR_VISIBILITY_ALWAYS = "always"
        const val SCROLLBAR_VISIBILITY_AUTO = "auto"
        const val SCROLLBAR_VISIBILITY_OFF = "off"
        val DEFAULT_TEXT_PADDING = arrayListOf(0.003, 0.003, 0.003, 0.003)

        const val CURSOR_BLINK_INTERVAL = 400L // in ms
    }

    var textChangedListener: ((text: String) -> Unit)? = null

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
        properties.putDefaultDouble(PROP_TEXT_SIZE, DEFAULT_TEXT_SIZE)
        properties.putDefaultSerializable(PROP_TEXT_PADDING, DEFAULT_TEXT_PADDING)
        properties.putDefaultString(PROP_ALIGNMENT, DEFAULT_ALIGNMENT)
        properties.putDefaultString(PROP_SCROLLBAR_VISIBILITY, SCROLLBAR_VISIBILITY_AUTO)
        properties.putDefaultDouble(PROP_CHARACTERS_SPACING, DEFAULT_CHARACTERS_SPACING)
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.text_edit, null)
    }

    override fun setupView() {
        // default dimension
        var widthPx = LinearLayout.LayoutParams.WRAP_CONTENT
        var heightPx = LinearLayout.LayoutParams.WRAP_CONTENT

        if (properties.containsKey(PROP_WIDTH)) {
            val widthInMeters = properties.getDouble(PROP_WIDTH).toFloat()
            widthPx = Utils.metersToPx(widthInMeters, context)
        }

        if (properties.containsKey(PROP_HEIGHT)) {
            val heightInMeters = properties.getDouble(PROP_HEIGHT).toFloat()
            heightPx = Utils.metersToPx(heightInMeters, context)
        }
        view.sv_text_edit.layoutParams = LinearLayout.LayoutParams(widthPx, heightPx)

        val fontParams = FontParamsReader.readFontParams(properties, PROP_FONT_PARAMS)
        if (fontParams == null) { // setting a default typeface
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
        val color = PropertiesReader.readColor(props, PROP_HINT_COLOR)
        if (color != null) {
            this.hintColor = color
            refreshVisibleText()
        }
    }

    private fun setTextSize(props: Bundle) {
        if (props.containsKey(PROP_TEXT_SIZE)) {
            val sizeMeters = props.getDouble(PROP_TEXT_SIZE).toFloat()
            val size = Utils.metersToFontPx(sizeMeters, view.context).toFloat()
            view.text_edit.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
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
        val color = PropertiesReader.readColor(props, PROP_TEXT_COLOR)
        if (color != null) {
            this.textColor = color
            refreshVisibleText()
        }
    }

    private fun setCharactersSpacing(props: Bundle) {
        if (props.containsKey(PROP_CHARACTERS_SPACING)) {
            val spacing = props.getDouble(PROP_CHARACTERS_SPACING)
            view.text_edit.letterSpacing = spacing.toFloat()
        }
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
        val padding = PropertiesReader.readPadding(props, PROP_TEXT_PADDING)
        if (padding != null) {
            val top = Utils.metersToPx(padding.top, view.context)
            val right = Utils.metersToPx(padding.right, view.context)
            val bottom = Utils.metersToPx(padding.bottom, view.context)
            val left = Utils.metersToPx(padding.left, view.context)
            view.text_edit.setPadding(left, top, right, bottom)
        }
    }

    private fun setFontParams(props: Bundle) {
        val fontParams = FontParamsReader.readFontParams(props, PROP_FONT_PARAMS) ?: return
        view.text_edit.typeface = fontProvider.provideFont(fontParams)
        view.text_edit.isAllCaps = fontParams.allCaps
    }

    private fun setScrollBarVisibility(props: Bundle) {
        if (props.containsKey(PROP_SCROLLBAR_VISIBILITY)) {
            when (props.getString(PROP_SCROLLBAR_VISIBILITY)) {
                SCROLLBAR_VISIBILITY_AUTO -> {
                    view.sv_text_edit.isVerticalScrollBarEnabled = true
                    view.sv_text_edit.isScrollbarFadingEnabled = true
                }
                SCROLLBAR_VISIBILITY_ALWAYS -> {
                    view.sv_text_edit.isVerticalScrollBarEnabled = true
                    view.sv_text_edit.isScrollbarFadingEnabled = false
                }
                SCROLLBAR_VISIBILITY_OFF -> {
                    view.sv_text_edit.isVerticalScrollBarEnabled = false
                }
            }
        }
    }

    private fun setupViewListeners() {
        view.text_edit.setOnClickListener {
            // disabling parent view is not sufficient
            if (!properties.getBoolean(PROP_ENABLED)) {
                return@setOnClickListener
            }
            val activity = ArViewManager.getActivityRef().get()
            if (activity != null) {
                isSelected = true
                startCursorAnimation()
                showBorder()
                view.text_edit_underline.visibility = View.INVISIBLE
                view.text_edit.setTextColor(textColor)
                showInputDialog(activity)
            }
        }

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
                textChangedListener?.invoke(input)
            }
        }

        builder.setOnCloseListener {
            isSelected = false
            stopCursorAnimation()
            refreshVisibleText()
            hideBorder()
            view.text_edit_underline.visibility = View.VISIBLE
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
            view.text_edit.text = spannable
            view.text_edit.setTextColor(textColor) // clear hint color
            return
        }

        if (text.isEmpty()) { // display hint
            view.text_edit.text = hint
            view.text_edit.setTextColor(hintColor)
            return
        }

        view.text_edit.text = getMaskedText()
        view.text_edit.setTextColor(textColor) // clear hint color
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

}