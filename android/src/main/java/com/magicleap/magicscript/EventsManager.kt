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

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.magicleap.magicscript.scene.UiNodesManager.findNodeWithId
import com.magicleap.magicscript.scene.nodes.*
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiNode
import com.magicleap.magicscript.scene.nodes.toggle.UiToggleNode
import com.magicleap.magicscript.scene.nodes.video.VideoNode

class EventsManager(private val context: ReactApplicationContext) {

    companion object {

        // Supported event names
        private const val EVENT_CLICK = "onClick"
        private const val EVENT_ACTIVATE = "onActivate" // = onClick
        private const val EVENT_PRESS = "onPress"
        private const val EVENT_LONG_PRESS = "onLongPress"
        private const val EVENT_RELEASE = "onRelease"
        private const val EVENT_FOCUS_GAINED = "onFocusGained"
        private const val EVENT_FOCUS_LOST = "onFocusLost"
        private const val EVENT_NODE_ENABLED = "onEnabled"
        private const val EVENT_NODE_DISABLED = "onDisabled"
        private const val EVENT_NODE_UPDATED = "onUpdate"
        private const val EVENT_NODE_DELETED = "onDelete"

        private const val EVENT_TEXT_CHANGED = "onTextChanged"
        private const val EVENT_TOGGLE_CHANGED = "onToggleChanged"
        private const val EVENT_VIDEO_PREPARED = "onVideoPrepared"
        private const val EVENT_DROPDOWN_SELECTION_CHANGED = "onSelectionChanged"
        private const val EVENT_SLIDER_VALUE_CHANGED = "onSliderChanged"
        private const val EVENT_COLOR_CONFIRMED = "onColorConfirmed"
        private const val EVENT_COLOR_CANCELLED = "onColorCanceled"
        private const val EVENT_COLOR_CHANGED = "onColorChanged"
        private const val EVENT_DATE_CHANGED = "onDateChanged"
        private const val EVENT_DATE_CONFIRMED = "onDateConfirmed"
        private const val EVENT_TIME_CHANGED = "onTimeChanged"
        private const val EVENT_TIME_CONFIRMED = "onTimeConfirmed"
        private const val EVENT_SCROLL_CHANGED = "onScrollChanged"
        private const val EVENT_DIALOG_CONFIRMED = "onDialogConfirmed"
        private const val EVENT_DIALOG_CANCELED = "onDialogCanceled"
        private const val EVENT_DIALOG_EXPIRED = "onDialogTimeExpired"
        private const val EVENT_CONFIRMATION_COMPLETED = "onConfirmationCompleted"
        private const val EVENT_CONFIRMATION_UPDATED = "onConfirmationUpdated"
        private const val EVENT_CONFIRMATION_CANCELED = "onConfirmationCanceled"

        // Supported events arguments
        private const val EVENT_ARG_NODE_ID = "nodeId"
        private const val EVENT_ARG_TEXT = "text"
        private const val EVENT_ARG_TOGGLE_ACTIVE = "On"
        private const val EVENT_ARG_SELECTED_ITEMS = "selectedItemsIndexes"
        private const val EVENT_ARG_SLIDER_VALUE = "Value"
        private const val EVENT_ARG_COLOR = "color"
        private const val EVENT_ARG_DATE = "date"
        private const val EVENT_ARG_TIME = "time"
        private const val EVENT_ARG_SCROLL_VALUE = "ScrollValue"
        private const val EVENT_ARG_CONFIRMATION_UPDATED_VALUE = "Angle"
    }

