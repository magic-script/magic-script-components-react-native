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

package com.reactlibrary.utils

import com.google.ar.sceneform.math.Quaternion
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.math.PI

@RunWith(RobolectricTestRunner::class)
class ExtensionsTest {

    // epsilon (for angles operations cannot achieve better precision, why?)
    private val eps = 0.1f

    @Test
    fun shouldConvertQuaternionToEulerAngle() {
        val rotation = Quaternion(0f, 0f, 0.7071068f, 0.7071068f)

        val expectedXAngle = 0f
        val expectedYAngle = 0f
        val expectedZAngle = (PI / 2).toFloat()

        val eulerAngles = rotation.toEulerAngles()

        assertEquals(expectedXAngle, eulerAngles.x, eps)
        assertEquals(expectedYAngle, eulerAngles.y, eps)
        assertEquals(expectedZAngle, eulerAngles.z, eps)
    }

}