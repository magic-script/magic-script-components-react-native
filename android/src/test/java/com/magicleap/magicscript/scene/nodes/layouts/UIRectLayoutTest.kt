package com.magicleap.magicscript.scene.nodes.layouts

import com.facebook.react.bridge.JavaOnlyMap
import com.magicleap.magicscript.NodeBuilder
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.layouts.manager.LayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.shouldEqualInexact
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UIRectLayoutTest {

    private lateinit var layoutManager: LayoutManager<LayoutParams>

    // local bounds of children inside the layout
    private val layoutBounds = Bounding(1f, -2f, 3f, 1f)

    @Before
    fun setUp() {
        layoutManager = mock()
        whenever(layoutManager.getLayoutBounds(any())).thenReturn(layoutBounds)
    }

    @Test
    fun `should return layout bounds based on bounds returned by layout manager`() {
        val props = reactMapOf(
            TransformNode.PROP_ALIGNMENT, "center-center"
        )
        val node = createNode(props)
        val expectedBounds = Bounding(left = -1f, bottom = -1.5f, right = 1f, top = 1.5f)

        val bounds = node.getBounding()

        bounds shouldEqualInexact expectedBounds
    }

    @Test
    fun `should set top-left alignment when no alignment is passed`() {
        val props = JavaOnlyMap()
        val node = createNode(props)

        node.verticalAlignment shouldEqual Alignment.VerticalAlignment.TOP
        node.horizontalAlignment shouldEqual Alignment.HorizontalAlignment.LEFT
    }

    @Test
    fun `should set passed content alignment`() {
        val props = reactMapOf(UiRectLayout.PROP_CONTENT_ALIGNMENT, "bottom-left")
        val node = createNode(props)

        val layoutParams = node.getLayoutParams()

        layoutParams.itemsAlignment[0]!!.vertical shouldEqual Alignment.VerticalAlignment.BOTTOM
        layoutParams.itemsAlignment[0]!!.horizontal shouldEqual Alignment.HorizontalAlignment.LEFT
    }


    private fun createNode(props: JavaOnlyMap): UiRectLayout {
        return UiRectLayout(props, layoutManager).apply {
            build()
            addContent(NodeBuilder().build())
        }
    }

}