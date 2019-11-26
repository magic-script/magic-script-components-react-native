package com.magicleap.magicscript.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import com.magicleap.magicscript.scene.nodes.views.CustomAlertDialogBuilder
import com.magicleap.magicscript.scene.nodes.views.NotifiableTimePickerDialog

interface DialogProvider {

    fun provideDatePickerDialog(
            context: Context
    ): DatePickerDialog

    fun provideTimePickerDialog(
            context: Context,
            onTimeSetListener: TimePickerDialog.OnTimeSetListener,
            is24HourView: Boolean
    ): NotifiableTimePickerDialog

    fun provideCustomAlertDialogBuilder(
            context: Context
    ): CustomAlertDialogBuilder
}