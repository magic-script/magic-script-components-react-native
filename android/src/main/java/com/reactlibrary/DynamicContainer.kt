package com.reactlibrary

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

// Hacky container based on
// https://github.com/facebook/react-native/issues/8862
// See bug: https://github.com/facebook/react-native/issues/17968
// Source: https://github.com/tlcheah2/test/commit/9a70c2b985769a2a8277219d104b0893b406eb49

class DynamicContainer @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    override fun requestLayout() {
        super.requestLayout()
        post(measureAndLayout)
    }

    private val measureAndLayout = Runnable {
        measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
        layout(left, top, right, bottom)
    }
}