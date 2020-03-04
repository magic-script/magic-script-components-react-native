package com.magicleap.magicscript.scene.nodes.layouts.manager

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.UiNodeBuilder
import com.magicleap.magicscript.layoutUntilStableBounds
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout.Companion.WRAP_CONTENT_DIMENSION
import com.magicleap.magicscript.scene.nodes.layouts.params.GridLayoutParams
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.shouldEqualInexact
import com.magicleap.magicscript.utils.Vector2
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GridLayoutManagerTest {

    private lateinit var manager: GridLayoutManager
    private lateinit var childrenList: List<TransformNode>
    private lateinit var itemsPadding: Map<TransformNode, Padding>
    private lateinit var itemsAlignment: Map<TransformNode, Alignment>

    private val childrenBounds = mutableMapOf<TransformNode, AABB>()

    // Layout params
    private var columns = 0 // dynamic
    private var rows = 1
    private var size = Vector2(1f, WRAP_CONTENT_DIMENSION)
    private lateinit var context: Context

    @Before
    fun setUp() {
        manager = GridLayoutManager()
        context = ApplicationProvider.getApplicationContext()

        childrenList = listOf(
            buildChild(width = 2f, height = 1f, alignment = "bottom-left"),
            buildChild(width = 1f, height = 1f, alignment = "bottom-left")
        )

        itemsPadding = mapOf(
            childrenList[0] to Padding(),
            childrenList[1] to Padding()
        )

        itemsAlignment = mapOf(
            childrenList[0] to Alignment(Alignment.Vertical.CENTER, Alignment.Horizontal.CENTER),
            childrenList[1] to Alignment(Alignment.Vertical.CENTER, Alignment.Horizontal.CENTER)
        )
    }

    @Test
    fun `should return correct layout bounds size when parent bigger than children bounds`() {
        size = Vector2(5f, 6f)
        itemsPadding = mapOf(
            childrenList[0] to Padding(0.1f, 0.1f, 0.1f, 0.1f),
            childrenList[1] to Padding(0.1f, 0.1f, 0.1f, 0.1f)
        )
        manager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        val boundsSize = manager.getLayoutBounds(getLayoutParams()).size()

        boundsSize shouldEqualInexact Vector3(5f, 6f, 0f)
    }

    @Test
    fun `should scale down children proportionally to their size when layout size is limited`() {
        size = Vector2(1f, WRAP_CONTENT_DIMENSION)

        manager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        childrenList[0].localScale shouldEqualInexact Vector3(1 / 3f, 1 / 3f, 1f)
        childrenList[1].localScale shouldEqualInexact Vector3(1 / 3f, 1 / 3f, 1f)
    }

    @Test
    fun `should correctly scale children when padding set`() {
        size = Vector2(2.5f, WRAP_CONTENT_DIMENSION)
        itemsPadding = mapOf(
            childrenList[0] to Padding(0f, 0.04f, 0f, 0.06f),
            childrenList[1] to Padding(0f, 0.04f, 0f, 0.06f)
        )
        // scale = (layout width - horizontal sum padding) / children sum width
        val expectedXYScale = 0.76666f

        manager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 50)

        childrenList[0].localScale shouldEqualInexact Vector3(expectedXYScale, expectedXYScale, 1f)
        childrenList[1].localScale shouldEqualInexact Vector3(expectedXYScale, expectedXYScale, 1f)
    }

    @Test
    fun `should set back initial scale on children when layout width updated to unlimited`() {
        size = Vector2(1f, WRAP_CONTENT_DIMENSION)
        manager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        size = Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)
        manager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        childrenList[0].localScale shouldEqualInexact Vector3(1f, 1f, 1f)
        childrenList[1].localScale shouldEqualInexact Vector3(1f, 1f, 1f)
    }

    @Test
    fun `should apply previous scale when padding set back to 0`() {
        size = Vector2(1f, WRAP_CONTENT_DIMENSION)
        itemsPadding = mapOf(
            childrenList[0] to Padding(0.2f, 0.14f, 0.2f, 0.16f),
            childrenList[1] to Padding(0.2f, 0.14f, 0.2f, 0.16f)
        )
        manager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 50)

        itemsPadding = mapOf(
            childrenList[0] to Padding(),
            childrenList[1] to Padding()
        )
        manager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 50)

        childrenList[0].localScale shouldEqualInexact Vector3(1 / 3f, 1 / 3f, 1f)
        childrenList[1].localScale shouldEqualInexact Vector3(1 / 3f, 1 / 3f, 1f)
    }

    // positioning starts at top-left origin (0, 0) towards bottom-right
    @Test
    fun `should correctly position children with different padding`() {
        size = Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)
        columns = 2
        rows = 0 // dynamic

        childrenList = listOf(
            buildChild(width = 2f, height = 1f, alignment = "top-left"),
            buildChild(width = 2f, height = 1f, alignment = "top-left"),
            buildChild(width = 2f, height = 1f, alignment = "top-left"),
            buildChild(width = 2f, height = 1f, alignment = "top-left")
        )
        itemsPadding = mapOf(
            childrenList[0] to Padding(0.5f, 0f, 0f, 0.5f),
            childrenList[1] to Padding(0.5f, 0.2f, 0f, 1.5f),
            childrenList[2] to Padding(0f, 0.4f, 0.4f, 0f),
            childrenList[3] to Padding(0f, 3f, 0f, 0f)
        )
        itemsAlignment = mapOf(
            childrenList[0] to Alignment(Alignment.Vertical.TOP, Alignment.Horizontal.LEFT),
            childrenList[1] to Alignment(Alignment.Vertical.TOP, Alignment.Horizontal.LEFT)
        )

        manager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        childrenList[0].localPosition shouldEqualInexact Vector3(0.5f, -0.5f, 0f)
        childrenList[1].localPosition shouldEqualInexact Vector3(4f, -0.5f, 0f)
        childrenList[2].localPosition shouldEqualInexact Vector3(0f, -1.5f, 0f)
        childrenList[3].localPosition shouldEqualInexact Vector3(2.5f, -1.5f, 0f)
        val layoutBounds = manager.getLayoutBounds(getLayoutParams())
        layoutBounds shouldEqualInexact AABB(Vector3(0f, -2.9f, 0f), Vector3(7.5f, 0f, 0f))
    }

    private fun getLayoutParams() =
        GridLayoutParams(
            columns = columns,
            rows = rows,
            size = size,
            itemsAlignment = itemsAlignment,
            itemsPadding = itemsPadding
        )

    private fun buildChild(width: Float, height: Float, alignment: String): TransformNode {
        return UiNodeBuilder(context)
            .withSize(width, height)
            .withAlignment(alignment)
            .build()
    }

}