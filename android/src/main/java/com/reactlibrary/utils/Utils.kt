package com.reactlibrary.utils

import android.content.Context
import android.net.Uri
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Box
import com.reactlibrary.BuildConfig
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.props.Bounding
import java.io.File

/**
 * Class containing general purpose utility functions
 */
class Utils {

    companion object {

        // By default, every 250dp for the view becomes 1 meter for the renderable
        // https://developers.google.com/ar/develop/java/sceneform/create-renderables
        private const val DP_TO_METER_RATIO = 250

        private const val DEBUG_ASSETS_PATH = "http://localhost:8081/assets/"

        /**
         * Converts React project's image path to path
         * that can be accessed from android code.
         */
        fun getImagePath(imagePath: String, context: Context): Uri {
            val path = parseNormalPath(imagePath)
            if (path != null) {
                return path
            }

            // e.g. resources\DemoPicture1.jpg
            return if (BuildConfig.DEBUG) {
                Uri.parse(DEBUG_ASSETS_PATH + imagePath)
            } else {
                val packageName = context.packageName
                val basePath = "android.resource://$packageName/drawable/"
                // resources\DemoPicture1.jpg is copied to
                // res/drawable with file name = "resources_demopicture1"
                val fileName = imagePath.toLowerCase().replace("/", "_")
                Uri.parse(basePath + fileName)
            }
        }

        /**
         *
         * Converts React project's file path (other than image) or standard path to path
         * that can be accessed from android code.
         *
         * TODO currently debug path works only when device is connected to PC
         */
        fun getFilePath(filePath: String, context: Context): Uri {
            val path = parseNormalPath(filePath)
            if (path != null) {
                return path
            }

            // e.g. resources\model.glb
            return if (BuildConfig.DEBUG) {
                Uri.parse(DEBUG_ASSETS_PATH + filePath)
            } else {
                val packageName = context.packageName
                val basePath = "android.resource://$packageName/raw/"
                // TODO check if resources\model.glb is copied to
                // TODO res/raw with file name = "resources_model"
                val fileName = filePath.toLowerCase().replace("/", "_")
                Uri.parse(basePath + fileName)
            }
        }

        /**
         *  Converts ARCore's meters to pixels
         *  (Uses an average of horizontal and vertical density -
         *  usually they are almost the same)
         */
        fun metersToPx(meters: Float, context: Context): Int {
            val xdpi = context.resources.displayMetrics.xdpi
            val ydpi = context.resources.displayMetrics.ydpi
            val densityAvgFactor = (xdpi + ydpi) / 320
            return (meters * DP_TO_METER_RATIO * densityAvgFactor).toInt()
        }

        /**
         * Converts path to Uri
         */
        private fun parseNormalPath(path: String): Uri? {
            // check if it's a remote path
            if (path.startsWith("http")) {
                return Uri.parse(path)
            }

            // check if it's a standard filesystem path (e.g from react-native-fs library)
            val file = File(path)
            try {
                if (file.exists()) {
                    return Uri.fromFile(file)
                }
            } catch (e: SecurityException) {
                logMessage("cannot read file: $path exception: $e")
            }
            return null
        }

        /**
         * Calculates local bounds of a node using its collision shape
         */
        fun calculateBoundsOfNode(node: Node): Bounding {
            // TODO add Sphere collision shape support (there are 2 types of possible shapes)
            val collShape = node.collisionShape
            return if (collShape is Box) {
                val left = collShape.center.x - collShape.size.x / 2 + node.localPosition.x
                val right = collShape.center.x + collShape.size.x / 2 + node.localPosition.x
                val top = collShape.center.y + collShape.size.y / 2 + node.localPosition.y
                val bottom = collShape.center.y - collShape.size.y / 2 + node.localPosition.y
                Bounding(left, bottom, right, top)
            } else {
                // default
                Bounding(node.localPosition.x, node.localPosition.y, node.localPosition.x, node.localPosition.y)
            }
        }

        /**
         * Calculates local bounds of group of nodes
         * (minimum possible frame that contains all [nodes])
         */
        fun calculateSumBounds(nodes: List<Node>): Bounding {
            val bounds = Bounding(0f, 0f, 0f, 0f)

            for (node in nodes) {
                val childBounds = if (node is TransformNode) {
                    node.getBounding()
                } else {
                    calculateBoundsOfNode(node)
                }

                if (childBounds.left < bounds.left) {
                    bounds.left = childBounds.left
                }
                if (childBounds.right > bounds.right) {
                    bounds.right = childBounds.right
                }
                if (childBounds.top > bounds.top) {
                    bounds.top = childBounds.top
                }
                if (childBounds.bottom < bounds.bottom) {
                    bounds.bottom = childBounds.bottom
                }
            }

            return bounds
        }


    }

}

