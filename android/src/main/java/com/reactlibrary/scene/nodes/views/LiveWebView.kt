package com.reactlibrary.scene.nodes.views

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

class LiveWebView : WebView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onCheckIsTextEditor(): Boolean {
        return true
    }
}