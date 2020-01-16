/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.ar.ModelRenderableLoader
import com.magicleap.magicscript.ar.RenderableAnimator
import com.magicleap.magicscript.reactArrayOf
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import junit.framework.Assert.assertEquals
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class ModelNodeTest {

    private lateinit var context: Context
    private lateinit var modelRenderableLoader: ModelRenderableLoader
    private lateinit var renderableAnimator: RenderableAnimator

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        modelRenderableLoader = mock()
        renderableAnimator = mock()
    }

    @Test
    fun `should load model when path provided`() {
        val modelPath = "http://sample.com/model.glb"
        val props = reactMapOf(ModelNode.PROP_MODEL_PATH, modelPath)
        val node = createNode(props)

        node.build()
        node.attachRenderable()

        verify(modelRenderableLoader).loadRenderable(any(), any())
    }

    @Test
    fun `should apply import scale on a content node`() {
        // importScale should be applied to the contentNode, because localScale
        // of a node itself may be changed by any layout (layouts may scale children nodes
        // by changing their localScale)
        val props = reactMapOf(
            ModelNode.PROP_IMPORT_SCALE, 2.0,
            TransformNode.PROP_LOCAL_SCALE, reactArrayOf(0.5f, 0.5f, 0.5f)
        )
        val expectedNodeScale = Vector3(0.5f, 0.5f, 0.5f)
        val expectedContentNodeScale = Vector3(2f, 2f, 2f)
        val node = createNode(props)

        node.build()
        node.attachRenderable()

        assertEquals(expectedContentNodeScale, node.contentNode.localScale)
        assertEquals(expectedNodeScale, node.localScale)
    }

    @Test
    fun `should not change hardcoded alignment`() {
        val props = reactMapOf(TransformNode.PROP_ALIGNMENT, "bottom-left")
        val node = createNode(props)

        node.build()

        assertEquals(Alignment.HorizontalAlignment.CENTER, node.horizontalAlignment)
        assertEquals(Alignment.VerticalAlignment.CENTER, node.verticalAlignment)
    }

    @Test
    fun `should hide the node when its center is outside of clip bounds`() {
        val props = reactMapOf(
            TransformNode.PROP_LOCAL_POSITION, reactArrayOf(-4f, -4f, 0f)
        )
        val node = createNode(props)
        node.build()
        val clipBounds = Bounding(left = 2f, bottom = -1f, right = 5f, top = 1f)

        node.setClipBounds(clipBounds)

        node.isVisible shouldEqual false
    }

    private fun createNode(props: JavaOnlyMap): ModelNode {
        return ModelNode(props, context, modelRenderableLoader, renderableAnimator)
    }

}