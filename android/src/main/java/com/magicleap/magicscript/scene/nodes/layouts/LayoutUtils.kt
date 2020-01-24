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
}