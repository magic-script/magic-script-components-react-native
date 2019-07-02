package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode

class UiSpinnerNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    companion object {
        // properties
        private const val PROP_SIZE = "size"
        private const val PROP_VALUE = "value"
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.spinner, null)
    }

    override fun applyProperties(properties: Bundle, update: Boolean) {
        super.applyProperties(properties, update)
        // TODO
    }

}