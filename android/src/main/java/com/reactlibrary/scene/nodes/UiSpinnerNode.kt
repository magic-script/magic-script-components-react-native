package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.Utils

// TODO fix: spinner animation is paused after disabling and enabling the screen
// TODO or when inside a grid layout
class UiSpinnerNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    companion object {
        // properties
        private const val PROP_SIZE = "size"
        private const val PROP_VALUE = "value"
    }

    override fun provideView(context: Context): View {
        val view = LayoutInflater.from(context).inflate(R.layout.spinner, null)

        view.post {
            val rotateAnimation = RotateAnimation(0f, 360f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f)

            rotateAnimation.interpolator = LinearInterpolator()
            rotateAnimation.duration = 1000
            rotateAnimation.repeatCount = Animation.INFINITE
            view.findViewById<ImageView>(R.id.spinner).startAnimation(rotateAnimation)
        }

        return view
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_SIZE)) {
            setNeedsRebuild()
        }
    }

    override fun setViewSize() {
        // default dimension
        var widthPx = ViewGroup.LayoutParams.WRAP_CONTENT
        var heightPx = ViewGroup.LayoutParams.WRAP_CONTENT

        if (properties.containsKey(PROP_SIZE)) {
            val sizeArray = properties.getSerializable(PROP_SIZE)
            if (sizeArray is ArrayList<*> && (sizeArray as ArrayList<Double>).size == 2) {
                widthPx = Utils.metersToPx(sizeArray[0].toFloat(), context)
                heightPx = Utils.metersToPx(sizeArray[1].toFloat(), context)
            }
        }
        view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
    }

}