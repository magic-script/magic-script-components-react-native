/*
 *  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

package com.magicleap.magicscript.plane

import com.facebook.react.bridge.ReadableMap
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.magicleap.magicscript.TestReactObjectsProvider
import com.nhaarman.mockitokotlin2.*
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.nio.FloatBuffer

@RunWith(MockitoJUnitRunner::class)
class ARPlaneDetectorBridgeTest {

    private lateinit var bridge: ARPlaneDetectorBridge

    @Before
    fun setUp() {
        bridge = ARPlaneDetectorBridge.INSTANCE
        bridge.objectsProvider = TestReactObjectsProvider()
    }

    @After
    fun tearDown() {
        bridge.destroy()
    }

    @Test
    fun `should set bridge to detecting mode`() {
        val config = mock<ReadableMap>()
        bridge.startDetecting(config)

        assertTrue(bridge.isDetecting())
    }

    @Test
    fun `should unset bridge from detecting mode`() {
        bridge.stopDetecting()

        assertFalse(bridge.isDetecting())
    }

    @Test
    fun `should map planes to writable map`() {
        val pose = mock<Pose>()
        val plane = mockPlane(pose)

        bridge.mapPlanesToWritableMap(plane)

        // VERTICES
        verify(pose).transformPoint(floatArrayOf(0f, 0f, 1f))
        verify(pose).transformPoint(floatArrayOf(2f, 0f, 3f))
        // CENTER
        verify(pose).tx()
        verify(pose).ty()
        verify(pose).tz()
        // Type
        verify(plane).type
    }

    @Test
    fun `should invoke tap listener when plane is tapped`() {
        val pose = mock<Pose>()
        val plane = mockPlane(mock())
        val hitTest = mock<HitResult>() {
            on { hitPose } doReturn pose
        }
        val onTappedListener = mock<OnPlaneTapped>()
        bridge.setOnPlaneTappedListener(onTappedListener)

        bridge.onPlaneTapped(plane, hitTest)

        verify(pose).tx()
        verify(pose).ty()
        verify(pose).tz()
        verify(onTappedListener).invoke(any())
    }

    private fun mockPlane(pose: Pose): Plane = mock {
        on { type } doReturn Plane.Type.VERTICAL
        on { polygon } doReturn FloatBuffer.wrap(floatArrayOf(0f, 1f, 2f, 3f))
        on { pose.transformPoint(any()) } doReturn floatArrayOf(0f, 0f, 0f)
        on { centerPose } doReturn pose
    }
}