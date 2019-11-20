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

package com.reactlibrary.scene.nodes

import android.os.Bundle
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.reactlibrary.scene.nodes.base.UiLayout
import com.reactlibrary.utils.ifContainsBoolean
import com.reactlibrary.utils.logMessage
import com.reactlibrary.utils.putDefault

class ToggleGroupNode(initProps: ReadableMap) : GroupNode(initProps) {

    companion object {
        const val PROP_ALLOW_MULTIPLE_ON = "allowMultipleOn"
        const val PROP_ALLOW_ALL_OFF = "allowAllOff"
        const val PROP_FORCE_ALL_OFF = "allTogglesOff"
    }

    init {
        properties.putDefault(PROP_ALLOW_MULTIPLE_ON, false)
        properties.putDefault(PROP_ALLOW_ALL_OFF, false)
    }

    private var togglesList = mutableListOf<UiToggleNode>()

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        refreshTogglesState()
    }

    override fun addContent(child: Node) {
        super.addContent(child)

        if (child is UiLayout) {
            child.onAddedToLayoutListener = { node ->
                if (node is UiToggleNode) {
                    togglesList.add(node)
                    refreshTogglesState()
                }
            }

            child.onRemovedFromLayoutListener = { node ->
                if (node is UiToggleNode) {
                    togglesList.remove(node)
                    refreshTogglesState()
                }
            }
        } else if (child is UiToggleNode) {
            togglesList.add(child)
            refreshTogglesState()
        }
    }

    override fun removeContent(child: Node) {
        super.removeContent(child)
        if (child is UiToggleNode) {
            togglesList.remove(child)
        }
    }

    private fun refreshTogglesState() {
        logMessage("refresh toggles=${togglesList.size}")

        properties.ifContainsBoolean(PROP_FORCE_ALL_OFF) { forceAllOff ->
            if (forceAllOff) {
                togglesList.forEach { toggle ->
                    toggle.update(JavaOnlyMap.of(UiToggleNode.PROP_CHECKED, false))
                }
            }
        }
    }

    // find all descendant toggles
    private fun getToggles(node: Node, childrenList: MutableList<UiToggleNode>) {
        if (node is UiToggleNode) {
            childrenList.add(node)
        }
        node.children.forEach { child ->
            getToggles(child, childrenList)
        }
    }
}