    // = onClick
    fun addOnActivateEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiNode) {
            node.onClickListener = {
                val activateParams = Arguments.createMap()
                activateParams.putString(EVENT_ARG_NODE_ID, nodeId)
                // must use separate map
                val clickParams = Arguments.createMap()
                clickParams.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_ACTIVATE, activateParams)
                sendEvent(EVENT_CLICK, clickParams)
            }
        }
    }

    fun addOnPressEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiNode) {
            node.onPressListener = {
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_PRESS, params)
            }
        }
    }

    fun addOnLongPressEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiNode) {
            node.onLongPressListener = {
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_LONG_PRESS, params)
            }
        }
    }

    fun addOnReleaseEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiNode) {
            node.onReleaseListener = {
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_RELEASE, params)
            }
        }
    }

    fun addOnFocusGainedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiNode) {
            node.onFocusGainedListener = {
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_FOCUS_GAINED, params)
            }
        }
    }

    fun addOnFocusLostEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiNode) {
            node.onFocusLostListener = {
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_FOCUS_LOST, params)
            }
        }
    }

    fun addOnUpdateEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is TransformNode) {
            node.onUpdatedListener = {
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_NODE_UPDATED, params)
            }
        }
    }

    fun addOnDeleteEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is TransformNode) {
            node.onDeletedListener = {
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_NODE_DELETED, params)
            }
        }
    }

    fun addOnEnabledEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiNode) {
            node.onEnabledListener = {
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_NODE_ENABLED, params)
            }
        }
    }

    fun addOnDisabledEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiNode) {
            node.onDisabledListener = {
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_NODE_DISABLED, params)
            }
        }
    }

    fun addOnTextChangedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiTextEditNode) {
            node.textChangedListener = { text: String ->
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putString(EVENT_ARG_TEXT, text)
                sendEvent(EVENT_TEXT_CHANGED, params)
            }
        }
    }

    fun addOnToggleChangedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiToggleNode) {
            node.toggleChangedListener = { isOn: Boolean ->
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putBoolean(EVENT_ARG_TOGGLE_ACTIVE, isOn)
                sendEvent(EVENT_TOGGLE_CHANGED, params)
            }
        }
    }

    fun addOnVideoPreparedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is VideoNode) {
            node.onVideoPreparedListener = {
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_VIDEO_PREPARED, params)
            }
        }
    }

    fun addOnSliderChangedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiSliderNode) {
            node.setOnSliderChangedListener { value: Float ->
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putDouble(EVENT_ARG_SLIDER_VALUE, value.toDouble())
                sendEvent(EVENT_SLIDER_VALUE_CHANGED, params)
            }
        }
    }

    fun addOnSelectionChangedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiDropdownListNode) {
            node.onSelectionChangedListener = { itemIndex: Int ->
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                val selectedItems = Arguments.createArray()
                selectedItems.pushInt(itemIndex)
                params.putArray(EVENT_ARG_SELECTED_ITEMS, selectedItems)
                sendEvent(EVENT_DROPDOWN_SELECTION_CHANGED, params)
            }
        }
    }

    fun addOnColorConfirmedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiColorPickerNode) {
            node.onColorConfirmed = { colors: Array<Double> ->
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                val selectedItems = Arguments.createArray()
                for (color in colors) {
                    selectedItems.pushDouble(color)
                }
                params.putArray(EVENT_ARG_COLOR, selectedItems)
                sendEvent(EVENT_COLOR_CONFIRMED, params)
            }
        }
    }

    fun addOnColorCanceledEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiColorPickerNode) {
            node.onColorCanceled = {
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_COLOR_CANCELLED, params)
            }
        }
    }

    fun addOnColorChangedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiColorPickerNode) {
            node.onColorConfirmed = { colors: Array<Double> ->
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                val selectedItems = Arguments.createArray()
                for (color in colors) {
                    selectedItems.pushDouble(color)
                }
                params.putArray(EVENT_ARG_COLOR, selectedItems)
                sendEvent(EVENT_COLOR_CHANGED, params)
            }
        }
    }

    fun addOnDateChangedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiDatePickerNode) {
            node.onDateChanged = { date: String ->
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putString(EVENT_ARG_DATE, date)
                sendEvent(EVENT_DATE_CHANGED, params)
            }
        }
    }

    fun addOnDateConfirmedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiDatePickerNode) {
            node.onDateConfirmed = { date: String ->
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putString(EVENT_ARG_DATE, date)
                sendEvent(EVENT_DATE_CONFIRMED, params)
            }
        }
    }

    fun addOnScrollChangedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiScrollViewNode) {
            node.onScrollChangeListener = { position: Float ->
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putDouble(EVENT_ARG_SCROLL_VALUE, position.toDouble())
                sendEvent(EVENT_SCROLL_CHANGED, params)
            }
        }
    }

    fun addOnTimeChangedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiTimePickerNode) {
            node.onTimeChanged = { time: String ->
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putString(EVENT_ARG_TIME, time)
                sendEvent(EVENT_TIME_CHANGED, params)
            }
        }
    }

    fun addOnTimeConfirmedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiTimePickerNode) {
            node.onTimeConfirmed = { time: String ->
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putString(EVENT_ARG_TIME, time)
                sendEvent(EVENT_TIME_CONFIRMED, params)
            }
        }
    }

    fun addOnDialogConfirmedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is DialogNode) {
            node.onDialogConfirmListener = {
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_DIALOG_CONFIRMED, params)
            }
        }
    }

    fun addOnDialogCanceledEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is DialogNode) {
            node.onDialogCancelListener = {
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_DIALOG_CANCELED, params)
            }
        }
    }

    fun addOnDialogTimeExpiredEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is DialogNode) {
            node.onDialogExpiredListener = {
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_DIALOG_EXPIRED, params)
            }
        }
    }

    fun addOnConfirmationCompletedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiCircleConfirmationNode) {
            node.onConfirmationCompletedListener = {
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_CONFIRMATION_COMPLETED, params)
            }
        }
    }

    fun addOnConfirmationUpdatedEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiCircleConfirmationNode) {
            node.onConfirmationUpdatedListener = { value: Float ->
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                params.putDouble(EVENT_ARG_CONFIRMATION_UPDATED_VALUE, value.toDouble())
                sendEvent(EVENT_CONFIRMATION_UPDATED, params)
            }
        }
    }

    fun addOnConfirmationCanceledEventHandler(nodeId: String) {
        val node = findNodeWithId(nodeId)
        if (node is UiCircleConfirmationNode) {
            node.onConfirmationCanceledListener = {
                val params = Arguments.createMap()
                params.putString(EVENT_ARG_NODE_ID, nodeId)
                sendEvent(EVENT_CONFIRMATION_CANCELED, params)
            }
        }
    }

    private fun sendEvent(eventName: String, params: WritableMap?) {
        context.getJSModule(RCTDeviceEventEmitter::class.java).emit(eventName, params)
    }
}