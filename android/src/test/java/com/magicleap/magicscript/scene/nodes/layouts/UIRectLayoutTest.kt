package com.magicleap.magicscript.scene.nodes.layouts

import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.NodeBuilder
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.shouldEqualInexact
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UIRectLayoutTest {

    private lateinit var layoutManager: LayoutManager<LayoutParams>

    // local bounds of children inside the layout
    private val layoutBounds = AABB(min = Vector3(1f, -2f, 0f), max = Vector3(3f, 1f, 0f))

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

        val bounding = node.getBounding()

        bounding.min shouldEqualInexact Vector3(-1f, -1.5f, 0f)
        bounding.max shouldEqualInexact Vector3(1f, 1.5f, 0f)
    }

    @Test
    fun `should set top-left alignment when no alignment is passed`() {
        val props = JavaOnlyMap()
        val node = createNode(props)

        node.verticalAlignment shouldEqual Alignment.Vertical.TOP
        node.horizontalAlignment shouldEqual Alignment.Horizontal.LEFT
    }

    @Test
    fun `should set passed content alignment`() {
        val props = reactMapOf(UiRectLayout.PROP_CONTENT_ALIGNMENT, "bottom-left")
        val node = createNode(props)
        val child = NodeBuilder().build()
        node.addContent(child)

        val layoutParams = node.getLayoutParams()

        layoutParams.itemsAlignment[child] shouldNotBe null
        layoutParams.itemsAlignment[child]!!.vertical shouldEqual Alignment.Vertical.BOTTOM
        layoutParams.itemsAlignment[child]!!.horizontal shouldEqual Alignment.Horizontal.LEFT
    }

    private fun createNode(props: JavaOnlyMap): UiRectLayout {
        return UiRectLayout(props, layoutManager).apply {
            build()
        }
    }

}