package com.magicleap.magicscript.scene.nodes.layouts.manager

import com.magicleap.magicscript.NodeBuilder
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiLayout
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GridLayoutManagerTest {

    private val EPSILON = 1e-5f
    private lateinit var manager: GridLayoutManager
    private lateinit var child1: TransformNode
    private lateinit var child2: TransformNode

    @Before
    fun setUp() {
        manager = GridLayoutManagerImpl()
        manager.rows = 1
        manager.parentWidth = 1f
        manager.itemPadding = Padding(0f, 0f, 0f, 0f)

        val child1Bounds = Bounding(0f, 0f, 2f, 1f)
        val child2Bounds = Bounding(0f, 0f, 1f, 1f)
        child1 = NodeBuilder().withContentBounds(child1Bounds).build()
        child2 = NodeBuilder().withContentBounds(child2Bounds).build()
    }

    @Test
    fun `should scale down children proportionally to their size when parent size is limited`() {
        manager.layoutChildren(listOf(child1, child2), getChildrenBounds())

        assertEquals(1 / 3f, child1.localScale.x, EPSILON)
        assertEquals(1 / 3f, child2.localScale.x, EPSILON)
    }

    @Test
    fun `should set back initial scale on children when parent width updated to unlimited`() {
        manager.layoutChildren(listOf(child1, child2), getChildrenBounds())
        manager.parentWidth = UiLayout.WRAP_CONTENT_DIMENSION

        manager.layoutChildren(listOf(child1, child2), getChildrenBounds())

        assertEquals(1f, child1.localScale.x, EPSILON)
        assertEquals(1f, child2.localScale.x, EPSILON)
    }


    private fun getChildrenBounds(): Map<Int, Bounding> {
        return mapOf(
            0 to child1.getBounding(),
            1 to child2.getBounding()
        )
    }
}