/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.magicleap.magicscript.scene.nodes.toggle

import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.scene.nodes.GroupNode
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout
import com.magicleap.magicscript.utils.putDefault
import com.magicleap.magicscript.utils.read

class ToggleGroupNode(initProps: ReadableMap) : GroupNode(initProps) {

    companion object {
        const val PROP_ALLOW_MULTIPLE_ON = "allowMultipleOn"
        const val PROP_ALLOW_ALL_OFF = "allowAllOff"
        const val PROP_FORCE_ALL_OFF = "allTogglesOff"
    }

    init {
        properties.putDefault(PROP_ALLOW_MULTIPLE_ON, false)
        properties.putDefault(PROP_ALLOW_ALL_OFF, false)
        properties.putDefault(PROP_FORCE_ALL_OFF, false)
    }

    private var togglesList = mutableListOf<UiToggleNode>()

    fun updateToggle(toggleNode: UiToggleNode, wantBeActive: Boolean) {
        if (wantBeActive) {
            if (multiSelectAllowed()) {
                toggleNode.isOn = true
            } else {
                // deactivate other active nodes
                togglesList.filter { it.isOn && it != toggleNode }.forEach { activeToggle ->
                    activeToggle.isOn = false
                }
                toggleNode.isOn = true
            }
        } else { // want be off
            if (allOffAllowed()) {
                toggleNode.isOn = false
            } else {
                val inactiveToggles = togglesList.count { !it.isOn }
                if (inactiveToggles < togglesList.size - 1) {
                    toggleNode.isOn = false
                }
            }
        }
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_ALLOW_MULTIPLE_ON) || props.containsKey(PROP_ALLOW_ALL_OFF)) {
            togglesList.forEach { toggle ->
                updateToggle(toggle, toggle.isOn)
            }
        }
        setForceAllOff(props)
    }

    override fun addContent(child: TransformNode) {
        super.addContent(child)

        if (child is UiBaseLayout<*>) {
            child.childrenList
                .filterIsInstance<UiToggleNode>()
                .forEach { toggle ->
                    togglesList.add(toggle)
                    updateToggle(toggle, toggle.isOn)
                }

            child.onAddedToLayoutListener = { node ->
                if (node is UiToggleNode) {
                    togglesList.add(node)
                    updateToggle(node, node.isOn)
                }
            }
            child.onRemovedFromLayoutListener = { node ->
                if (node is UiToggleNode) {
                    togglesList.remove(node)
                }
            }
        } else if (child is UiToggleNode) {
            togglesList.add(child)
            updateToggle(child, child.isOn)
        }

        setForceAllOff(properties)
    }

    override fun removeContent(child: TransformNode) {
        super.removeContent(child)
        if (child is UiToggleNode) {
            togglesList.remove(child)
        }
    }

    private fun setForceAllOff(props: Bundle) {
        props.read<Boolean>(PROP_FORCE_ALL_OFF)?.let { forceAllOff ->
            if (forceAllOff) {
                togglesList.forEach { toggle ->
                    toggle.isOn = false
                }
            }
        }
    }

    private fun multiSelectAllowed(): Boolean = properties.getBoolean(PROP_ALLOW_MULTIPLE_ON)

    private fun allOffAllowed(): Boolean = properties.getBoolean(PROP_ALLOW_ALL_OFF)

}