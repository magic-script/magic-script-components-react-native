package com.magicleap.magicscript.scene.nodes.layouts

import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.NodeBuilder
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.shouldEqualInexact
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldHaveSize
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LayoutUtilsTest {

    @Test
    fun `should create padding map with default values if padding per item not specified`() {
        val children = createNodesList(3)

        val paddingMap = LayoutUtils.createChildrenPaddingMap(
            children = children,
            defaultPadding = Padding(),
            childPaddings = null
        )

        paddingMap.shouldHaveSize(children.size)
        for (element in children) {
            paddingMap[element] shouldEqual Padding()
        }
    }

    @Test
    fun `should use default padding only if child does not have custom padding`() {
        val children = createNodesList(5)
        val defaultPadding = Padding(1f, 1f, 1f, 1f)
        val customPadding2 = Padding(2f, 2f, 2f, 2f)
        val customPadding4 = Padding(4f, 4f, 4f, 4f)
        val childCustomPaddings = mapOf(
            2 to customPadding2,
            4 to customPadding4
        )

        val childrenPaddingMap =
            LayoutUtils.createChildrenPaddingMap(children, defaultPadding, childCustomPaddings)

        childrenPaddingMap[children[0]] shouldEqual defaultPadding
        childrenPaddingMap[children[1]] shouldEqual defaultPadding
        childrenPaddingMap[children[2]] shouldEqual customPadding2
        childrenPaddingMap[children[3]] shouldEqual defaultPadding
        childrenPaddingMap[children[4]] shouldEqual customPadding4
    }

    @Test
    fun `should create alignment map with default values if alignment per item not specified`() {
        val children = createNodesList(3)

        val childrenAlignmentMap =
            LayoutUtils.createChildrenAlignmentMap(
                children = children,
                defaultAlignment = Alignment(),
                childAlignments = null
            )

        childrenAlignmentMap.shouldHaveSize(children.size)
        for (child in children) {
            childrenAlignmentMap[child] shouldEqual Alignment()
        }
    }

    @Test
    fun `should use default alignment only if alignment per item not specified`() {
        val children = createNodesList(5)
        val defaultAlignment =
            Alignment(Alignment.Vertical.TOP, Alignment.Horizontal.LEFT)
        val customAlignment2 =
            Alignment(Alignment.Vertical.CENTER, Alignment.Horizontal.CENTER)
        val customAlignment4 =
            Alignment(Alignment.Vertical.BOTTOM, Alignment.Horizontal.RIGHT)
        val childCustomAlignments = mapOf(
            2 to customAlignment2,
            4 to customAlignment4
        )

        val childrenAlignmentMap = LayoutUtils.createChildrenAlignmentMap(
            children = children,
            defaultAlignment = defaultAlignment,
            childAlignments = childCustomAlignments
        )

        childrenAlignmentMap[children[0]] shouldEqual defaultAlignment
        childrenAlignmentMap[children[1]] shouldEqual defaultAlignment
        childrenAlignmentMap[children[2]] shouldEqual customAlignment2
        childrenAlignmentMap[children[3]] shouldEqual defaultAlignment
        childrenAlignmentMap[children[4]] shouldEqual customAlignment4
    }

    @Test
    fun `should use default padding values for grid items when padding per item not specified`() {
        val children = createNodesList(10)
        val columns = 2
        val rows = UiGridLayout.DYNAMIC_VALUE

        val childrenPaddingMap = LayoutUtils.createChildrenPaddingMap(
            columns = columns,
            rows = rows,
            children = children,
            defaultPadding = Padding(),
            childPaddings = null
        )

        childrenPaddingMap.shouldHaveSize(children.size)
        for (child in children) {
            childrenPaddingMap[child] shouldEqual Padding()
        }
    }

    @Test
    fun `should use default value only if custom padding not specified for grid child`() {
        val children = createNodesList(5)
        val columns = 2
        val rows = UiGridLayout.DYNAMIC_VALUE
        val defaultPadding = Padding(1f, 1f, 1f, 1f)
        val customPadding0_1 = Padding(2f, 2f, 2f, 2f)
        val customPadding1_1 = Padding(4f, 4f, 4f, 4f)
        val childCustomPaddings = mapOf(
            Pair(0, 1) to customPadding0_1,
            Pair(1, 1) to customPadding1_1
        )

        val childrenPaddingMap = LayoutUtils.createChildrenPaddingMap(
            columns = columns,
            rows = rows,
            children = children,
            defaultPadding = defaultPadding,
            childPaddings = childCustomPaddings
        )

        childrenPaddingMap[children[0]] shouldEqual defaultPadding
        childrenPaddingMap[children[1]] shouldEqual defaultPadding
        childrenPaddingMap[children[2]] shouldEqual customPadding0_1
        childrenPaddingMap[children[3]] shouldEqual customPadding1_1
        childrenPaddingMap[children[4]] shouldEqual defaultPadding
    }

    @Test
    fun `should create grid alignment map with default values if child alignments not specified`() {
        val children = createNodesList(3)
        val columns = 2
        val rows = UiGridLayout.DYNAMIC_VALUE

        val childrenAlignmentMap =
            LayoutUtils.createChildrenAlignmentMap(
                columns = columns,
                rows = rows,
                children = children,
                defaultAlignment = Alignment(),
                childAlignments = null
            )

        childrenAlignmentMap.shouldHaveSize(children.size)
        for (child in children) {
            childrenAlignmentMap[child] shouldEqual Alignment()
        }
    }

    @Test
    fun `should use default value only if grid child does not have custom alignment`() {
        val children = createNodesList(6)
        val columns = 2
        val rows = UiGridLayout.DYNAMIC_VALUE
        val defaultAlignment =
            Alignment(Alignment.Vertical.TOP, Alignment.Horizontal.LEFT)
        val customAlignment1_1 =
            Alignment(Alignment.Vertical.CENTER, Alignment.Horizontal.CENTER)
        val customAlignment1_2 =
            Alignment(Alignment.Vertical.BOTTOM, Alignment.Horizontal.RIGHT)
        val childCustomAlignments = mapOf(
            Pair(1, 1) to customAlignment1_1,
            Pair(1, 2) to customAlignment1_2
        )

        val childrenAlignmentMap = LayoutUtils.createChildrenAlignmentMap(
            columns = columns,
            rows = rows,
            children = children,
            defaultAlignment = defaultAlignment,
            childAlignments = childCustomAlignments
        )

        childrenAlignmentMap[children[0]] shouldEqual defaultAlignment
        childrenAlignmentMap[children[1]] shouldEqual defaultAlignment
        childrenAlignmentMap[children[2]] shouldEqual defaultAlignment
        childrenAlignmentMap[children[3]] shouldEqual customAlignment1_1
        childrenAlignmentMap[children[4]] shouldEqual defaultAlignment
        childrenAlignmentMap[children[5]] shouldEqual customAlignment1_2
    }

    @Test
    fun `getColumnIndex test`() {
        /***
        _______Col 0 | Col 1
        Row 0 |  0   |  1
        Row 1 |  2   |  3
        Row 2 |  4   |  5
         ***/
        val rows = UiGridLayout.DYNAMIC_VALUE
        val columns = 2

        LayoutUtils.getColumnIndex(0, columns, rows) shouldEqual 0
        LayoutUtils.getColumnIndex(1, columns, rows) shouldEqual 1
        LayoutUtils.getColumnIndex(2, columns, rows) shouldEqual 0
        LayoutUtils.getColumnIndex(3, columns, rows) shouldEqual 1
        LayoutUtils.getColumnIndex(4, columns, rows) shouldEqual 0
        LayoutUtils.getColumnIndex(5, columns, rows) shouldEqual 1
    }

    @Test
    fun `getRowIndex test`() {
        /***
        _______Col 0 | Col 1
        Row 0 |  0   |  1
        Row 1 |  2   |  3
        Row 2 |  4   |  5
         ***/
        val rows = UiGridLayout.DYNAMIC_VALUE
        val columns = 2

        LayoutUtils.getRowIndex(0, columns, rows) shouldEqual 0
        LayoutUtils.getRowIndex(1, columns, rows) shouldEqual 0
        LayoutUtils.getRowIndex(2, columns, rows) shouldEqual 1
        LayoutUtils.getRowIndex(3, columns, rows) shouldEqual 1
        LayoutUtils.getRowIndex(4, columns, rows) shouldEqual 2
        LayoutUtils.getRowIndex(5, columns, rows) shouldEqual 2
    }


    @Test
    fun `should return sum of all vertical paddings from the map`() {
        val childrenList = createNodesList(2)
        val paddingMap = mapOf(
            childrenList[0] to Padding(1f, 0.5f, 0.4f, 2f),
            childrenList[1] to Padding(4f, 0.5f, 0.6f, 2f)
        )

        val paddingSum = LayoutUtils.getVerticalPaddingSumOf(childrenList, paddingMap)

        paddingSum shouldEqualInexact 6f
    }

    @Test
    fun `should return sum of last 2 vertical paddings from the map`() {
        val childrenList = createNodesList(3)
        val paddingMap = mapOf(
            childrenList[0] to Padding(1f, 2f, 0.4f, 1f),
            childrenList[1] to Padding(1f, 1f, 1f, 4f),
            childrenList[2] to Padding(0.2f, 5f, 0.2f, 5f)
        )

        val paddingSum = LayoutUtils.getVerticalPaddingSumOf(
            childrenList.subList(1, 3),
            paddingMap
        )

        paddingSum shouldEqualInexact 2.4f
    }

    @Test
    fun `should return sum of all horizontal paddings from the map`() {
        val childrenList = createNodesList(2)
        val paddingMap = mapOf(
            childrenList[0] to Padding(1f, 2f, 0.4f, 3f),
            childrenList[1] to Padding(4f, 0.5f, 0.6f, 2f)
        )

        val paddingSum = LayoutUtils.getHorizontalPaddingSumOf(childrenList, paddingMap)

        paddingSum shouldEqualInexact 7.5f
    }

    @Test
    fun `should return sum of first 2 horizontal paddings from the map`() {
        val childrenList = createNodesList(3)
        val paddingMap = mapOf(
            childrenList[0] to Padding(1f, 2f, 0.4f, 1f),
            childrenList[1] to Padding(4f, 1f, 0.6f, 4f),
            childrenList[2] to Padding(0f, 5f, 0.6f, 5f)
        )

        val paddingSum = LayoutUtils.getHorizontalPaddingSumOf(
            childrenList.take(2),
            paddingMap
        )

        paddingSum shouldEqualInexact 8f
    }

    @Test
    fun `should return sum of all horizontal bounds sizes from the map`() {
        val childrenList = createNodesList(2)
        val boundsMap = mapOf(
            childrenList[0] to AABB(min = Vector3(-1.5f, -0.5f, 0f), max = Vector3(1f, 1f, 0f)),
            childrenList[1] to AABB(min = Vector3(0.1f, -0.5f, 0f), max = Vector3(0.5f, 1f, 0f))
        )

        val sum = LayoutUtils.getHorizontalBoundsSumOf(childrenList, boundsMap)

        sum shouldEqualInexact 2.9f
    }

    @Test
    fun `should return sum of all vertical bounds sizes from the map`() {
        val childrenList = createNodesList(2)
        val boundsMap = mapOf(
            childrenList[0] to AABB(min = Vector3(-1.5f, -2f, 0f), max = Vector3(1f, 1f, 0f)),
            childrenList[1] to AABB(min = Vector3(0.1f, -0.5f, 0f), max = Vector3(0.5f, 0f, 0f))
        )

        val sum = LayoutUtils.getVerticalBoundsSumOf(childrenList, boundsMap)

        sum shouldEqualInexact 3.5f
    }

    @Test
    fun `should return sum of first and last horizontal bounds sizes from the map`() {
        val childrenList = createNodesList(3)
        val boundsMap = mapOf(
            childrenList[0] to AABB(min = Vector3(0f, -0.5f, 0f), max = Vector3(3f, 2f, 0f)),
            childrenList[1] to AABB(min = Vector3(0.1f, 0f, 0f), max = Vector3(0.5f, 1f, 0f)),
            childrenList[2] to AABB(min = Vector3(-0.3f, -1f, 0f), max = Vector3(0.4f, 0f, 0f))
        )

        val sum = LayoutUtils.getHorizontalBoundsSumOf(
            children = listOf(childrenList.first(), childrenList.last()),
            bounds = boundsMap
        )

        sum shouldEqualInexact 3.7f
    }

    @Test
    fun `should return sum of last 2 vertical bounds sizes from the map`() {
        val childrenList = createNodesList(3)
        val boundsMap = mapOf(
            childrenList[0] to AABB(min = Vector3(-1.5f, -0.5f, 0f), max = Vector3(1f, 2f, 0f)),
            childrenList[1] to AABB(min = Vector3(0.1f, 0f, 0f), max = Vector3(0.5f, 1f, 0f)),
            childrenList[2] to AABB(min = Vector3(-1f, -1f, 0f), max = Vector3(1f, 0f, 0f))
        )

        val sum = LayoutUtils.getVerticalBoundsSumOf(
            children = childrenList.subList(1, 3),
            bounds = boundsMap
        )

        sum shouldEqualInexact 2f
    }

    private fun createNodesList(count: Int): List<TransformNode> {
        val nodes = mutableListOf<TransformNode>()
        for (i in 0 until count) {
            nodes.add(NodeBuilder().build())
        }
        return nodes
    }

}