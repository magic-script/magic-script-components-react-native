package com.reactlibrary.scene.nodes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode

class UiToggleNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.toggle, null)
    }


}