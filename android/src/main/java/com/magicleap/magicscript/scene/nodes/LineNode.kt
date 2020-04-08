/*
 * Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Ray
import com.google.ar.sceneform.collision.RayHit
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.Renderable
import com.magicleap.magicscript.ar.BoundingBox
import com.magicleap.magicscript.ar.renderable.CubeRenderableBuilder
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.utils.*

// Node that represents a chain of lines
class LineNode(
    initProps: ReadableMap,
    private val cubeRenderableBuilder: CubeRenderableBuilder
) : TransformNode(initProps, useContentNodeAlignment = false) {

    companion object {
        // properties
        const val PROP_POINTS = "points"
        const val PROP_COLOR = "color"

        private const val LINE_THICKNESS = 0.002f // in meters
    }

    override var clipBounds: AABB?
        get() = super.clipBounds
        set(value) {
            super.clipBounds = value
            value?.let { applyClipBounds(it) }
        }

    private val colorDefault = Color(1f, 1f, 1f)
    private var baseCube: Renderable? = null // reused by all line segments
    private var linesBounding = AABB()
    private var clipBox = BoundingBox(
        Vector3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE),
        Vector3.zero()
    )

    private val pointsList = mutableListOf<Vector3>()
    private var cubeLoadRequest: CubeRenderableBuilder.LoadRequest? = null

    override fun build() {
        super.build()
        val androidColor = properties.readColor(PROP_COLOR)
        val color = if (androidColor != null) Color(androidColor) else colorDefault
        loadBaseCube(color)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_POINTS) || props.containsKey(PROP_COLOR)) {
            drawLines(clipBox)
        }
    }

    override fun getContentBounding(): AABB {
        val minEdge = linesBounding.min + contentNode.localPosition
        val maxEdge = linesBounding.max + contentNode.localPosition

        return AABB(minEdge, maxEdge)
    }

    override fun setAlignment(props: Bundle) {
        // according to Lumin we cannot change alignment for line
    }

    override fun onVisibilityChanged(visibility: Boolean) {
        if (visibility) {
            contentNode.children.forEach {
                it.renderable = baseCube
            }
        } else {
            contentNode.children.forEach {
                it.renderable = null
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelCubeLoadRequest()
    }

    private fun applyClipBounds(clipBounds: AABB) {
        val localBounds = clipBounds.translated(-getContentPosition())
        val center = (localBounds.min + localBounds.max) / 2f
        clipBox = BoundingBox(localBounds.size(), center)

        drawLines(clipBox)
    }

    private fun drawLines(clipBox: BoundingBox) {
        // clear old lines in case of points list update
        clearLines()

        // draw each line segment
        val points = properties.readVectorsList(PROP_POINTS)

        var idx = 0
        while (idx + 1 < points.size) {
            val clipped = clipLineSegment(points[idx], points[idx + 1], clipBox)
            if (clipped != null) {
                val (start, end) = clipped
                drawLineSegment(start, end)
                this.pointsList.add(start)
                this.pointsList.add(end)
            }
            idx++
        }

        updateLinesBounding()
    }

    private fun clipLineSegment(start: Vector3, end: Vector3, clipBox: BoundingBox)
            : Pair<Vector3, Vector3>? {
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

    private fun drawLineSegment(start: Vector3, end: Vector3) {
        val lineSegment = Node()
        val diff = Vector3.subtract(start, end)
        val direction = diff.normalized()
        val rotation = Quaternion.lookRotation(direction, Vector3.up())
        val scaleZ = diff.length() / LINE_THICKNESS

        contentNode.addChild(lineSegment)

        if (isVisible && baseCube != null) {
            lineSegment.renderable = baseCube
        }

        lineSegment.localScale = Vector3(1f, 1f, scaleZ)
        lineSegment.localPosition = Vector3.add(start, end).scaled(0.5f)
        lineSegment.localRotation = rotation
    }

    private fun loadBaseCube(color: Color) {
        val cubeSize = Vector3(LINE_THICKNESS, LINE_THICKNESS, LINE_THICKNESS)
        cubeLoadRequest =
            CubeRenderableBuilder.LoadRequest(cubeSize, Vector3.zero(), color) { result ->
                if (result is DataResult.Success) {
                    baseCube = result.data
                    if (isVisible) {
                        contentNode.children.forEach {
                            it.renderable = baseCube
                        }
                    }
                }
            }.also {
                cubeRenderableBuilder.buildRenderable(it)
            }
    }

    private fun updateLinesBounding() {
        linesBounding = Utils.findMinimumBounding(pointsList)
    }

    private fun clearLines() {
        for (i in contentNode.children.size - 1 downTo 0) {
            contentNode.removeChild(contentNode.children[i])
        }
        pointsList.clear()
    }

    private fun cancelCubeLoadRequest() {
        cubeLoadRequest?.let { it ->
            cubeRenderableBuilder.cancel(it)
        }
    }

}