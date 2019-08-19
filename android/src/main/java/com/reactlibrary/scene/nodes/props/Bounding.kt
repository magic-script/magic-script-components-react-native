package com.reactlibrary.scene.nodes.props

import kotlin.math.abs

/**
 * Represents bounds of a node
 */
data class Bounding(
        var left: Float = 0f,
        var bottom: Float = 0f,
        var right: Float = 0f,
        var top: Float = 0f
) {
    companion object {

        private const val eps = 1e-5 // epsilon

        /**
         * Compares 2 bounds and returns true if they are the same
         * with the accuracy of [eps]
         */
        fun equalInexact(a: Bounding, b: Bounding): Boolean {
            return abs(a.left - b.left) < eps
                    && abs(a.right - b.right) < eps
                    && abs(a.bottom - b.bottom) < eps
                    && abs(a.top - b.top) < eps

        }
    }

}