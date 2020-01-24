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

package com.magicleap.magicscript

import android.os.Bundle
import com.google.ar.sceneform.Node
import com.magicleap.magicscript.scene.NodesManager
import com.magicleap.magicscript.scene.nodes.*
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiNode
import com.magicleap.magicscript.scene.nodes.dropdown.UiDropdownListNode
import com.magicleap.magicscript.scene.nodes.picker.NativeFilePickerNode
import com.magicleap.magicscript.scene.nodes.toggle.UiToggleNode
import com.magicleap.magicscript.scene.nodes.video.VideoNode

class ReactEventsManager(
    private val eventsEmitter: EventsEmitter,
    private val nodesManager: NodesManager
) : EventsManager {

    companion object {

        // Supported event names
        const val EVENT_CLICK = "onClick"
        const val EVENT_ACTIVATE = "onActivate" // = onClick
        const val EVENT_PRESS = "onPress"
        const val EVENT_LONG_PRESS = "onLongPress"
        const val EVENT_RELEASE = "onRelease"
        const val EVENT_FOCUS_GAINED = "onFocusGained"
        const val EVENT_FOCUS_LOST = "onFocusLost"
        const val EVENT_NODE_ENABLED = "onEnabled"
        const val EVENT_NODE_DISABLED = "onDisabled"
        const val EVENT_NODE_UPDATED = "onUpdate"
        const val EVENT_NODE_DELETED = "onDelete"

        const val EVENT_TEXT_CHANGED = "onTextChanged"
        const val EVENT_TOGGLE_CHANGED = "onToggleChanged"
        const val EVENT_VIDEO_PREPARED = "onVideoPrepared"
        const val EVENT_DROPDOWN_SELECTION_CHANGED = "onSelectionChanged"
        const val EVENT_SLIDER_VALUE_CHANGED = "onSliderChanged"
        const val EVENT_COLOR_CONFIRMED = "onColorConfirmed"
        const val EVENT_COLOR_CANCELLED = "onColorCanceled"
        const val EVENT_COLOR_CHANGED = "onColorChanged"
        const val EVENT_DATE_CHANGED = "onDateChanged"
        const val EVENT_DATE_CONFIRMED = "onDateConfirmed"
        const val EVENT_TIME_CHANGED = "onTimeChanged"
        const val EVENT_TIME_CONFIRMED = "onTimeConfirmed"
        const val EVENT_SCROLL_CHANGED = "onScrollChanged"
        const val EVENT_DIALOG_CONFIRMED = "onDialogConfirmed"
        const val EVENT_DIALOG_CANCELED = "onDialogCanceled"
        const val EVENT_DIALOG_EXPIRED = "onDialogTimeExpired"
        const val EVENT_CONFIRMATION_COMPLETED = "onConfirmationCompleted"
        const val EVENT_CONFIRMATION_UPDATED = "onConfirmationUpdated"
        const val EVENT_CONFIRMATION_CANCELED = "onConfirmationCanceled"
        const val EVENT_FILE_SELECTED = "onFileSelected"

        // Supported events arguments
        const val EVENT_ARG_NODE_ID = "nodeId"
        const val EVENT_ARG_TEXT = "text"
        const val EVENT_ARG_TOGGLE_ACTIVE = "On"
        const val EVENT_ARG_SELECTED_ITEMS = "SelectedItems"
        const val EVENT_ARG_SELECTED_ITEM_ID = "id"
        const val EVENT_ARG_SELECTED_ITEM_LABEL = "label"
        const val EVENT_ARG_SLIDER_VALUE = "Value"
        const val EVENT_ARG_COLOR = "color"
        const val EVENT_ARG_DATE = "date"
        const val EVENT_ARG_TIME = "time"
        const val EVENT_ARG_SCROLL_VALUE = "ScrollValue"
        const val EVENT_ARG_CONFIRMATION_UPDATED_VALUE = "Angle"
        const val EVENT_ARG_FILE_PATH = "filePath"
    }

    // = onClick
    override fun addOnActivateEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiNode) {
            node.onClickListener = {
                val clickParams = Bundle()
                clickParams.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_ACTIVATE, clickParams)
                sendEvent(EVENT_CLICK, clickParams)
            }
        }
    }

    override fun addOnPressEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiNode) {
            node.onPressListener = {
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_PRESS, params)
            }
        }
    }

    override fun addOnLongPressEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiNode) {
            node.onLongPressListener = {
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_LONG_PRESS, params)
            }
        }
    }

    override fun addOnReleaseEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiNode) {
            node.onReleaseListener = {
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_RELEASE, params)
            }
        }
    }

    override fun addOnFocusGainedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiNode) {
            node.onFocusGainedListener = {
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_FOCUS_GAINED, params)
            }
        }
    }

    override fun addOnFocusLostEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiNode) {
            node.onFocusLostListener = {
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_FOCUS_LOST, params)
            }
        }
    }

    override fun addOnUpdateEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is TransformNode) {
            node.onUpdatedListener = {
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_NODE_UPDATED, params)
            }
        }
    }

    override fun addOnDeleteEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is TransformNode) {
            node.onDeletedListener = {
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_NODE_DELETED, params)
            }
        }
    }

    override fun addOnEnabledEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiNode) {
            node.onEnabledListener = {
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_NODE_ENABLED, params)
            }
        }
    }

    override fun addOnDisabledEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiNode) {
            node.onDisabledListener = {
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_NODE_DISABLED, params)
            }
        }
    }

    override fun addOnTextChangedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiTextEditNode) {
            node.onTextChangedListener = { text: String ->
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putString(EVENT_ARG_TEXT, text)
                sendEvent(EVENT_TEXT_CHANGED, params)
            }
        }
    }

    override fun addOnToggleChangedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiToggleNode) {
            node.toggleChangedListener = { isOn: Boolean ->
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putBoolean(EVENT_ARG_TOGGLE_ACTIVE, isOn)
                sendEvent(EVENT_TOGGLE_CHANGED, params)
            }
        }
    }

    override fun addOnVideoPreparedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is VideoNode) {
            node.onVideoPreparedListener = {
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_VIDEO_PREPARED, params)
            }
        }
    }

    override fun addOnSliderChangedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiSliderNode) {
            node.setOnSliderChangedListener { value: Float ->
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putDouble(EVENT_ARG_SLIDER_VALUE, value.toDouble())
                sendEvent(EVENT_SLIDER_VALUE_CHANGED, params)
            }
        }
    }

    override fun addOnSelectionChangedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiDropdownListNode) {
            node.onSelectionChangedListener = { selectedItems ->
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                val selectedItemsArray = arrayListOf<Bundle>()
                selectedItems.forEach { item ->
                    val itemInfo = Bundle().apply {
                        putInt(EVENT_ARG_SELECTED_ITEM_ID, item.id)
                        putString(EVENT_ARG_SELECTED_ITEM_LABEL, item.label)
                    }
                    selectedItemsArray.add(itemInfo)
                }
                params.putSerializable(EVENT_ARG_SELECTED_ITEMS, selectedItemsArray)
                sendEvent(EVENT_DROPDOWN_SELECTION_CHANGED, params)
            }
        }
    }

    override fun addOnColorConfirmedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiColorPickerNode) {
            node.onColorConfirmed = { colors: Array<Double> ->
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                val selectedItems = arrayListOf<Double>()
                for (color in colors) {
                    selectedItems.add(color)
                }
                params.putSerializable(EVENT_ARG_COLOR, selectedItems)
                sendEvent(EVENT_COLOR_CONFIRMED, params)
            }
        }
    }

    override fun addOnColorCanceledEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiColorPickerNode) {
            node.onColorCanceled = {
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_COLOR_CANCELLED, params)
            }
        }
    }

    override fun addOnColorChangedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiColorPickerNode) {
            node.onColorChanged = { colors: Array<Double> ->
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                val selectedItems = arrayListOf<Double>()
                for (color in colors) {
                    selectedItems.add(color)
                }
                params.putSerializable(EVENT_ARG_COLOR, selectedItems)
                sendEvent(EVENT_COLOR_CHANGED, params)
            }
        }
    }

    override fun addOnDateChangedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiDatePickerNode) {
            node.onDateChanged = { date: String ->
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putString(EVENT_ARG_DATE, date)
                sendEvent(EVENT_DATE_CHANGED, params)
            }
        }
    }

    override fun addOnDateConfirmedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiDatePickerNode) {
            node.onDateConfirmed = { date: String ->
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putString(EVENT_ARG_DATE, date)
                sendEvent(EVENT_DATE_CONFIRMED, params)
            }
        }
    }

    override fun addOnScrollChangedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiScrollViewNode) {
            node.onScrollChangeListener = { position: Float ->
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putDouble(EVENT_ARG_SCROLL_VALUE, position.toDouble())
                sendEvent(EVENT_SCROLL_CHANGED, params)
            }
        }
    }

    override fun addOnTimeChangedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiTimePickerNode) {
            node.onTimeChanged = { time: String ->
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putString(EVENT_ARG_TIME, time)
                sendEvent(EVENT_TIME_CHANGED, params)
            }
        }
    }

    override fun addOnTimeConfirmedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiTimePickerNode) {
            node.onTimeConfirmed = { time: String ->
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putString(EVENT_ARG_TIME, time)
                sendEvent(EVENT_TIME_CONFIRMED, params)
            }
        }
    }

    override fun addOnDialogConfirmedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is DialogNode) {
            node.onDialogConfirmListener = {
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_DIALOG_CONFIRMED, params)
            }
        }
    }

    override fun addOnDialogCanceledEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is DialogNode) {
            node.onDialogCancelListener = {
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_DIALOG_CANCELED, params)
            }
        }
    }

    override fun addOnDialogTimeExpiredEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is DialogNode) {
            node.onDialogExpiredListener = {
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_DIALOG_EXPIRED, params)
            }
        }
    }

    override fun addOnConfirmationCompletedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiCircleConfirmationNode) {
            node.onConfirmationCompletedListener = {
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_CONFIRMATION_COMPLETED, params)
            }
        }
    }

    override fun addOnConfirmationUpdatedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiCircleConfirmationNode) {
            node.onConfirmationUpdatedListener = { value: Float ->
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putDouble(EVENT_ARG_CONFIRMATION_UPDATED_VALUE, value.toDouble())
                sendEvent(EVENT_CONFIRMATION_UPDATED, params)
            }
        }
    }

    override fun addOnConfirmationCanceledEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiCircleConfirmationNode) {
            node.onConfirmationCanceledListener = {
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_CONFIRMATION_CANCELED, params)
            }
        }
    }

    override fun addOnFileSelectedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is NativeFilePickerNode) {
            node.onFileSelected = { filePath ->
                val params = Bundle()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putString(EVENT_ARG_FILE_PATH, filePath)
                sendEvent(EVENT_FILE_SELECTED, params)
            }
        }
    }

    private fun sendEvent(eventName: String, params: Bundle) {
        eventsEmitter.sendEvent(eventName, params)
    }

    private fun findNodeWithId(id: String): Node? {
        return nodesManager.findNodeWithId(id)
    }
}