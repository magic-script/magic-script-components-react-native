package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.reactlibrary.ar.CubeRenderableBuilder
import com.reactlibrary.ar.RenderableResult
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.putDefaultSerializable

class UiListViewItemNode(initProps: ReadableMap,
                         private val context: Context,
                         private val renderableLoader: CubeRenderableBuilder)
    : TransformNode(initProps, hasRenderable = true, useContentNodeAlignment = true) {

    companion object {
        const val COLOR_TAG = "LIST_VIEW_ITEM_BACKGROUND_COLOR"
        const val PROP_BACKGROUND_COLOR = "backgroundColor"
    }

    private var currentColor: Color? = null

    init {
        properties.putDefaultSerializable(PROP_BACKGROUND_COLOR, arrayListOf<Double>(0.0, 0.0, 0.0, 0.0))
    }

    private fun setBackgroundColor(props: Bundle) {
        val androidColor = PropertiesReader.readColor(props, PROP_BACKGROUND_COLOR)
        if (androidColor != null) {
            currentColor = Color(androidColor)
            val colorNode = Node()
            colorNode.name = COLOR_TAG
            val color = Color(androidColor)
            val bounding = getBounding()
            renderableLoader.buildRenderable(Vector3(bounding.right - bounding.left, bounding.top - bounding.bottom, 0.1f), contentNode.localPosition, color) { result ->
                if (result is RenderableResult.Success) {
                    if(contentNode.children.any { it.name == COLOR_TAG }) {
                        contentNode.children.removeAll { it.name == COLOR_TAG }
                    }
                    contentNode.addChild(colorNode)
                    colorNode.renderable = result.renderable
//                    colorNode.localRotation = localRotation
//                    colorNode.localScale = localScale
//                    colorNode.localPosition = contentNode.localPosition
                }
            }
        }
    }

    override fun getContentBounding(): Bounding {
        return Bounding(-0.5f, -0.5f, 0.5f, 0.5f)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setBackgroundColor(props)
    }
}