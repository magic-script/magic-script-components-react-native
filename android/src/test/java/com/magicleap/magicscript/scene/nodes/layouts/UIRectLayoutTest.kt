package com.magicleap.magicscript.scene.nodes.layouts

import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.NodeBuilder
import com.magicleap.magicscript.scene.nodes.base.UiLayout
import com.magicleap.magicscript.scene.nodes.layouts.manager.RectLayoutManager
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UIRectLayoutTest {

    private lateinit var rectLayoutManager: RectLayoutManager

    @Before
    fun setUp() {
        rectLayoutManager = mock()
    }

    @Test
    fun `should set center-center alignment when no alignment is passed`() {
        val props = JavaOnlyMap()
        val node = UiRectLayout(props, rectLayoutManager)
        node.build()

        verify(rectLayoutManager).contentHorizontalAlignment = Alignment.HorizontalAlignment.CENTER
        verify(rectLayoutManager).contentVerticalAlignment = Alignment.VerticalAlignment.CENTER
    }

    @Test
    fun `should set passed alignment`() {
        val props = JavaOnlyMap.of(UiRectLayout.PROP_CONTENT_ALIGNMENT, "bottom-left")
        val node = UiRectLayout(props, rectLayoutManager)
        node.build()

        verify(rectLayoutManager).contentHorizontalAlignment = Alignment.HorizontalAlignment.LEFT
        verify(rectLayoutManager).contentVerticalAlignment = Alignment.VerticalAlignment.BOTTOM
    }

    @Test
    fun `should return correct bounds`() {
        val props = JavaOnlyMap.of(UiLayout.PROP_WIDTH, 2.0, UiLayout.PROP_HEIGHT, 1.0)
        val node = UiRectLayout(props, rectLayoutManager)
        val expectedBounds = Bounding(-1F, -0.5F, 1F, 0.5F)
        node.build() // invokes the layout loop

        val bounds = node.getBounding()

        assertTrue(Bounding.equalInexact(expectedBounds, bounds))
    }

    @Test
    fun `should rescale child if bigger than layout size`() {
        val props = JavaOnlyMap.of(UiLayout.PROP_WIDTH, 1.0, UiLayout.PROP_HEIGHT, 1.0)
        val node = UiRectLayout(props, rectLayoutManager)
        val childNode = NodeBuilder()
            .withContentBounds(Bounding(0f, 0f, 2f, 1f))
            .build()
        node.addContent(childNode)
        node.build() // invokes the layout loop

        assertEquals(Vector3(0.5f, 0.5f, 1f), childNode.localScale)
    }
}