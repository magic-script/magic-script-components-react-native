package com.reactlibrary.utils

import android.os.Bundle
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Sphere
import com.google.ar.sceneform.math.Vector3
import com.google.ar.schemas.sceneform.CollisionShapeType
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.reactlibrary.scene.nodes.props.Padding
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PropertiesReaderTest {

    @Test
    fun `should return null if there is no property to read vector from`() {
        val bundle = mock<Bundle>()
        val props = "BUNDLE_ARRAY"
        whenever(bundle.getSerializable(props)).thenReturn(null)

        val vector = PropertiesReader.readVector3(bundle, props)

        Assert.assertNull(vector)
    }

    @Test
    fun `should return null if property is list and size is not equal to 3`() {
        val bundle = mock<Bundle>()
        val props = "BUNDLE_ARRAY"
        whenever(bundle.getSerializable(props)).thenReturn(arrayListOf(1.0, 2.0))

        val vector = PropertiesReader.readVector3(bundle, props)

        Assert.assertNull(vector)
    }

    @Test
    fun `should return vector if property is list and size is equal to 3`() {
        val bundle = mock<Bundle>()
        val props = "BUNDLE_ARRAY"
        whenever(bundle.getSerializable(props)).thenReturn(arrayListOf(1.0, 2.0, 3.0))

        val vector = PropertiesReader.readVector3(bundle, props)

        Assert.assertNotNull(vector)
        Assert.assertTrue(vector is Vector3)
    }

    @Test
    fun `should return color if property is list and size is equal to 4`() {
        val bundle = mock<Bundle>()
        val props = "BUNDLE_ARRAY"
        whenever(bundle.getSerializable(props)).thenReturn(arrayListOf(1.0, 2.0, 3.0, 4.0))

        val color = PropertiesReader.readColor(bundle, props)

        Assert.assertNotNull(color)
        Assert.assertTrue(color is Int)
    }

    @Test
    fun `should return padding if property is list and size is equal to 4`() {
        val bundle = mock<Bundle>()
        val props = "BUNDLE_ARRAY"
        whenever(bundle.getSerializable(props)).thenReturn(arrayListOf(1.0, 2.0, 3.0, 4.0))

        val padding = PropertiesReader.readPadding(bundle, props)

        Assert.assertNotNull(padding)
        Assert.assertTrue(padding is Padding)
    }
}