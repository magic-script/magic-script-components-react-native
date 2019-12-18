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

interface EventsManager {
    // = onClick
    fun addOnActivateEventHandler(nodeId: String)

    fun addOnPressEventHandler(nodeId: String)
    fun addOnLongPressEventHandler(nodeId: String)
    fun addOnReleaseEventHandler(nodeId: String)
    fun addOnFocusGainedEventHandler(nodeId: String)
    fun addOnFocusLostEventHandler(nodeId: String)
    fun addOnUpdateEventHandler(nodeId: String)
    fun addOnDeleteEventHandler(nodeId: String)
    fun addOnEnabledEventHandler(nodeId: String)
    fun addOnDisabledEventHandler(nodeId: String)
    fun addOnTextChangedEventHandler(nodeId: String)
    fun addOnToggleChangedEventHandler(nodeId: String)
    fun addOnVideoPreparedEventHandler(nodeId: String)
    fun addOnSliderChangedEventHandler(nodeId: String)
    fun addOnSelectionChangedEventHandler(nodeId: String)
    fun addOnColorConfirmedEventHandler(nodeId: String)
    fun addOnColorCanceledEventHandler(nodeId: String)
    fun addOnColorChangedEventHandler(nodeId: String)
    fun addOnDateChangedEventHandler(nodeId: String)
    fun addOnDateConfirmedEventHandler(nodeId: String)
    fun addOnScrollChangedEventHandler(nodeId: String)
    fun addOnTimeChangedEventHandler(nodeId: String)
    fun addOnTimeConfirmedEventHandler(nodeId: String)
    fun addOnDialogConfirmedEventHandler(nodeId: String)
    fun addOnDialogCanceledEventHandler(nodeId: String)
    fun addOnDialogTimeExpiredEventHandler(nodeId: String)
    fun addOnConfirmationCompletedEventHandler(nodeId: String)
    fun addOnConfirmationUpdatedEventHandler(nodeId: String)
    fun addOnConfirmationCanceledEventHandler(nodeId: String)
    fun addOnFileSelectedEventHandler(nodeId: String)
}