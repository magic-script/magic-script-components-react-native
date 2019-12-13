package com.magicleap.magicscript.scene.nodes.views

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context

interface DialogProvider {

    fun provideDatePickerDialog(context: Context): DatePickerDialog

    fun provideTimePickerDialog(
        context: Context,
        onTimeSetListener: TimePickerDialog.OnTimeSetListener,
        is24HourView: Boolean
    ): NotifiableTimePickerDialog

    fun provideCustomAlertDialogBuilder(context: Context): CustomAlertDialogBuilder
}