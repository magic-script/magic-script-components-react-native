package com.magicleap.magicscript.scene.nodes.layouts.manager

import com.magicleap.magicscript.NodeBuilder
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

    @Before
    fun setUp() {
        this.manager = GridLayoutManagerImpl()
    }

    @Test
    fun `should scale down children proportionally to their size when parent size is limited`() {
        manager.rows = 1
        manager.parentWidth = 1f
        manager.itemPadding = Padding(0f, 0f, 0f, 0f)
        val child1Bounds = Bounding(0f, 0f, 2f, 1f)
        val child2Bounds = Bounding(0f, 0f, 1f, 1f)
        val child1 = NodeBuilder().withContentBounds(child1Bounds).build()
        val child2 = NodeBuilder().withContentBounds(child2Bounds).build()
        val childrenBoundsMap = mapOf(0 to child1Bounds, 1 to child2Bounds)

        manager.layoutChildren(listOf(child1, child2), childrenBoundsMap)

        assertEquals(1 / 3f, child1.localScale.x, EPSILON)
        assertEquals(1 / 3f, child2.localScale.x, EPSILON)
    }

}