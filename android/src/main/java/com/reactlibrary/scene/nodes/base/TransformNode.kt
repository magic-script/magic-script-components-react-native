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
 * It's characterised by [properties] bundle based on passed [props].
 * Properties can be added or changed using the [update] function.
 *
 * @param props the initial properties of the node
 * @param hasRenderable indicates whether the node will have a renderable (view, model, etc)
 */
abstract class TransformNode(props: ReadableMap, val hasRenderable: Boolean) : Node() {

    companion object {
        // props
        const val PROP_LOCAL_POSITION = "localPosition"
        const val PROP_LOCAL_SCALE = "localScale"
        const val PROP_LOCAL_ROTATION = "localRotation"
    }

    /**
     * All node's properties (packed to Bundle to avoid "already consumed"
     * exceptions when reading from [ReadableMap])
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
     * Returns true if already started to load the renderable,
     * otherwise false
     * (loading a renderable is an asynchronous operation)
     */
    var renderableRequested = false
        private set

    open fun addChildNode(child: Node) {
        addChild(child)
    }

    /**
     * Returns 2D bounding of the node - the minimum rectangle
     * that includes the node
     */
    abstract fun getBounding(): Bounding

    /**
     * Builds the node by calling [applyProperties] with all initial properties
     */
    open fun build() {
        applyProperties(properties)
    }

    /**
     * Attaches a renderable (view, model) to the node
     * Must be called after the ARCore resources have been initialized
     * @see: https://github.com/google-ar/sceneform-android-sdk/issues/574
     */
    fun attachRenderable() {
        loadRenderable()
        renderableRequested = true
    }

    /**
     * Updates properties of the node.
     * Should be called after [build]
     *
     * @param props properties to change or new properties to apply
     */
    fun update(props: ReadableMap) {
        val propsToUpdate = Arguments.toBundle(props) ?: Bundle()
        this.properties.putAll(propsToUpdate) // save new props

        logMessage("updating properties: $propsToUpdate")
        applyProperties(propsToUpdate)
    }

    /**
     * Applies the properties to the node
     * @param props properties to apply
     */
    protected open fun applyProperties(props: Bundle) {
        setPosition(props)
        setLocalScale(props)
        setLocalRotation(props)
    }

    /**
     * If the node contains a renderable, it should be loaded
     * and assigned in this method
     */
    protected open fun loadRenderable() {}

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