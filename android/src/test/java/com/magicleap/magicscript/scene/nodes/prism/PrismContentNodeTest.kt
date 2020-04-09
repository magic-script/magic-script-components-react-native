/*
 * Copyright (c) 2020 Magic Leap, Inc. All Rights Reserved
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

package com.magicleap.magicscript.scene.nodes.prism

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer
import com.google.ar.sceneform.ux.TransformationSystem
import com.magicleap.magicscript.ar.renderable.CubeRenderableBuilder
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PrismContentNodeTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val cubeBuilder: CubeRenderableBuilder = mock()

    @Test
    fun `edit mode should be inactive by default`() {
        val tested = buildNode(size = Vector3.one())

        tested.editModeActive shouldBe false
    }

    @Test
    fun `should build cube renderable with initial size`() {
        val size = Vector3(0.8f, 0.4f, 0.2f)
        buildNode(size)

        verify(cubeBuilder).buildRenderable(argThat {
            cubeSize == size
            cubeCenter == Vector3.zero()
        })
    }

    @Test
    fun `should rebuild cube renderable when size changed`() {
        val tested = buildNode(size = Vector3.one())
        val sizeToUpdate = Vector3(2.4f, 1.2f, 0.5f)

        tested.setSize(sizeToUpdate)

        verify(cubeBuilder).buildRenderable(argThat {
            cubeSize == sizeToUpdate
        })
    }

    @Test
    fun `should have disabled default transformation controllers`() {
        val tested = buildNode(size = Vector3.one())

        tested.translationController.isEnabled shouldBe false
        tested.scaleController.isEnabled shouldBe false
        tested.rotationController.isEnabled shouldBe false
    }

    @Test
    fun `should cancel cube load request on destroy`() {
        val tested = buildNode(size = Vector3.one())

        tested.onDestroy()

        verify(cubeBuilder).cancel(any())
    }

    private fun buildNode(size: Vector3): PrismContentNode {
        val displayMetrics = context.resources.displayMetrics
        val transformSystem = TransformationSystem(displayMetrics, FootprintSelectionVisualizer())
        return PrismContentNode(transformSystem, cubeBuilder, size)
    }

}