package com.magicleap.magicscript.plane

import com.facebook.react.bridge.ReadableMap
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.magicleap.magicscript.TestReactObjectsProvider
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
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
        val hitTest = mock<HitResult>()
        val onTappedListener = mock<OnPlaneTapped>()
        bridge.setOnPlaneTappedListener(onTappedListener)
        whenever(hitTest.hitPose).thenReturn(pose)

        bridge.onPlaneTapped(plane, hitTest)

        verify(pose).tx()
        verify(pose).ty()
        verify(pose).tz()
        verify(onTappedListener).invoke(any())
    }

    private fun mockPlane(pose: Pose): Plane {
        val plane = mock<Plane>()
        whenever(plane.type).thenReturn(Plane.Type.VERTICAL)
        whenever(plane.polygon).thenReturn(FloatBuffer.wrap(floatArrayOf(0f, 1f, 2f, 3f)))
        whenever(pose.transformPoint(any())).thenReturn(floatArrayOf(0f, 0f, 0f))
        whenever(plane.centerPose).thenReturn(pose)
        return plane
    }
}