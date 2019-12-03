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
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.math.Vector3
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.magicleap.magicscript.ar.ModelRenderableLoader
import com.magicleap.magicscript.reactArrayOf
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.Alignment
import junit.framework.Assert.assertEquals
import org.junit.Assert
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

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        modelRenderableLoader = mock()
    }

    @Test
    fun shouldLoadModelWhenPathProvided() {
        val modelPath = "http://sample.com/model.glb"
        val props = reactMapOf(ModelNode.PROP_MODEL_PATH, modelPath)
        val node = ModelNode(props, context, modelRenderableLoader)
        node.build()

        node.attachRenderable()

        verify(modelRenderableLoader).loadRenderable(any(), any())
    }

    @Test
    fun shouldApplyImportScaleWhenImportScalePropertyPresent() {
        val props = reactMapOf(ModelNode.PROP_IMPORT_SCALE, 0.5)
        val expectedLocalScale = Vector3(0.5F, 0.5F, 0.5F)

        val node = ModelNode(props, context, modelRenderableLoader)
        node.build()
        node.attachRenderable()

        assertEquals(expectedLocalScale, node.localScale)
    }

    @Test
    fun shouldApplyCorrectScaleWhenLocalAndImportScalePropertiesPresent() {
        val initialLocalScale = reactArrayOf(2.0, 2.0, 2.0)
        val importScale = 1.5
        val props = reactMapOf(TransformNode.PROP_LOCAL_SCALE, initialLocalScale,
                ModelNode.PROP_IMPORT_SCALE, importScale

        )
        val expectedLocalScale = Vector3(3F, 3F, 3F)

        val node = ModelNode(props, context, modelRenderableLoader)
        node.build()
        node.attachRenderable()

        assertEquals(expectedLocalScale, node.localScale)
    }

    @Test
    fun shouldNotChangeHardcodedAlignment() {
        val props = reactMapOf(TransformNode.PROP_ALIGNMENT, "bottom-left")
        val node = ModelNode(props, context, modelRenderableLoader)

        node.build()

        Assert.assertEquals(Alignment.HorizontalAlignment.CENTER, node.horizontalAlignment)
        Assert.assertEquals(Alignment.VerticalAlignment.CENTER, node.verticalAlignment)
    }

}