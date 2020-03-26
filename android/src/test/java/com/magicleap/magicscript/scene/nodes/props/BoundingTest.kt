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

import com.magicleap.magicscript.shouldEqualInexact
import com.magicleap.magicscript.utils.Vector2
import org.amshove.kluent.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BoundingTest {

    @Test
    fun `should consider equal`() {
        val boundsA = Bounding(left = -4f, bottom = -2f, right = 3f, top = 6f)
        val boundsB = Bounding(left = -4.000006f, bottom = -2f, right = 3f, top = 6f)

        boundsA.equalInexact(boundsB) shouldBe true
    }

    @Test
    fun `should not consider equal`() {
        val boundsA = Bounding(left = -4f, bottom = -2f, right = 3f, top = 6f)
        val boundsB = Bounding(left = -4.01f, bottom = -2f, right = 3f, top = 6f)

        boundsA.equalInexact(boundsB) shouldBe false
    }

    @Test
    fun `should return correct center`() {
        val bounds = Bounding(left = -4f, bottom = -2f, right = 3f, top = 6f)

        bounds.center() shouldEqualInexact Vector2(-0.5f, 2f)
    }

    @Test
    fun `should return correct size`() {
        val bounds = Bounding(left = -4f, bottom = -2f, right = 3f, top = 6f)
        val expectedSize = Vector2(7f, 8f)

        bounds.size() shouldEqualInexact expectedSize
    }

    @Test
    fun `should correctly translate`() {
        val bounds = Bounding(left = -4f, bottom = -2f, right = 3f, top = 6f)
        val expected = Bounding(left = -5f, bottom = -1f, right = 2f, top = 7f)

        bounds.translated(Vector2(-1f, 1f)) shouldEqualInexact expected
    }

    @Test
    fun `should calculate intersection correctly`() {
        val boundsA = Bounding(left = -4f, bottom = -2f, right = 3f, top = 6f)
        val boundsB = Bounding(left = -10f, bottom = -1f, right = 10f, top = 8f)

        val intersection = boundsA.intersection(boundsB)

        intersection shouldEqualInexact Bounding(-4f, -1f, 3f, 6f)
    }

    @Test
    fun `should return empty intersection`() {
        val boundsA = Bounding(left = -4f, bottom = -2f, right = 3f, top = 6f)
        val boundsB = Bounding(left = -40f, bottom = -2f, right = -30f, top = 6f)

        val intersection = boundsA.intersection(boundsB)

        intersection shouldEqualInexact Bounding(0f, 0f, 0f, 0f)
    }

}