/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebView
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.video.VideoNode
import com.reactlibrary.utils.Vector2
import com.reactlibrary.utils.logMessage
import com.reactlibrary.utils.putDefaultDouble

class UIWebViewNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader
) : UiNode(initProps, context, viewRenderableLoader) {

    companion object {
        // properties
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
        const val PROP_URL = "url"
        const val PROP_ACTION = "action"
        const val PROP_SCROLL_BY = "scrollBy"

        const val ACTION_BACK = "back"
        const val ACTION_FORWARD = "forward"
        const val ACTION_RELOAD = "reload"
    }

    init {
        properties.apply {
            putDefaultDouble(PROP_WIDTH, 1.0)
            putDefaultDouble(PROP_HEIGHT, 1.0)
        }
    }

    override fun provideView(context: Context) =
        LayoutInflater.from(context).inflate(R.layout.web_view, null)

    override fun provideDesiredSize(): Vector2 {
        val height = properties.getDouble(PROP_HEIGHT, WRAP_CONTENT_DIMENSION.toDouble())
        val width = properties.getDouble(PROP_WIDTH, WRAP_CONTENT_DIMENSION.toDouble())
        return Vector2(width.toFloat(), height.toFloat())
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_WIDTH) || props.containsKey(PROP_HEIGHT)) {
            setNeedsRebuild()
        }

        setUrl(props)
        setScrollBy(props)
        setAction(props)
    }

    private fun setUrl(props: Bundle) {
        if (props.containsKey(PROP_URL)) {
            (view as WebView).loadUrl(props.getString(PROP_URL))
        }
    }

    private fun setScrollBy(props: Bundle) {
        if (props.containsKey(PROP_SCROLL_BY)) {
            (view as WebView).scrollBy(0, props.getInt(PROP_SCROLL_BY))
        }
    }

    private fun setAction(props: Bundle) {
        if (props.containsKey(PROP_ACTION)) {
            val action = props.getString(VideoNode.PROP_ACTION)

            try {
                when (action) {
                    ACTION_BACK -> (view as WebView).goBack()
                    ACTION_FORWARD -> (view as WebView).goForward()
                    ACTION_RELOAD -> (view as WebView).reload()
                }
            } catch (e: Exception) {
                logMessage("Error setting WebView action $action: $e", true)
            }
        }
    }

}
