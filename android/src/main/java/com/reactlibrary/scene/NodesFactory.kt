package com.reactlibrary.scene

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.reactlibrary.R
import com.reactlibrary.utils.PropertiesReader

/**
 * Utility class with methods that create nodes.
 */
class NodesFactory(private val context: Context) {

    companion object {
        // By default, every 250dp for the view becomes 1 meter for the renderable
        // https://developers.google.com/ar/develop/java/sceneform/create-renderables
        const val DP_TO_METER_RATIO = 250
    }

    private val screenDensity = context.resources.displayMetrics.density;

    fun createViewGroup(props: ReadableMap): UiNode {
        return createUiNode(props)
    }

    fun createButton(props: ReadableMap): UiNode {
        val node = createUiNode(props)
        val view = LayoutInflater.from(context).inflate(R.layout.button, null) as Button

        val textSize = PropertiesReader.Common.getTextSize(props)
        if (textSize != null) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, metersToPx(textSize).toFloat())
        }

        createRenderable(view, props) { renderable ->
            node.renderable = renderable
            view.setOnClickListener { node.clickListener?.invoke() }
        }
        return node
    }

    fun createLabel(props: ReadableMap): UiNode {
        val node = createUiNode(props)
        val view = LayoutInflater.from(context).inflate(R.layout.label, null)
        createRenderable(view, props) { renderable ->
            node.renderable = renderable
            view.setOnClickListener { node.clickListener?.invoke() }
        }
        return node
    }

    fun createImageView(props: ReadableMap): UiNode {
        val node = createUiNode(props)
        val view = LayoutInflater.from(context).inflate(R.layout.image, null) as ImageView

        val source = props.getMap("source")
        val path = source?.getString("uri") ?: ""

        val uri = if (path.startsWith("resources_")) { // for release
            val packageName = context.packageName
            Uri.parse("android.resource://$packageName/drawable/$path")
        } else { // for debug (metro, image located in localhost)
            Uri.parse(path)
        }

        createRenderable(view, props) { renderable ->
            node.renderable = renderable
            view.setOnClickListener { node.clickListener?.invoke() }
        }

        // TODO doesn't work without delay (load after attached?)
        Handler().postDelayed({
            // http://localhost:8081/assets/resources/DemoPicture1.jpg
            Glide.with(context)
                    .load(uri)
                    .into(view)
        }, 3000)

        return node
    }

    private fun createRenderable(view: View, props: ReadableMap, result: (renderable: Renderable) -> Unit) {
        val size = PropertiesReader.Common.getSize(props)
        // convert meters to px (1m is DP_TO_METER_RATIO by default)
        val widthPx = metersToPx(size.width)
        val heightPx = metersToPx(size.height)
        view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)

        // TODO replace delay with callback when AR fragment has been loaded
        // Wait until AR engine was loaded
        // @see: https://github.com/google-ar/sceneform-android-sdk/issues/574
        Handler().postDelayed({
            ViewRenderable
                    .builder()
                    .setView(context, view)
                    .build()
                    .thenAccept { renderable ->
                        result(renderable)
                    }
        }, 1000)
    }

    private fun createUiNode(props: ReadableMap): UiNode {
        val node = UiNode()
        val localPos = PropertiesReader.Common.getPosition(props)
        node.localPosition = localPos
        return node
    }

    // converts ARCore's meters to pixels
    private fun metersToPx(meters: Double): Int {
        return (meters * DP_TO_METER_RATIO * screenDensity).toInt()
    }


}