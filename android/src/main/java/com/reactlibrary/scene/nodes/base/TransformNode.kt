package com.reactlibrary.scene.nodes.base

import android.os.Bundle
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.logMessage

/**
 * Base node.
 * It's characterised by [properties] bundle based on [props].
 * Some properties may be added or changed on [update] function.
 * @param props the initial properties of the node
 */
abstract class TransformNode(props: ReadableMap) : Node() {

    companion object {
        // props
        const val PROP_LOCAL_POSITION = "localPosition"
        const val PROP_LOCAL_SCALE = "localScale"
        const val PROP_LOCAL_ROTATION = "localRotation"
    }

    /**
     * All node's properties
     * Packed to Bundle to avoid "already consumed" bugs
     */
    protected val properties = Arguments.toBundle(props) ?: Bundle()

    init {
        // Set default properties if not present
        if (!properties.containsKey(PROP_LOCAL_POSITION)) {
            val position: ArrayList<Double> = arrayListOf(0.0, 0.0, 0.0)
            properties.putSerializable(PROP_LOCAL_POSITION, position)
        }
        logMessage("initial properties = ${this.properties}")
    }

    /**
     * Return true if already tried to attach the renderable (view or model),
     * otherwise false
     */
    var renderableRequested = false
        private set

    open fun addChildNode(child: Node) {
        addChild(child)
    }

    /**
     * Returns 2D (x, y) bounding of the node - the minimum rectangle
     * that include the node
     */
    abstract fun getBounding(): Bounding

    /**
     * Builds the node by calling [applyProperties] with all initial properties
     */
    open fun build() {
        applyProperties(properties)
    }

    /**
     * Updates properties of the node
     * It should be called after [build]
     * @param props properties to change or new properties to apply
     */
    fun update(props: ReadableMap) {
        val propsToUpdate = Arguments.toBundle(props) ?: Bundle()
        this.properties.putAll(propsToUpdate) // save new props

        logMessage("updating properties: $propsToUpdate")
        applyProperties(propsToUpdate)
    }

    /**
     * Applies props on the node.localRotation
     * @param update if true it's called on [update],
     * else it's called when initialized ([build])
     */
    protected open fun applyProperties(props: Bundle) {
        setPosition(props)
        setLocalScale(props)
        setLocalRotation(props)
    }

    /**
     * Should attach renderable to the node (view or model)
     */
    fun attachRenderable() {
        renderableRequested = loadRenderable()
    }

    /** Should assign renderable to the node (if any)
     *  @return true if renderable has been assigned to the node, false otherwise
     */
    protected abstract fun loadRenderable(): Boolean

    private fun setPosition(props: Bundle) {
        val localPosition = PropertiesReader.readVector3(props, PROP_LOCAL_POSITION)
        if (localPosition != null) {
            this.localPosition = localPosition
        }
    }

    private fun setLocalScale(props: Bundle) {
        val localScale = PropertiesReader.readVector3(props, PROP_LOCAL_SCALE)
        if (localScale != null) {
            this.localScale = localScale
        }
    }

    private fun setLocalRotation(props: Bundle) {
        val quaternionData = props.getSerializable(PROP_LOCAL_ROTATION)
        if (quaternionData != null && quaternionData is ArrayList<*>) {
            quaternionData as ArrayList<Double>
            if (quaternionData.size == 4) {
                val x = quaternionData[0].toFloat()
                val y = quaternionData[1].toFloat()
                val z = quaternionData[2].toFloat()
                val w = quaternionData[3].toFloat()
                this.localRotation = Quaternion(x, y, z, w) // Quaternion.axisAngle
            }
        }
    }

}