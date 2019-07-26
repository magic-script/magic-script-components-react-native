package com.reactlibrary.scene.nodes.layouts

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.base.UiLayout
import com.reactlibrary.scene.nodes.layouts.manager.FlexGridManager
import com.reactlibrary.scene.nodes.props.Alignment
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.props.Padding
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.logMessage

class UiGridLayout(props: ReadableMap) : UiLayout(props) {

    companion object {
        // properties
        const val PROP_COLUMNS = "columns"
        const val PROP_ROWS = "rows"
        const val PROP_ITEM_PADDING = "itemPadding"
        const val PROP_DEFAULT_ITEM_PADDING = "defaultItemPadding"
        const val PROP_ITEM_ALIGNMENT = "itemAlignment"
        const val PROP_DEFAULT_ITEM_ALIGNMENT = "defaultItemAlignment"

        // default values
        const val COLUMNS_DEFAULT = 1
        const val ROWS_DEFAULT = 0 // 0 means unspecified (will grow with content)
    }

    var columns: Int = properties.getDouble(PROP_COLUMNS, COLUMNS_DEFAULT.toDouble()).toInt()
        private set(value) {
            if (value == 0 && rows == 0) {
                field = 1 // can't be 0 along with rows
            } else {
                field = value
            }

        }

    var rows: Int = properties.getDouble(PROP_ROWS, ROWS_DEFAULT.toDouble()).toInt()
        private set(value) {
            if (value == 0 && columns == 0) {
                field = 1 // can't be 0 along with columns
            } else {
                field = value
            }
        }

    // default padding for each item [top, right, bottom, left]
    var itemPadding = Padding(0F, 0F, 0F, 0F)
        private set

    var itemHorizontalAlignment = Alignment.Horizontal.CENTER
        private set

    var itemVerticalAlignment = Alignment.Vertical.CENTER
        private set

    private var layoutManager: LayoutManager = FlexGridManager(this)

    // we should re-draw the grid after adding / removing a child
    private var shouldRedraw = false

    private var handler = Handler(Looper.getMainLooper())

    // child index, bounding
    private val childrenBounds = mutableMapOf<Int, Bounding>()

    init {
        layoutLoop()
    }

    override fun loadRenderable(): Boolean {

        // for tests only
        handler.postDelayed({
            children.forEachIndexed { index, node ->
                val childBounds = if (node is TransformNode) node.getBounding() else Bounding()
                logMessage("grid child[$index] bounds= $childBounds")
            }

            val bounds = getBounding()
            logMessage("grid bounds= $bounds")
        }, 3000)

        return false
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setColumns(props)
        setRows(props)
        setItemPadding(props)
        setItemAlignment(props)
    }

    override fun getBounding(): Bounding {
        val childBounds = Utils.calculateSumBounds(children)
        return Bounding(
                childBounds.left + localPosition.x - itemPadding.left,
                childBounds.bottom + localPosition.y - itemPadding.bottom,
                childBounds.right + localPosition.x + itemPadding.right,
                childBounds.top + localPosition.y + itemPadding.top
        )
    }

    override fun addChildNode(child: Node) {
        addChild(child)
        shouldRedraw = true
    }

    /**
     * Loop that requests re-drawing the grid if needed.
     * It measures the children, because the nodes' view size is not known
     * from the beginning, also a client may change the view size at any time: we need to
     * re-draw the layout in such case.
     */
    private fun layoutLoop() {
        handler.postDelayed({
            measureChildren()
            if (shouldRedraw) {
                layoutManager.layoutChildren(children, childrenBounds)
                shouldRedraw = false
                logMessage("grid redraw")
            }
            layoutLoop()
        }, 100)
    }

    /**
     * Measures the bounds of children nodes; if any bound has changed
     * it sets the [shouldRedraw] flag to true.
     */
    private fun measureChildren() {
        for (i in 0 until children.size) {
            val node = children[i]
            val oldBounds = childrenBounds[i] ?: Bounding()
            childrenBounds[i] = if (node is TransformNode) {
                node.getBounding()
            } else {
                Utils.calculateBoundsOfNode(node)
            }

            if (!Bounding.equalInexact(childrenBounds[i]!!, oldBounds)) {
                shouldRedraw = true
            }
        }
    }

    private fun setColumns(props: Bundle) {
        if (props.containsKey(PROP_COLUMNS)) {
            columns = props.getDouble(PROP_COLUMNS).toInt()
            shouldRedraw = true
        }
    }

    private fun setRows(props: Bundle) {
        if (props.containsKey(PROP_ROWS)) {
            rows = props.getDouble(PROP_ROWS).toInt()
            shouldRedraw = true
        }
    }

    private fun setItemPadding(props: Bundle) {
        var padding = PropertiesReader.readPadding(props, PROP_ITEM_PADDING)
        if (padding == null) {
            padding = PropertiesReader.readPadding(props, PROP_DEFAULT_ITEM_PADDING)
        }
        if (padding != null) {
            itemPadding = padding
            shouldRedraw = true
        }
    }

    private fun setItemAlignment(props: Bundle) {
        var alignment = props.getString(PROP_ITEM_ALIGNMENT)
        if (alignment == null) {
            alignment = props.getString(PROP_DEFAULT_ITEM_ALIGNMENT)
        }

        if (alignment != null) {
            val alignmentArray = alignment.split("-")
            if (alignmentArray.size == 2) {
                val horizontalAlign = alignmentArray[0]
                val verticalAlign = alignmentArray[1]
                itemHorizontalAlignment = Alignment.Horizontal.valueOf(horizontalAlign.toUpperCase())
                itemVerticalAlignment = Alignment.Vertical.valueOf(verticalAlign.toUpperCase())
            }
        }
    }

}