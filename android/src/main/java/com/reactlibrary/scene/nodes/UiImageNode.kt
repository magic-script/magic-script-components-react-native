package com.reactlibrary.scene.nodes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.getStringSafely

class UiImageNode(props: ReadableMap, private val context: Context) : UiNode(props, context) {

    companion object {
        // properties
        private const val PROP_FILE_PATH = "filePath"
    }

    override fun applyProperties(props: ReadableMap, update: Boolean) {
        super.applyProperties(props, update)
        setImagePath(props)
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.image, null) as ImageView
    }

    private fun setImagePath(props: ReadableMap) {
        // path to folder in react project
        val path = props.getStringSafely(PROP_FILE_PATH)
        if (path != null) {
            val androidPath = Utils.getImagePath(path, context)
            // e.g. http://localhost:8081/assets/resources/DemoPicture1.jpg
            Glide.with(context)
                    .load(androidPath)
                    .into(view as ImageView)

        }
    }

}