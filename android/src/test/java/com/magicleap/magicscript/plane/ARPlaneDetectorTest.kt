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

import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import kotlin.reflect.typeOf

@RunWith(RobolectricTestRunner::class)
class ARPlaneDetectorTest {

    @Mock private lateinit var bridge: ARPlaneDetectorBridge
    @Mock private lateinit var eventsManager: ARPlaneDetectorEventsManager
    private lateinit var detector: ARPlaneDetector

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val context = ReactApplicationContext(ApplicationProvider.getApplicationContext())
        detector = ARPlaneDetector(context, eventsManager, bridge)
    }

    @Test
    fun `should destroy bridge objects when on activity is destroyed`() {
        detector.onHostDestroy()

        verify(bridge).destroy()
    }

    @Test
    fun `should start detecting`() {
        val mockedMap = mock<ReadableMap>()

        detector.startDetecting(mockedMap)

        verify(bridge).startDetecting(mockedMap)
    }

    @Test
    fun `should stop detecting`() {
        detector.stopDetecting()

        verify(bridge).stopDetecting()
    }

    @Test
    fun `should add on plane detected event handler`() {
        detector.addOnPlaneDetectedEventHandler()

        verify(bridge).setOnPlanesAddedListener(any())
    }

    @Test
    fun `should add on plane removed event handler`() {
        detector.addOnPlaneRemovedEventHandler()

        verify(bridge).setOnPlanesRemovedListener(any())
    }

    @Test
    fun `should add on plane updated event handler`() {
        detector.addOnPlaneUpdatedEventHandler()

        verify(bridge).setOnPlanesUpdatedListener(any())
    }

    @Test
    fun `should add on plane tapped event handler`() {
        detector.addOnPlaneTappedEventHandler()

        verify(bridge).setOnPlaneTappedListener(any())
    }

    @Test
    fun `should get all planes`() {
        val callback = mock<Callback>()
        val config = mock<ReadableMap>()

        detector.getAllPlanes(config, callback)

        verify(bridge).getAllPlanes(config, callback)
    }
}