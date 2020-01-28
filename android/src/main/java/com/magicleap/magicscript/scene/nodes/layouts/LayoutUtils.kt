package com.magicleap.magicscript.scene.nodes.layouts

import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Padding

object LayoutUtils {

    fun createChildrenPaddingMap(
        childCount: Int,
        defaultPadding: Padding,
        childPaddings: Map<Int, Padding>? = null
    ) = mutableMapOf<Int, Padding>()
        .apply {
            for (i in 0 until childCount) {
                this[i] = childPaddings?.get(i) ?: defaultPadding
            }
        }

    fun createChildrenAlignmentMap(
        childCount: Int,
        defaultAlignment: Alignment,
        childAlignments: Map<Int, Alignment>? = null
    ) = mutableMapOf<Int, Alignment>()
        .apply {
            for (i in 0 until childCount) {
                this[i] = childAlignments?.get(i) ?: defaultAlignment
            }
        }

    fun createChildrenPaddingMap(
        columns: Int,
        rows: Int,
        childCount: Int,
        defaultPadding: Padding,
        childPaddings: Map<Pair<Int, Int>, Padding>? = null
    ) = mutableMapOf<Int, Padding>()
        .apply {
            for (i in 0 until childCount) {
                val column = getColumnIndex(i, columns, rows)
                val row = getRowIndex(i, columns, rows)
                this[i] = childPaddings?.get(Pair(column, row)) ?: defaultPadding
            }
        }

    fun createChildrenAlignmentMap(
        columns: Int,
        rows: Int,
        childCount: Int,
        defaultAlignment: Alignment,
        childAlignments: Map<Pair<Int, Int>, Alignment>? = null
    ) = mutableMapOf<Int, Alignment>()
        .apply {
            for (i in 0 until childCount) {
                val column = getColumnIndex(i, columns, rows)
                val row = getRowIndex(i, columns, rows)
                this[i] = childAlignments?.get(Pair(column, row)) ?: defaultAlignment
            }
        }

    fun getColumnIndex(
        childIdx: Int,
        columns: Int,
        rows: Int
    ): Int {
        return if (rows != 0) {
            childIdx / rows
        } else {
            childIdx % columns
        }
    }

    fun getRowIndex(
        childIdx: Int,
        columns: Int,
        rows: Int
    ): Int {
        return if (rows != 0) {
            childIdx % rows
        } else {
            childIdx / columns
        }
    }
}