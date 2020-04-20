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

package com.magicleap.magicscript.ar

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.ar.core.HitResult
import com.google.ar.core.Pose
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer
import com.google.ar.sceneform.ux.TransformationSystem
import com.magicleap.magicscript.shouldEqualInexact
import com.magicleap.magicscript.utils.Utils
import com.magicleap.magicscript.utils.getRotation
import com.magicleap.magicscript.utils.getTranslationVector
import com.nhaarman.mockitokotlin2.spy
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ArResourcesManagerTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val manager = ArResourcesManager()
    private val arScene = spy<Scene>()
    private val transformationSystem = getTransformationSystem()

    @Test
    fun `should notify listener every time scene is setup`() {
        var counter = 0
        manager.addArSceneChangedListener(object : ArResourcesProvider.ArSceneChangedListener {
            override fun onSceneChanged(arScene: Scene) {
                counter++
            }
        })

        manager.setupScene(arScene)
        manager.setupScene(arScene)

        counter shouldEqual 2
        manager.getArScene() shouldBe arScene
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

        manager.setupScene(arScene)

        notified shouldBe false
    }

    @Test
    fun `should notify listener after ARCore has been loaded first time`() {
        var firstTimeFlag = false
        manager.addArLoadedListener(object : ArResourcesProvider.ArLoadedListener {
            override fun onArLoaded(firstTime: Boolean) {
                firstTimeFlag = firstTime
            }
        })

        manager.onArCoreLoaded()

        firstTimeFlag shouldBe true
        manager.isArLoaded() shouldBe true
    }

    @Test
    fun `should notify listener each time ARCore core is loaded`() {
        var notifyCount = 0
        var firstTimeFlag = true
        manager.addArLoadedListener(object : ArResourcesProvider.ArLoadedListener {
            override fun onArLoaded(firstTime: Boolean) {
                notifyCount++
                firstTimeFlag = firstTime
            }
        })

        manager.onArCoreLoaded()
        manager.onArCoreLoaded()

        notifyCount shouldEqual 2
        firstTimeFlag shouldBe false
        manager.isArLoaded() shouldBe true
    }

    @Test
    fun `should not notify removed listener after ARCore has been loaded`() {
        var notified = false
        val listener = object : ArResourcesProvider.ArLoadedListener {
            override fun onArLoaded(firstTime: Boolean) {
                notified = true
            }
        }
        manager.addArLoadedListener(listener)
        manager.removeArLoadedListener(listener)

        manager.onArCoreLoaded()

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

        manager.setupTransformationSystem(transformationSystem)

        notified shouldBe true
        manager.getTransformationSystem() shouldBe transformationSystem
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

        manager.setupTransformationSystem(transformationSystem)

        notified shouldBe false
    }

    @Test
    fun `should notify listener when camera pose updated`() {
        var poseFromListener: Pose? = null
        manager.addCameraUpdatedListener(object : ArResourcesProvider.CameraUpdatedListener {
            override fun onCameraUpdated(cameraPose: Pose, state: TrackingState) {
                poseFromListener = cameraPose
            }
        })
        val updatedPose = Utils.createPose(Vector3(4f, 2f, -1f), Quaternion.identity())

        manager.onCameraUpdated(updatedPose, TrackingState.TRACKING)

        manager.getCameraState() shouldEqual TrackingState.TRACKING
        poseFromListener shouldNotBe null
        poseFromListener!!.getTranslationVector() shouldEqualInexact Vector3(4f, 2f, -1f)
        poseFromListener!!.getRotation() shouldEqual Quaternion.identity()
    }

    @Test
    fun `should not notify removed listener when camera pose updated`() {
        var notified = false
        val listener = object : ArResourcesProvider.CameraUpdatedListener {
            override fun onCameraUpdated(cameraPose: Pose, state: TrackingState) {
                notified = true
            }
        }
        manager.addCameraUpdatedListener(listener)
        manager.removeCameraUpdatedListener(listener)
        val updatedPose = Utils.createPose(Vector3(4f, 2f, -1f), Quaternion.identity())

        manager.onCameraUpdated(updatedPose, TrackingState.TRACKING)

        notified shouldBe false
    }

    @Test
    fun `isPlaneDetectionEnabled should return true if plane detection enabled`() {
        manager.planeDetection = true

        manager.isPlaneDetectionEnabled() shouldBe true
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

        manager.onPlaneTapped(tapResult)

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

        manager.onPlaneTapped(hitResult)

        notified shouldBe false
    }

    @Test
    fun `should clear AR Core references`() {
        manager.setupScene(arScene)
        manager.setupTransformationSystem(transformationSystem)
        val cameraPose = Utils.createPose(Vector3(4f, 2f, -1f), Quaternion.identity())
        manager.onCameraUpdated(cameraPose, TrackingState.TRACKING)
        manager.onArCoreLoaded()

        manager.clearArReferences()

        manager.getArScene() shouldBe null
        manager.getTransformationSystem() shouldBe null
        manager.getCameraState() shouldBe null
        manager.isArLoaded() shouldBe false
    }

    private fun getTransformationSystem(): TransformationSystem {
        val displayMetrics = context.resources.displayMetrics
        return TransformationSystem(displayMetrics, FootprintSelectionVisualizer())
    }

}