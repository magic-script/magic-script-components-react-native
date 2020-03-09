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

package com.magicleap.magicscript.utils

import com.magicleap.magicscript.shouldEqualInexact
import org.amshove.kluent.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class Vector2Test {

    @Test
    fun `should add 2 vectors`() {
        val vector1 = Vector2(-1f, 2.5f)
        val vector2 = Vector2(1f, 1f)

        val sum = vector1 + vector2

        sum shouldEqualInexact Vector2(0f, 3.5f)
    }

    @Test
    fun `should subtract 2 vectors`() {
        val vector1 = Vector2(-1f, 2.5f)
        val vector2 = Vector2(1f, 1f)

        val sum = vector1 - vector2

        sum shouldEqualInexact Vector2(-2f, 1.5f)
    }

    @Test
    fun `should correctly coerce vector values`() {
        val vector = Vector2(-2f, 2f)

        val coercedVector = vector.coerceIn(-1f, 0.5f)

        coercedVector shouldEqualInexact Vector2(-1f, 0.5f)
    }

    @Test
    fun `should correctly coerce at least vector`() {
        val vector = Vector2(-2f, 2f)

        val coercedVector = vector.coerceAtLeast(5f)

        coercedVector shouldEqualInexact Vector2(5f, 5f)
    }

    @Test
    fun `should consider vectors almost equal`() {
        val vector1 = Vector2(-2.000000078f, 3.00000009f)
        val vector2 = Vector2(-2f, 3f)

        vector1.equalInexact(vector2) shouldBe true
    }

    @Test
    fun `should not consider vectors almost equal`() {
        val vector1 = Vector2(-2.000000078f, 3.00000009f)
        val vector2 = Vector2(-2.008f, 3.0006f)

        vector1.equalInexact(vector2) shouldBe false
    }

    @Test
    fun `should not consider vectors almost equal for opposite values`() {
        val vector1 = Vector2(-1f, 1f)
        val vector2 = Vector2(1f, -1f)

        vector1.equalInexact(vector2) shouldBe false
    }

}