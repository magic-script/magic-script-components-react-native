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

package com.magicleap.magicscript.scene.nodes.views

import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker

class NotifiableTimePickerDialog(
    context: Context,
    onTimeSetListener: OnTimeSetListener,
    is24HourView: Boolean
) : TimePickerDialog(context, onTimeSetListener, 0, 0, is24HourView) {

    var onTimeChange: (hourOfDay: Int, minute: Int) -> Unit = { _, _ -> }

    override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
        super.onTimeChanged(view, hourOfDay, minute)

        onTimeChange(hourOfDay, minute)
    }
}