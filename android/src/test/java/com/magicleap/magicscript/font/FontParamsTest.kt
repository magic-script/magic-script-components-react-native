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

package com.magicleap.magicscript.font

import android.os.Bundle
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FontParamsTest {

    @Test
    fun `should return proper font params from bundle`() {
        val bundle = Bundle().apply {
            putString("style", "italic")
            putString("weight", "bold")
            val fontParamsBundle = Bundle().apply {
                putDouble("fontSize", 0.2)
                putBoolean("allCaps", true)
            }
            putBundle("fontParameters", fontParamsBundle)
        }

        val fontParams = FontParams.fromBundle(bundle)

        fontParams.style shouldEqual FontStyle.ITALIC
        fontParams.weight shouldEqual FontWeight.BOLD
        fontParams.fontSize shouldEqual 0.2
        fontParams.allCaps shouldBe true
    }

    @Test
    fun `should return null font params for empty bundle`() {
        val bundle = Bundle()

        val fontParams = FontParams.fromBundle(bundle)

        fontParams.style shouldBe null
        fontParams.weight shouldBe null
        fontParams.fontSize shouldBe null
        fontParams.allCaps shouldBe null
        fontParams.tracking shouldBe null
    }

}