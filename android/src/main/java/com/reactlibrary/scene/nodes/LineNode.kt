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
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.reactlibrary.ar.CubeRenderableBuilder
import com.reactlibrary.ar.RenderableResult
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.utils.PropertiesReader

// Node that represents a chain of lines
class LineNode(initProps: ReadableMap,
               private val context: Context,
               private val cubeRenderableBuilder: CubeRenderableBuilder) :
        TransformNode(initProps, hasRenderable = true, useContentNodeAlignment = false) {

    companion object {
        // properties
        const val PROP_POINTS = "points"
        const val PROP_COLOR = "color"

        private const val LINE_THICKNESS = 0.002f // in meters
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_POINTS) || props.containsKey(PROP_COLOR)) {
            // cannot update renderable before [renderableRequested],
            // because Sceneform may be uninitialized yet
            // (loadRenderable may have not been called)
            if (renderableRequested) {
                drawLines()
            }
        }
    }

    override fun loadRenderable() {
        drawLines()
    }

    override fun setAlignment(props: Bundle) {
        // according to Lumin we cannot change alignment for line
    }

    private fun drawLines() {
        // clear the old line segments in case of update
        for (i in contentNode.children.size - 1 downTo 0) {
            contentNode.removeChild(contentNode.children[i])
        }

        // draw each line segment
        val points = PropertiesReader.readVectorsList(properties, PROP_POINTS)
        val androidColor = PropertiesReader.readColor(properties, PROP_COLOR)
        val color = if (androidColor != null) Color(androidColor) else Color(1f, 1f, 1f)

        var idx = 0
        while (idx + 1 < points.size) {
            val start = points[idx]
            val end = points[idx + 1]
            drawLineSegment(start, end, color)
            idx++
        }
    }

    private fun drawLineSegment(start: Vector3, end: Vector3, color: Color) {
        val lineSegment = Node()
        val diff = Vector3.subtract(start, end)
        val direction = diff.normalized()
        val rotation = Quaternion.lookRotation(direction, Vector3.up())
        val lineSize = Vector3(LINE_THICKNESS, LINE_THICKNESS, diff.length())
        cubeRenderableBuilder.buildRenderable(lineSize, Vector3.zero(), color) { result ->
            if (result is RenderableResult.Success) {
                contentNode.addChild(lineSegment)
                lineSegment.renderable = result.renderable
                lineSegment.localPosition = Vector3.add(start, end).scaled(0.5f)
                lineSegment.localRotation = rotation
            }
        }
    }

}