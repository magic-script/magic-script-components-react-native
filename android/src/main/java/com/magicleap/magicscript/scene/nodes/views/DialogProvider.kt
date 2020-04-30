package com.magicleap.magicscript.scene.nodes.views

import android.app.DatePickerDialog
import android.app.TimePickerDialog

interface DialogProvider {

    fun provideDatePickerDialog(): DatePickerDialog?

    fun provideTimePickerDialog(
        onTimeSetListener: TimePickerDialog.OnTimeSetListener,
        is24HourView: Boolean
    ): NotifiableTimePickerDialog?

    fun provideColorPickerDialog(): ColorPickerDialog?

    fun provideCustomAlertDialogBuilder(): CustomAlertDialogBuilder?
}