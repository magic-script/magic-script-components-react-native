package com.reactlibrary.scene.nodes

import android.content.Context
import android.view.LayoutInflater
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.UiNode

class UiTextNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.text, null)
        attachView(view, props)
    }

    override fun update(props: ReadableMap) {
        super.update(props)

    }

}