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

package com.magicleap.magicscript.ar

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.ar.core.HitResult
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer
import com.google.ar.sceneform.ux.TransformationSystem
import com.nhaarman.mockitokotlin2.spy
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ArResourcesManagerTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val manager = ArResourcesManager
    private val arScene = spy<Scene>()
    private val transformationSystem = getTransformationSystem()

    @Test
    fun srtUp() {
        // since it's a singleton, we have to clear it
        manager.clearListeners()
    }

    @Test
    fun `should notify listener every time scene is setup`() {
        var counter = 0
        manager.addArSceneChangedListener(object : ArResourcesProvider.ArSceneChangedListener {
            override fun onSceneChanged(arScene: Scene) {
                counter++
            }
        })

        ArResourcesManager.setupScene(arScene)
        ArResourcesManager.setupScene(arScene)

        counter shouldEqual 2
        ArResourcesManager.getArScene() shouldBe arScene
    }

    @Test
    fun `should not notify removed listener when scene is setup`() {
        var notified = false
        val listener = object : ArResourcesProvider.ArSceneChangedListener {
            override fun onSceneChanged(arScene: Scene) {
                notified = true
            }
        }
        manager.addArSceneChangedListener(listener)
        manager.removeArSceneChangedListener(listener)

        ArResourcesManager.setupScene(arScene)

        notified shouldBe false
    }

    @Test
    fun `should notify listener after ARCore has been loaded`() {
        var notified = false
        manager.addArLoadedListener(object : ArResourcesProvider.ArLoadedListener {
            override fun onArLoaded() {
                notified = true
            }
        })

        ArResourcesManager.onArCoreLoaded()

        notified shouldBe true
        ArResourcesManager.isArLoaded() shouldBe true
    }

    @Test
    fun `should not notify removed listener after ARCore has been loaded`() {
        var notified = false
        val listener = object : ArResourcesProvider.ArLoadedListener {
            override fun onArLoaded() {
                notified = true
            }
        }
        manager.addArLoadedListener(listener)
        manager.removeArLoadedListener(listener)

        ArResourcesManager.onArCoreLoaded()

        notified shouldBe false
    }

    @Test
    fun `should notify listener after transformation system has been setup`() {
        var notified = false
        manager.addTransformationSystemListener(object :
            ArResourcesProvider.TransformationSystemListener {
            override fun onTransformationSystemChanged(transformationSystem: TransformationSystem) {
                notified = true
            }
        })

        ArResourcesManager.setupTransformationSystem(transformationSystem)

        notified shouldBe true
        ArResourcesManager.getTransformationSystem() shouldBe transformationSystem
    }

    @Test
    fun `should not notify removed listener when transformation system has been setup`() {
        var notified = false
        val listener = object : ArResourcesProvider.TransformationSystemListener {
            override fun onTransformationSystemChanged(transformationSystem: TransformationSystem) {
                notified = true
            }
        }
        manager.addTransformationSystemListener(listener)
        manager.removeTransformationSystemListener(listener)

        ArResourcesManager.setupTransformationSystem(transformationSystem)

        notified shouldBe false
    }

    @Test
    fun `should notify listener when camera position updated`() {
        var cameraPosition = Vector3.zero()
        manager.addCameraUpdatedListener(object : ArResourcesProvider.CameraUpdatedListener {
            override fun onCameraUpdated(position: Vector3, state: TrackingState) {
                cameraPosition = position
            }
        })
        val updatedPosition = Vector3(4f, 2f, -1f)

        ArResourcesManager.updateCameraPosition(updatedPosition, TrackingState.TRACKING)

        cameraPosition shouldEqual updatedPosition
        ArResourcesManager.getCameraState() shouldEqual TrackingState.TRACKING
    }

    @Test
    fun `should not notify removed listener when camera position updated`() {
        var notified = false
        val listener = object : ArResourcesProvider.CameraUpdatedListener {
            override fun onCameraUpdated(position: Vector3, state: TrackingState) {
                notified = true
            }
        }
        manager.addCameraUpdatedListener(listener)
        manager.removeCameraUpdatedListener(listener)

        ArResourcesManager.updateCameraPosition(Vector3.one(), TrackingState.TRACKING)

        notified shouldBe false
    }

    @Test
    fun `isPlaneDetectionEnabled should return true if plane detection enabled`() {
        ArResourcesManager.planeDetection = true

        ArResourcesManager.isPlaneDetectionEnabled() shouldBe true
    }

    @Test
    fun `should notify listener when plane is tapped`() {
        var listenerHitResult: HitResult? = null
        manager.addPlaneTapListener(object : ArResourcesProvider.PlaneTapListener {
            override fun onPlaneTap(hitResult: HitResult) {
                listenerHitResult = hitResult
            }
        })
        val tapResult = spy<HitResult>()

        ArResourcesManager.onPlaneTapped(tapResult)

        listenerHitResult shouldEqual tapResult
    }

    @Test
    fun `should not notify removed listener when plane is tapped`() {
        var notified = false
        val listener = object : ArResourcesProvider.PlaneTapListener {
            override fun onPlaneTap(hitResult: HitResult) {
                notified = true
            }
        }
        manager.addPlaneTapListener(listener)
        manager.removePlaneTapListener(listener)
        val hitResult = spy<HitResult>()

        ArResourcesManager.onPlaneTapped(hitResult)

        notified shouldBe false
    }

    private fun getTransformationSystem(): TransformationSystem {
        val displayMetrics = context.resources.displayMetrics
        return TransformationSystem(displayMetrics, FootprintSelectionVisualizer())
    }

}