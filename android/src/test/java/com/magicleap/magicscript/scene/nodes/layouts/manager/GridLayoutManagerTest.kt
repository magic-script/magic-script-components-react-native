package com.magicleap.magicscript.scene.nodes.layouts.manager

import com.magicleap.magicscript.NodeBuilder
import com.magicleap.magicscript.layoutUntilStableBounds
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
    private lateinit var childrenList: List<TransformNode>
    // <child index, bounding>
    private val childrenBounds = mutableMapOf<Int, Bounding>()

    @Before
    fun setUp() {
        manager = GridLayoutManagerImpl()
        manager.rows = 1
        manager.parentWidth = 1f
        manager.itemPadding = Padding(0f, 0f, 0f, 0f)

        val child1Bounds = Bounding(0f, 0f, 2f, 1f)
        val child2Bounds = Bounding(0f, 0f, 1f, 1f)
        val child1 = NodeBuilder().withContentBounds(child1Bounds).build()
        val child2 = NodeBuilder().withContentBounds(child2Bounds).build()
        childrenList = listOf(child1, child2)
    }

    @Test
    fun `should scale down children proportionally to their size when parent size is limited`() {
        manager.layoutUntilStableBounds(childrenList, childrenBounds)

        assertEquals(1 / 3f, childrenList[0].localScale.x, EPSILON)
        assertEquals(1 / 3f, childrenList[1].localScale.x, EPSILON)
    }

    @Test
    fun `should set back initial scale on children when parent width updated to unlimited`() {
        manager.layoutUntilStableBounds(childrenList, childrenBounds)

        manager.parentWidth = UiLayout.WRAP_CONTENT_DIMENSION
        manager.layoutUntilStableBounds(childrenList, childrenBounds)

        assertEquals(1f, childrenList[0].localScale.x, EPSILON)
        assertEquals(1f, childrenList[1].localScale.x, EPSILON)
    }

    @Test
    fun `should use apply previous scale when padding set back to 0`() {
        manager.layoutUntilStableBounds(childrenList, childrenBounds)
        manager.itemPadding = Padding(0f, 0f, 0f, 0f)
        manager.layoutUntilStableBounds(childrenList, childrenBounds)

        assertEquals(1 / 3f, childrenList[0].localScale.x, EPSILON)
        assertEquals(1 / 3f, childrenList[1].localScale.x, EPSILON)
    }

}