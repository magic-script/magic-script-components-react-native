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

package com.magicleap.magicscript.scene.nodes.props

import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.shouldEqualInexact
import org.amshove.kluent.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AABBTest {

    @Test
    fun `should consider equal`() {
        val tested = AABB(min = Vector3(-2f, 1f, -0.5f), max = Vector3(1f, 3f, 0.5f))
        val other = AABB(min = Vector3(-2.000007f, 1f, -0.5f), max = Vector3(1f, 3f, 0.5f))

        tested.equalInexact(other) shouldBe true
    }

    @Test
    fun `should not consider equal`() {
        val tested = AABB(min = Vector3(-2f, 1f, -0.5f), max = Vector3(1f, 3f, 0.5f))
        val other = AABB(min = Vector3(-2.07f, 1f, -0.5f), max = Vector3(1f, 3f, 0.5f))

        tested.equalInexact(other) shouldBe false
    }

    @Test
    fun `should return correct size`() {
        val tested = AABB(min = Vector3(-2f, 1f, -0.5f), max = Vector3(1f, 3f, 0.5f))

        tested.size() shouldEqualInexact Vector3(3f, 2f, 1f)
    }

    @Test
    fun `should correctly translate`() {
        val tested = AABB(min = Vector3(-2f, 1f, -0.5f), max = Vector3(1f, 3f, 0.5f))
        val expected = AABB(min = Vector3(-1f, 2f, 0.5f), max = Vector3(2f, 4f, 1.5f))

        val translated = tested.translated(Vector3(1f, 1f, 1f))

        translated shouldEqualInexact expected
    }

    @Test
    fun `should correctly scale`() {
        val tested = AABB(min = Vector3(-2f, 1f, -0.5f), max = Vector3(1f, 3f, 0.5f))
        val expected = AABB(min = Vector3(-1f, 2f, -0.5f), max = Vector3(0.5f, 6f, 0.5f))

        val scaled = tested.scaled(scaleX = 0.5f, scaleY = 2f, scaleZ = 1f)

        scaled shouldEqualInexact expected
    }

    @Test
    fun `should correctly convert to bounding`() {
        val tested = AABB(min = Vector3(-2f, 1f, -0.5f), max = Vector3(1f, 3f, 0.5f))
        val expectedBounds = Bounding(left = -2f, bottom = 1f, right = 1f, top = 3f)

        tested.toBounding2d() shouldEqualInexact expectedBounds
    }

    @Test
    fun `should correctly return center`() {
        val tested = AABB(min = Vector3(-8f, -5f, -5f), max = Vector3(4f, 3f, 9f))

        val center = tested.center()

        center shouldEqualInexact Vector3(-2f, -1f, 2f)
    }

    @Test
    fun `should correctly calculate intersection of two AABBs`() {
        val box1 = AABB(min = Vector3(-4f, -6f, -2f), max = Vector3(4f, 6f, 2f))
        val box2 = AABB(min = Vector3(-8f, -5f, 1f), max = Vector3(2f, 3f, 33f))
        val expected = AABB(min = Vector3(-4f, -5f, 1f), max = Vector3(2f, 3f, 2f))

        box1.intersection(box2) shouldEqualInexact expected
        box2.intersection(box1) shouldEqualInexact expected
    }

}