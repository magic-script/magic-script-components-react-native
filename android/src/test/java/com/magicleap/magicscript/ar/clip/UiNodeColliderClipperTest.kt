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

package com.magicleap.magicscript.ar.clip

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.UiNodeBuilder
import com.magicleap.magicscript.scene.nodes.base.UiNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.shouldEqualInexact
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UiNodeColliderClipperTest {

    private lateinit var appContext: Context
    private lateinit var clipper: UiNodeColliderClipper

    @Before
    fun setUp() {
        this.appContext = ApplicationProvider.getApplicationContext<Context>()
        this.clipper = UiNodeColliderClipper()
    }

    /*
          ^
          |
          |
          |
     +---------+
     |    |    | Node bounds
     |    |    |
     |    |    |
     |  +---+  |
     |  | | |  |
     |  | C |  |
     |  | | |  |
 ----+--+---+--+--------------->
          |
          |
  */
    @Test
    fun `should correctly create clipped collider`() {
        val clipBounds = AABB(min = Vector3(-1f, 0f, -1f), max = Vector3(1f, 3f, 1f))
        val node = UiNodeBuilder(appContext)
            .withSize(4f, 6f)
            .withAlignment("bottom-center")
            .build() as UiNode

        clipper.applyClipBounds(node, clipBounds)

        val collider = node.contentNode.collisionShape
        collider shouldBeInstanceOf Box::class
        (collider as Box).center shouldEqualInexact Vector3(0f, 1.5f, 0f)
        collider.size shouldEqualInexact Vector3(2f, 3f, 0f)
    }

    /*
               ^
               |
               |
               |
          +---------+
          |    |    | Clip bounds
          |    |    |
          |    |    |
          |  +---+  |
          |  | | |  |
          |  | N |  |
          |  | | |  |
      ----+--+---+--+--------------->
               |
               |
   */
    @Test
    fun `should create full size collider if node bounds inside clip bounds and using content node alignment`() {
        val clipBounds = AABB(min = Vector3(-2f, 0f, 0f), max = Vector3(2f, 6f, 0f))
        val node = UiNodeBuilder(appContext, useContentNodeAlignment = true)
            .withSize(2f, 3f)
            .withAlignment("bottom-center")
            .build() as UiNode

        clipper.applyClipBounds(node, clipBounds)

        val collider = node.contentNode.collisionShape
        // center at (0,0) because we use content node for alignment,
        // so collision shape center is relative to it
        collider shouldBeInstanceOf Box::class
        (collider as Box).center shouldEqualInexact Vector3(0f, 0f, 0f)
        collider.size shouldEqualInexact Vector3(2f, 3f, 0f)
    }

    @Test
    fun `should create empty collider when z position outside clip bounds`() {
        val clipBounds = AABB(min = Vector3(-1f, 0f, -1f), max = Vector3(1f, 3f, 1f))
        val node = UiNodeBuilder(appContext)
            .withSize(4f, 6f)
            .withAlignment("bottom-center")
            .build() as UiNode
        node.localPosition = Vector3(0f, 0f, -2f)

        clipper.applyClipBounds(node, clipBounds)

        val collider = node.contentNode.collisionShape
        collider shouldBeInstanceOf Box::class
        (collider as Box).center shouldEqualInexact Vector3(0f, 0f, 0f)
        collider.size shouldEqualInexact Vector3(0f, 0f, 0f)
    }

    /*
          ^
          |
          +---------------------+
          |                     | Node bounds (scaled)
          |                     |
          |                     |
          |                     |
          +-----------+         |
          |           |         |
          |           |         |
          |Clip bounds|         |
          |           |         |
    ------------------+---------+------->
          |
          |
          |
     */
    @Test
    fun `should create correct collider when node is scaled`() {
        val clipBounds = AABB(min = Vector3(0f, 0f, 0f), max = Vector3(2f, 2f, 0f))
        val node = UiNodeBuilder(appContext)
            .withSize(1f, 1f)
            .withAlignment("bottom-left")
            .build() as UiNode
        node.localScale = Vector3(4f, 4f, 1f)

        clipper.applyClipBounds(node, clipBounds)

        val collider = node.contentNode.collisionShape
        collider shouldBeInstanceOf Box::class
        (collider as Box).center shouldEqualInexact Vector3(0.25f, 0.25f, 0f)
        collider.size shouldEqualInexact Vector3(0.5f, 0.5f, 0f)
    }

}