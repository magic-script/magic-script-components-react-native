package com.magicleap.magicscript.scene.nodes.layouts.manager

import com.magicleap.magicscript.NodeBuilder
import com.magicleap.magicscript.layoutUntilStableBounds
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RectLayoutManagerTest {
    private lateinit var rectLayoutManager: RectLayoutManager
    private val EPSILON = 1e-5f

    @Before
    fun setUp() {
        this.rectLayoutManager = RectLayoutManagerImpl()
    }

    @Test
    fun `should position child node`() {
        rectLayoutManager.parentWidth = 2f
        rectLayoutManager.parentHeight = 2f
        rectLayoutManager.itemPadding = Padding(0.5F, 0.5F, 0.5F, 0.5F)
        rectLayoutManager.contentHorizontalAlignment = Alignment.HorizontalAlignment.RIGHT
        rectLayoutManager.contentVerticalAlignment = Alignment.VerticalAlignment.TOP
        val children: List<TransformNode> = listOf(
            NodeBuilder()
                .withContentBounds(Bounding(-0.2F, -0.2F, 0.2F, 0.2F))
                .build()
        )
        val childrenBounds: MutableMap<Int, Bounding> = mutableMapOf()

        rectLayoutManager.layoutUntilStableBounds(children, childrenBounds, 10)

        val childPos = children[0].localPosition
        assertEquals(0.3f, childPos.x, EPSILON)
        assertEquals(0.3f, childPos.y, EPSILON)
    }

    @Test
    fun `should rescale child if bigger than parent size`() {
        rectLayoutManager.itemPadding = Padding(0f, 0f, 0f, 0f)
        rectLayoutManager.parentWidth = 1f
        rectLayoutManager.parentHeight = 1f
        val children: List<TransformNode> = listOf(
            NodeBuilder()
                .withContentBounds(Bounding(0f, 0f, 2f, 1f))
                .build()
        )
        val childrenBounds: MutableMap<Int, Bounding> = mutableMapOf()

        rectLayoutManager.layoutUntilStableBounds(children, childrenBounds, 10)

        assertEquals(0.5f, children[0].localScale.x, EPSILON)
        assertEquals(0.5f, children[0].localScale.y, EPSILON)
    }

}