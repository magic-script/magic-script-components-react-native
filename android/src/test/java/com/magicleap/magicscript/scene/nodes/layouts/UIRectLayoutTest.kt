package com.magicleap.magicscript.scene.nodes.layouts

import com.facebook.react.bridge.JavaOnlyMap
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.base.LayoutParams
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout
import com.magicleap.magicscript.scene.nodes.layouts.manager.VerticalLinearLayoutManager
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.shouldEqualInexact
import com.nhaarman.mockitokotlin2.spy
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UIRectLayoutTest {

    private lateinit var layoutManager: VerticalLinearLayoutManager<LayoutParams>

    @Before
    fun setUp() {
        layoutManager = spy(VerticalLinearLayoutManager())
    }

    @Test
    fun `should set top-left alignment when no alignment is passed`() {
        val props = JavaOnlyMap()
        val node = createNode(props)
        node.build()

        node.verticalAlignment shouldEqual Alignment.VerticalAlignment.TOP
        node.horizontalAlignment shouldEqual Alignment.HorizontalAlignment.LEFT
    }

    @Test
    fun `should set passed content alignment`() {
        val props = reactMapOf(UiRectLayout.PROP_CONTENT_ALIGNMENT, "bottom-left")
        val node = createNode(props)
        node.build()

        val layoutParams = node.getLayoutParams()

        layoutParams.itemVerticalAlignment shouldEqual Alignment.VerticalAlignment.BOTTOM
        layoutParams.itemHorizontalAlignment shouldEqual Alignment.HorizontalAlignment.LEFT
    }

    @Test
    fun `should return correct bounds`() {
        val props = reactMapOf(
            UiBaseLayout.PROP_WIDTH, 2.0,
            UiBaseLayout.PROP_HEIGHT, 1.0,
            TransformNode.PROP_ALIGNMENT, "center-center"
        )
        val node = createNode(props)
        val expectedBounds = Bounding(-1F, -0.5F, 1F, 0.5F)
        node.build()

        val bounds = node.getBounding()

        bounds shouldEqualInexact expectedBounds
    }

    private fun createNode(props: JavaOnlyMap): UiRectLayout {
        return UiRectLayout(props, layoutManager)
    }

}