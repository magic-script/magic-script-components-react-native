/*
 *  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.magicleap.magicscript.utils.ActivityProvider

class DialogProviderImpl(private val activityProvider: ActivityProvider) : DialogProvider {

    override fun provideDatePickerDialog(): DatePickerDialog? {
        val activity = activityProvider.provideCurrentActivity() ?: return null
        return DatePickerDialog(activity)
    }

    override fun provideTimePickerDialog(
        onTimeSetListener: TimePickerDialog.OnTimeSetListener,
        is24HourView: Boolean
    ): NotifiableTimePickerDialog? {
        val activity = activityProvider.provideCurrentActivity() ?: return null
        return NotifiableTimePickerDialog(activity, onTimeSetListener, is24HourView)
    }

    override fun provideColorPickerDialog(): ColorPickerDialog? {
        val activity = activityProvider.provideCurrentActivity() ?: return null
        return ColorPickerDialog(activity)
    }

    override fun provideCustomAlertDialogBuilder(): CustomAlertDialogBuilder? {
        val activity = activityProvider.provideCurrentActivity() ?: return null
        return CustomAlertDialogBuilder(activity)
    }
}