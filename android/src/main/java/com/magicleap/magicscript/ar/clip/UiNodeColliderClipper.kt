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

package com.magicleap.magicscript.ar.clip

import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.unaryMinus

class UiNodeColliderClipper : Clipper {

    override fun applyClipBounds(node: TransformNode, clipBounds: AABB?) {
        if (!node.isVisible || clipBounds == null) {
            return
        }
        node.contentNode.collisionShape = createClippedCollider(node, clipBounds)
    }

    private fun createClippedCollider(node: TransformNode, clipBounds: AABB): Box {
        val nodeBounds = node.getBounding()
        if (nodeBounds.intersection(clipBounds).equalInexact(AABB())) {
            // no intersection in 3d space, returning empty box
            return Box(Vector3.zero(), Vector3.zero())
        }

        val size = nodeBounds.size()

        val pivotOffsetX = if (node.useContentNodeAlignment) {
            0f
        } else {
            -node.horizontalAlignment.centerOffset * size.x
        }

        val pivotOffsetY = if (node.useContentNodeAlignment) {
            0f
        } else {
            -node.verticalAlignment.centerOffset * size.y
        }

        val scaleX = node.localScale.x * node.contentNode.localScale.x
        val scaleY = node.localScale.y * node.contentNode.localScale.y

        val nodeCollisionShape = Bounding(
            -size.x / 2 * scaleX,
            -size.y / 2 * scaleY,
            size.x / 2 * scaleX,
            size.y / 2 * scaleY
        ).translated(Vector2(pivotOffsetX, pivotOffsetY))

        val clipCollisionShape = clipBounds
            .translated(-node.getContentPosition())
            .toBounding2d()

        val intersection = nodeCollisionShape.intersection(clipCollisionShape)

        // collision shape is not aware of scale, we need to scale to original position
        val sizeX = if (scaleX > 0) intersection.size().x / scaleX else 0F
        val sizeY = if (scaleY > 0) intersection.size().y / scaleY else 0F
        val collisionShapeSize = Vector3(sizeX, sizeY, 0F)

        var centerX = 0F
        var centerY = 0F

        if (!node.useContentNodeAlignment && scaleX > 0 && scaleY > 0) {
            centerX = intersection.center().x / scaleX
            centerY = intersection.center().y / scaleY
        }

        val collisionShapeCenter = Vector3(centerX, centerY, 0F)

        return Box(collisionShapeSize, collisionShapeCenter)
    }

}