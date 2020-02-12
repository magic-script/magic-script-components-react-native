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

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import com.google.ar.sceneform.math.Matrix
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.*
import com.magicleap.magicscript.scene.nodes.audio.model.SpatialSoundDistance
import com.magicleap.magicscript.scene.nodes.audio.model.SpatialSoundPosition
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Padding
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.amshove.kluent.shouldNotBeNull
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PropertiesUtilsTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
    }

    @Test
    fun `should return Uri equivalent if provided URL string`() {
        val path = "http://sample.com/file.mp4"
        val bundle = Bundle()
        val prop = "PROP_NAME"
        bundle.putString(prop, path)
        val expected = Uri.parse(path)

        val uri = bundle.readFilePath(prop, context)

        assertEquals(expected, uri)
    }

    @Test
    fun `should return Uri equivalent for URL path inside bundle`() {
        val path = "http://localhost/sample-image.jpg"
        val pathBundle = Bundle()
        pathBundle.putString("uri", path)
        val propsBundle = Bundle()
        val prop = "imagePath"
        propsBundle.putBundle(prop, pathBundle)
        val expected = Uri.parse(path)

        val uri = propsBundle.readImagePath(prop, context)

        expected shouldEqual uri
    }

    @Test
    fun `should return android resource Uri for non URL path inside bundle`() {
        val path = "resources_video1.mp4"
        val pathBundle = Bundle()
        pathBundle.putString("uri", path)
        val propsBundle = Bundle()
        val prop = "videoPath"
        propsBundle.putBundle(prop, pathBundle)

        val uri = propsBundle.readFilePath(prop, context)

        assertTrue(uri.toString().startsWith("android.resource"))
    }

    @Test
    fun `should return null if there is no property to read vector from`() {
        val emptyBundle = Bundle()

        val vector = emptyBundle.read<Vector3>("PROP_NAME")

        assertNull(vector)
    }

    @Test
    fun `should return null if property is list and size is not equal to 3`() {
        val bundle = Bundle()
        val prop = "PROP_NAME"
        bundle.putSerializable(prop, arrayListOf(1.0, 2.0))

        val vector = bundle.read<Vector3>(prop)

        assertNull(vector)
    }

    @Test
    fun `should return vector if property is list and size is equal to 3`() {
        val bundle = Bundle()
        val prop = "PROP_NAME"
        bundle.putSerializable(prop, arrayListOf(1.0, 2.0, 3.0))

        val vector = bundle.read<Vector3>(prop)

        assertNotNull(vector)
        vector shouldEqual Vector3(1.0f, 2.0f, 3.0f)
    }

    @Test
    fun `should return vectors list for list of 3 element double lists`() {
        val bundle = Bundle()
        val prop = "PROP_NAME"
        val element1 = arrayListOf(1.5, 1.5, 1.5)
        val element2 = arrayListOf(2.5, 2.5, 2.5)
        val elementsList = arrayListOf(element1, element2)
        bundle.putSerializable(prop, elementsList)

        val result = bundle.readVectorsList(prop)

        result.size shouldEqual 2
        result[0] shouldEqual Vector3(1.5f, 1.5f, 1.5f)
        result[1] shouldEqual Vector3(2.5f, 2.5f, 2.5f)
    }

    @Test
    fun `should return empty list for list of 2 element double lists`() {
        val bundle = Bundle()
        val prop = "PROP_NAME"
        val element = arrayListOf(1.5, 1.5)
        val elementsList = arrayListOf(element)
        bundle.putSerializable(prop, elementsList)

        val result = bundle.readVectorsList(prop)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return matrix for 16 elements double list`() {
        val bundle = Bundle()
        val prop = "PROP_NAME"
        val list = arrayListOf(
            1.0, 2.0, 3.0, 4.0,
            5.0, 6.0, 7.0, 8.0,
            9.0, 10.0, 11.0, 12.0,
            13.0, 14.0, 15.0, 16.0
        )
        bundle.putSerializable(prop, list)
        val expectedValues = list.map { it.toFloat() }.toFloatArray()

        val result = bundle.read<Matrix>(prop)

        result shouldNotBe null
        assertTrue(result?.data?.contentEquals(expectedValues) ?: false)
    }

    @Test
    fun `should return null for non 16 elements double list`() {
        val bundle = Bundle()
        val prop = "PROP_NAME"
        val list = arrayListOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
        bundle.putSerializable(prop, list)

        val result = bundle.read<Matrix>(prop)

        assertNull(result)
    }

    @Test
    fun `should return color if property is list and size is equal to 4`() {
        val bundle = Bundle()
        val prop = "PROP_NAME"
        val list = arrayListOf(0.8, 0.2, 0.5, 0.75)
        bundle.putSerializable(prop, list)

        val color = bundle.readColor(prop)

        color shouldEqual -1077136513
    }

    @Test
    fun `should return padding if property is list and size is equal to 4`() {
        val bundle = Bundle()
        val prop = "PROP_NAME"
        val list = arrayListOf(1.0, 2.0, 3.0, 4.0)
        bundle.putSerializable(prop, list)

        val padding = bundle.read<Padding>(prop)

        assertNotNull(padding)
        padding?.top shouldEqual 1.0f
        padding?.right shouldEqual 2.0f
        padding?.bottom shouldEqual 3.0f
        padding?.left shouldEqual 4.0f
    }

    @Test
    fun `should read scroll bounds`() {
        val bundle = reactMapOf().scrollBounds(
            min = arrayOf(1.2, 1.2, -0.1), max = arrayOf(2.0, 2.0, 0.1)
        ).toBundle()

        val scrollBounds = bundle.read<AABB>("scrollBounds")

        scrollBounds shouldNotBe null
        scrollBounds?.min shouldEqual Vector3(1.2f, 1.2f, -0.1f)
        scrollBounds?.max shouldEqual Vector3(2.0f, 2.0f, 0.1f)
    }

    @Test
    fun `should read SpatialSoundPosition`() {
        val bundle = reactMapOf()
            .spatialSoundPosition(channel = 4.0, channelPosition = arrayOf(.1, 1.1, 2.1))
            .toBundle()

        val spatialSoundPosition = bundle.read<SpatialSoundPosition>("spatialSoundPosition")

        spatialSoundPosition.shouldNotBeNull()
        spatialSoundPosition.channel shouldEqual 4.0
        spatialSoundPosition.channelPosition.shouldNotBeNull()
        spatialSoundPosition.channelPosition!!.run {
            x shouldEqual .1f
            y shouldEqual 1.1f
            z shouldEqual 2.1f
        }
    }

    @Test
    fun `should read SpatialSoundDistance`() {
        val bundle = reactMapOf()
            .spatialSoundDistance(4.0, 1.0, 3.0, 2.0)
            .toBundle()

        val spatialSoundDistance = bundle.read<SpatialSoundDistance>("spatialSoundDistance")

        spatialSoundDistance.shouldNotBeNull()
        spatialSoundDistance.channel shouldEqual 4.0
        spatialSoundDistance.minDistance shouldEqual 1f
        spatialSoundDistance.maxDistance shouldEqual 3f
        spatialSoundDistance.rolloffFactor shouldEqual 2
    }

}