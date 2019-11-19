package com.reactlibrary.scene.nodes.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import com.reactlibrary.R
import com.reactlibrary.utils.dp

class MaxHeightScrollView(context: Context, attrs: AttributeSet) : ScrollView(context, attrs) {

    private var maxHeight = 126.dp.toFloat()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightScrollView, 0, 0)
        try {
            maxHeight = typedArray.getDimension(R.styleable.MaxHeightScrollView_maxHeight, 126.dp.toFloat())
        } finally {
            typedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val calculatedHeight = MeasureSpec.makeMeasureSpec(maxHeight.toInt(), MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, calculatedHeight)
    }
}