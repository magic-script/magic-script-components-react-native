package com.magicleap.magicscript.scene.nodes.layouts

import com.facebook.react.bridge.JavaOnlyMap
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.base.UiLayout
import com.magicleap.magicscript.scene.nodes.layouts.manager.RectLayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.manager.RectLayoutManagerImpl
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.shouldEqualInexact
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UIRectLayoutTest {

    private lateinit var rectLayoutManager: RectLayoutManager

    @Before
    fun setUp() {
        rectLayoutManager = spy(RectLayoutManagerImpl())
    }

    @Test
    fun `should set center-center alignment when no alignment is passed`() {
        val props = JavaOnlyMap()
        val node = createNode(props)
        node.build()

        verify(rectLayoutManager).contentHorizontalAlignment = Alignment.HorizontalAlignment.CENTER
        verify(rectLayoutManager).contentVerticalAlignment = Alignment.VerticalAlignment.CENTER
    }

    @Test
    fun `should set passed alignment`() {
        val props = reactMapOf(UiRectLayout.PROP_CONTENT_ALIGNMENT, "bottom-left")
        val node = createNode(props)
        node.build()

        verify(rectLayoutManager).contentHorizontalAlignment = Alignment.HorizontalAlignment.LEFT
        verify(rectLayoutManager).contentVerticalAlignment = Alignment.VerticalAlignment.BOTTOM
    }

    @Test
    fun `should return correct bounds`() {
        val props = reactMapOf(UiLayout.PROP_WIDTH, 2.0, UiLayout.PROP_HEIGHT, 1.0)
        val node = createNode(props)
        val expectedBounds = Bounding(-1F, -0.5F, 1F, 0.5F)
        node.build()

        val bounds = node.getBounding()

        bounds shouldEqualInexact expectedBounds
    }

    private fun createNode(props: JavaOnlyMap): UiRectLayout {
        return UiRectLayout(props, rectLayoutManager)
    }

}