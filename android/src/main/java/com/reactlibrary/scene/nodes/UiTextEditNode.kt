package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R

open class UiTextEditNode(props: ReadableMap, context: Context) : UiTextNode(props, context) {

    companion object {
        // properties

    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.text_edit, null)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

    }

}