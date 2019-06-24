package com.reactlibrary.scene.nodes

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.Button
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.UiNode
import com.reactlibrary.utils.metersToPx

class UiButtonNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.button, null) as Button
        val textSizeInMeters = 0.03
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, metersToPx(textSizeInMeters, context).toFloat())
        attachView(view, props)
    }

    override fun update(props: ReadableMap) {
        super.update(props)

    }

}