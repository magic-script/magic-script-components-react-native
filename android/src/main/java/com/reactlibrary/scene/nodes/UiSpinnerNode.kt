package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
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
        // firstly override width and height, because spinner uses the [size] instead
        setSize(props)
        super.applyProperties(props)
        //apply other properties here
    }

    private fun setSize(props: Bundle) {
        if (props.containsKey(PROP_SIZE)) {
            val size = properties.getDouble(PROP_SIZE)
            props.putDouble(PROP_WIDTH, size)
            props.putDouble(PROP_HEIGHT, size)
        }
    }


}