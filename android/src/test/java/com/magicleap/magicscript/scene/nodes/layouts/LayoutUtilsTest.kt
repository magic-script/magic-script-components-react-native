package com.magicleap.magicscript.scene.nodes.layouts

import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Padding
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldHaveSize
import org.junit.Test

class LayoutUtilsTest {

    @Test
    fun `should return padding map with size and indices of children count`() {
        val childCount = 10

        val childrenPaddingMap = LayoutUtils.createChildrenPaddingMap(childCount, Padding(), null)

        childrenPaddingMap.shouldHaveSize(childCount)
        for (i in 0 until childCount) {
            childrenPaddingMap[i] shouldEqual Padding()
        }
    }

    @Test
    fun `should add default value only if child do not have custom padding`() {
        val childCount = 5
        val defaultPadding = Padding(1f, 1f, 1f, 1f)
        val customPadding2 = Padding(2f, 2f, 2f, 2f)
        val customPadding4 = Padding(4f, 4f, 4f, 4f)
        val childCustomPaddings = mapOf(
            2 to customPadding2,
            4 to customPadding4
        )

        val childrenPaddingMap =
            LayoutUtils.createChildrenPaddingMap(childCount, defaultPadding, childCustomPaddings)

        childrenPaddingMap[0] shouldEqual defaultPadding
        childrenPaddingMap[1] shouldEqual defaultPadding
        childrenPaddingMap[2] shouldEqual customPadding2
        childrenPaddingMap[3] shouldEqual defaultPadding
        childrenPaddingMap[4] shouldEqual customPadding4
    }

    @Test
    fun `should return alignment map with size and indices of children count`() {
        val childCount = 10

        val childrenAlignmentMap =
            LayoutUtils.createChildrenAlignmentMap(childCount, Alignment(), null)

        childrenAlignmentMap.shouldHaveSize(childCount)
        for (i in 0 until childCount) {
            childrenAlignmentMap[i] shouldEqual Alignment()
        }
    }

    @Test
    fun `should add default value only if child do not have custom alignment`() {
        val childCount = 5
        val defaultAlignment =
            Alignment(Alignment.VerticalAlignment.TOP, Alignment.HorizontalAlignment.LEFT)
        val customAlignment2 =
            Alignment(Alignment.VerticalAlignment.CENTER, Alignment.HorizontalAlignment.CENTER)
        val customAlignment4 =
            Alignment(Alignment.VerticalAlignment.BOTTOM, Alignment.HorizontalAlignment.RIGHT)
        val childCustomAlignments = mapOf(
            2 to customAlignment2,
            4 to customAlignment4
        )

        val childrenAlignmentMap = LayoutUtils.createChildrenAlignmentMap(
            childCount,
            defaultAlignment,
            childCustomAlignments
        )

        childrenAlignmentMap[0] shouldEqual defaultAlignment
        childrenAlignmentMap[1] shouldEqual defaultAlignment
        childrenAlignmentMap[2] shouldEqual customAlignment2
        childrenAlignmentMap[3] shouldEqual defaultAlignment
        childrenAlignmentMap[4] shouldEqual customAlignment4
    }

    @Test
    fun `should return padding map with size and indices of children count for grid child`() {
        val childCount = 10
        val columns = 2
        val rows = UiGridLayout.DYNAMIC_VALUE

        val childrenPaddingMap = LayoutUtils.createChildrenPaddingMap(
            columns = columns,
            rows = rows,
            childCount = childCount,
            defaultPadding = Padding(),
            childPaddings = null
        )

        childrenPaddingMap.shouldHaveSize(childCount)
        for (i in 0 until childCount) {
            childrenPaddingMap[i] shouldEqual Padding()
        }
    }

    @Test
    fun `should add default value only if child do not have custom padding for grid child`() {
        val childCount = 5
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
            childCount = childCount,
            defaultPadding = defaultPadding,
            childPaddings = childCustomPaddings
        )

        childrenPaddingMap[0] shouldEqual defaultPadding
        childrenPaddingMap[1] shouldEqual defaultPadding
        childrenPaddingMap[2] shouldEqual customPadding0_1
        childrenPaddingMap[3] shouldEqual customPadding1_1
        childrenPaddingMap[4] shouldEqual defaultPadding
    }

    @Test
    fun `should return alignment map with size and indices of children count for grid child`() {
        val childCount = 10
        val columns = 2
        val rows = UiGridLayout.DYNAMIC_VALUE

        val childrenAlignmentMap =
            LayoutUtils.createChildrenAlignmentMap(
                columns = columns,
                rows = rows,
                childCount = childCount,
                defaultAlignment = Alignment(),
                childAlignments = null
            )

        childrenAlignmentMap.shouldHaveSize(childCount)
        for (i in 0 until childCount) {
            childrenAlignmentMap[i] shouldEqual Alignment()
        }
    }

    @Test
    fun `should add default value only if child do not have custom alignment for grid child`() {
        val childCount = 6
        val columns = 2
        val rows = UiGridLayout.DYNAMIC_VALUE

        val defaultAlignment =
            Alignment(Alignment.VerticalAlignment.TOP, Alignment.HorizontalAlignment.LEFT)
        val customAlignment1_1 =
            Alignment(Alignment.VerticalAlignment.CENTER, Alignment.HorizontalAlignment.CENTER)
        val customAlignment1_2 =
            Alignment(Alignment.VerticalAlignment.BOTTOM, Alignment.HorizontalAlignment.RIGHT)
        val childCustomAlignments = mapOf(
            Pair(1, 1) to customAlignment1_1,
            Pair(1, 2) to customAlignment1_2
        )

        val childrenAlignmentMap = LayoutUtils.createChildrenAlignmentMap(
            columns = columns,
            rows = rows,
            childCount = childCount,
            defaultAlignment = defaultAlignment,
            childAlignments = childCustomAlignments
        )

        childrenAlignmentMap[0] shouldEqual defaultAlignment
        childrenAlignmentMap[1] shouldEqual defaultAlignment
        childrenAlignmentMap[2] shouldEqual defaultAlignment
        childrenAlignmentMap[3] shouldEqual customAlignment1_1
        childrenAlignmentMap[4] shouldEqual defaultAlignment
        childrenAlignmentMap[5] shouldEqual customAlignment1_2
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
}