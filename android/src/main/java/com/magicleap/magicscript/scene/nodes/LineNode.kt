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

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Ray
import com.google.ar.sceneform.collision.RayHit
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.Renderable
import com.magicleap.magicscript.ar.CubeRenderableBuilder
import com.magicleap.magicscript.ar.RenderableResult
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.utils.BoundingBox
import com.magicleap.magicscript.utils.PropertiesReader
import com.magicleap.magicscript.utils.minus
import kotlin.Float.Companion.MAX_VALUE
import kotlin.math.max
import kotlin.math.min

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

    private var renderableCopy: Renderable? = null
    private var linesBounding = Bounding()
    private var clipBox = BoundingBox(Vector3(MAX_VALUE, MAX_VALUE, MAX_VALUE), Vector3())

    init {
        visibilityObservers.add {
            if(isVisible) {
                contentNode.renderable = renderableCopy
            } else {
                contentNode.renderable = null
            }
        }
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_POINTS) || props.containsKey(PROP_COLOR)) {
            // cannot update renderable before [renderableRequested],
            // because Sceneform may be uninitialized yet
            // (loadRenderable may have not been called)
            if (renderableRequested) {
                loadRenderable()
            }
        }
    }

    override fun loadRenderable() {
        drawLines(clipBox)
        updateLinesBounding()
    }


    override fun getContentBounding(): Bounding {
        return Bounding(
                linesBounding.left + contentNode.localPosition.x,
                linesBounding.bottom + contentNode.localPosition.y,
                linesBounding.right + contentNode.localPosition.x,
                linesBounding.top + contentNode.localPosition.y
        )
    }

    override fun setClipBounds(clipBounds: Bounding) {
        val localBounds = clipBounds.translate(-getContentPosition())

        val center = localBounds.center().toVector3()
        val size = localBounds.size().toVector3()
        clipBox = BoundingBox(size, center)
        drawLines(clipBox)
    }

    override fun setAlignment(props: Bundle) {
        // according to Lumin we cannot change alignment for line
    }

    private fun drawLines(clipBox: BoundingBox) {
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
            val clipped = clipLineSegment(points[idx], points[idx + 1], clipBox)
            if (clipped != null) {
                val (start, end) = clipped
                drawLineSegment(start, end, color)
            }
            idx++
        }
    }

    private fun clipLineSegment(start: Vector3, end: Vector3, clipBox: BoundingBox): Pair<Vector3, Vector3>? {
        var collisions = 0
        val hit = RayHit()

        val startRay = Ray(start, end - start)
        val startClipped = if (clipBox.getRayIntersection(startRay, hit)) {
            collisions++
            hit.point
        } else {
            start
        }

        val endRay = Ray(end, start - end)
        val endClipped = if (clipBox.getRayIntersection(endRay, hit)) {
            collisions++
            hit.point
        } else {
            end
        }

        if (collisions < 2) {
            // Line completely outside of clip box.
            return null
        }

        return Pair(startClipped, endClipped)
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
                if(isVisible) {
                    lineSegment.renderable = result.renderable
                    renderableCopy = result.renderable
                } else {
                    renderableCopy = result.renderable
                }
                lineSegment.localPosition = Vector3.add(start, end).scaled(0.5f)
                lineSegment.localRotation = rotation
            }
        }
    }

    // For some reason Utils.calculateSumBounds fails for this node.
    // Even if it didn't, we still want to cache content bounding,
    // because clipping changes content size.
    private fun updateLinesBounding() {
        val points = PropertiesReader.readVectorsList(properties, PROP_POINTS)
        linesBounding = if (points.isEmpty()) {
            Bounding()
        } else {
            Bounding(MAX_VALUE, MAX_VALUE, -MAX_VALUE, -MAX_VALUE)
        }

        for (p in points) {
            linesBounding.left = min(linesBounding.left, p.x)
            linesBounding.right = max(linesBounding.right, p.x)
            linesBounding.top = max(linesBounding.top, p.y)
            linesBounding.bottom = min(linesBounding.bottom, p.y)
        }
    }
}