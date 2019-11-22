package com.reactlibrary.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import com.reactlibrary.scene.nodes.views.CustomAlertDialogBuilder
import com.reactlibrary.scene.nodes.views.NotifiableTimePickerDialog

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