package com.magicleap.magicscript.scene.nodes.layouts.manager

import com.magicleap.magicscript.NodeBuilder
import com.magicleap.magicscript.layoutUntilStableBounds
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout.Companion.WRAP_CONTENT_DIMENSION
import com.magicleap.magicscript.scene.nodes.layouts.params.GridLayoutParams
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Vector2
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

    // Layout params
    private var columns = 0
    private var rows = 1
    private var size = Vector2(1f, WRAP_CONTENT_DIMENSION)
    private var itemPadding = Padding(0f, 0f, 0f, 0f)
    private var itemHorizontalAlignment = Alignment.HorizontalAlignment.CENTER
    private var itemVerticalAlignment = Alignment.VerticalAlignment.CENTER

    @Before
    fun setUp() {
        manager = GridLayoutManager()

        val child1Bounds = Bounding(0f, 0f, 2f, 1f)
        val child2Bounds = Bounding(0f, 0f, 1f, 1f)
        val child1 = NodeBuilder().withContentBounds(child1Bounds).build()
        val child2 = NodeBuilder().withContentBounds(child2Bounds).build()
        childrenList = listOf(child1, child2)
    }

    @Test
    fun `should return correct layout bounds size when parent bigger than children bounds`() {
        size = Vector2(5f, 6f)
        itemPadding = Padding(0.1f, 0.1f, 0.1f, 0.1f)
        manager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        val boundsSize = manager.getLayoutBounds(getLayoutParams()).size()
        assertEquals(5f, boundsSize.x, EPSILON)
        assertEquals(6f, boundsSize.y, EPSILON)
    }

    @Test
    fun `should scale down children proportionally to their size when layout size is limited`() {
        size = Vector2(1f, WRAP_CONTENT_DIMENSION)

        manager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        assertEquals(1 / 3f, childrenList[0].localScale.x, EPSILON)
        assertEquals(1 / 3f, childrenList[1].localScale.x, EPSILON)
        assertEquals(1 / 3f, childrenList[0].localScale.y, EPSILON)
        assertEquals(1 / 3f, childrenList[1].localScale.y, EPSILON)
    }

    @Test
    fun `should correctly scale children when padding set`() {
        size = Vector2(2.5f, WRAP_CONTENT_DIMENSION)
        itemPadding = Padding(0f, 0.04f, 0f, 0.06f)

        manager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 50)

        // scale = (layout width - horizontal sum padding) / children sum width
        val expectedScale = 0.76666f
        assertEquals(expectedScale, childrenList[0].localScale.x, EPSILON)
        assertEquals(expectedScale, childrenList[1].localScale.x, EPSILON)
        assertEquals(expectedScale, childrenList[0].localScale.y, EPSILON)
        assertEquals(expectedScale, childrenList[1].localScale.y, EPSILON)
    }

    @Test
    fun `should set back initial scale on children when layout width updated to unlimited`() {
        size = Vector2(1f, WRAP_CONTENT_DIMENSION)
        manager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        size = Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)
        manager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        assertEquals(1f, childrenList[0].localScale.x, EPSILON)
        assertEquals(1f, childrenList[1].localScale.x, EPSILON)
        assertEquals(1f, childrenList[0].localScale.y, EPSILON)
        assertEquals(1f, childrenList[1].localScale.y, EPSILON)
    }

    @Test
    fun `should apply previous scale when padding set back to 0`() {
        size = Vector2(1f, WRAP_CONTENT_DIMENSION)
        itemPadding = Padding(0.2f, 0.14f, 0.2f, 0.16f)
        manager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 50)

        itemPadding = Padding(0f, 0f, 0f, 0f)
        manager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 50)

        assertEquals(1 / 3f, childrenList[0].localScale.x, EPSILON)
        assertEquals(1 / 3f, childrenList[1].localScale.x, EPSILON)
        assertEquals(1 / 3f, childrenList[0].localScale.y, EPSILON)
        assertEquals(1 / 3f, childrenList[1].localScale.y, EPSILON)
    }

    private fun getLayoutParams() =
        GridLayoutParams(
            columns = columns,
            rows = rows,
            size = size,
            itemsAlignment = mapOf(
                Pair(0, Alignment(itemVerticalAlignment, itemHorizontalAlignment)),
                Pair(1, Alignment(itemVerticalAlignment, itemHorizontalAlignment))
            ),
            itemsPadding = mapOf(
                Pair(0, itemPadding),
                Pair(1, itemPadding)
            )
        )

}