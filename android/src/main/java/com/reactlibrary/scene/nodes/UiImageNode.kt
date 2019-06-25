package com.reactlibrary.scene.nodes

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.view.LayoutInflater
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.BuildConfig
import com.reactlibrary.R
import com.reactlibrary.scene.UiNode

class UiImageNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.image, null) as ImageView
        val imagePath = getImagePath(props, context)

        // TODO doesn't work without delay (load starts after view is attached?)
        Handler().postDelayed({
            // http://localhost:8081/assets/resources/DemoPicture1.jpg
            Glide.with(context)
                    .load(imagePath)
                    .into(view)
        }, 3000)

        attachView(view, props)
    }

    override fun update(props: ReadableMap, useDefaults: Boolean) {
        super.update(props, useDefaults)

    }

    private fun getImagePath(props: ReadableMap, context: Context): Uri {
        // e.g. resources\DemoPicture1.jpg
        val filePath = props.getString("filePath") ?: ""

        return if (BuildConfig.DEBUG) {
            Uri.parse("http://localhost:8081/assets/$filePath")
        } else {
            val packageName = context.packageName
            val resourcesPath = "android.resource://$packageName/drawable/"
            // convert string to format: resources_demopicture1
            val normalizedPath = filePath.toLowerCase().replace("/", "_")
            Uri.parse(normalizedPath)
        }
    }

}