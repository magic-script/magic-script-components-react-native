package com.reactlibrary.utils

import android.os.Bundle
import com.google.ar.sceneform.math.Vector3
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.reactlibrary.scene.nodes.props.Padding
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PropertiesReaderTest {

    @Test
    fun `should return null if there is no property to read vector from`() {
        val bundle = mock<Bundle>()
        val prop = "BUNDLE_ARRAY"
        whenever(bundle.getSerializable(prop)).thenReturn(null)

        val vector = PropertiesReader.readVector3(bundle, prop)

        assertNull(vector)
    }

    @Test
    fun `should return null if property is list and size is not equal to 3`() {
        val bundle = mock<Bundle>()
        val prop = "BUNDLE_ARRAY"
        whenever(bundle.getSerializable(prop)).thenReturn(arrayListOf(1.0, 2.0))

        val vector = PropertiesReader.readVector3(bundle, prop)

        assertNull(vector)
    }

    @Test
    fun `should return vector if property is list and size is equal to 3`() {
        val bundle = mock<Bundle>()
        val prop = "BUNDLE_ARRAY"
        whenever(bundle.getSerializable(prop)).thenReturn(arrayListOf(1.0, 2.0, 3.0))

        val vector = PropertiesReader.readVector3(bundle, prop)

        assertNotNull(vector)
        assertTrue(vector is Vector3)
    }

    @Test
    fun `should return color if property is list and size is equal to 4`() {
        val bundle = mock<Bundle>()
        val prop = "BUNDLE_ARRAY"
        whenever(bundle.getSerializable(prop)).thenReturn(arrayListOf(1.0, 2.0, 3.0, 4.0))

        val color = PropertiesReader.readColor(bundle, prop)

        assertNotNull(color)
        assertTrue(color is Int)
    }

    @Test
    fun `should return padding if property is list and size is equal to 4`() {
        val bundle = mock<Bundle>()
        val prop = "BUNDLE_ARRAY"
        whenever(bundle.getSerializable(prop)).thenReturn(arrayListOf(1.0, 2.0, 3.0, 4.0))

        val padding = PropertiesReader.readPadding(bundle, prop)

        assertNotNull(padding)
        assertTrue(padding is Padding)
    }
}