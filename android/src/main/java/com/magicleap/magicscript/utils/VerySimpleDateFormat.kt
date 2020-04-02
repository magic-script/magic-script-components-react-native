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
package com.magicleap.magicscript.utils

import java.text.SimpleDateFormat
import java.util.*

class VerySimpleDateFormat(
    val pattern: String?,
    locale: Locale,
    val is24h: Boolean = is24hours(pattern)
) : SimpleDateFormat(pattern.simplify(), locale) {

    fun format(year: Int, month: Int, day: Int): String =
        format(
            Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, day)
            }.time
        )

    fun format(hourOfDay: Int, minute: Int): String = format(
        Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }.time
    )
}

private fun String?.simplify(): String? =
    this?.replace("HH", if (is24hours(this)) "HH" else "hh", false)
        ?.replace("D", "d", false)
        ?.replace("p", "a")
        ?.replace("SS", "ss", false)
        ?.replace("MM", if (containsAny("H", "K", "s")) "mm" else "MM", false)

private fun is24hours(pattern: String?) = pattern?.containsAmPmSign() != true

private fun String?.containsAmPmSign() = this?.contains("p") ?: false

private fun String.containsAny(vararg words: String): Boolean {
    words.forEach {
        if (contains(it)) {
            return true
        }
    }
    return false
}
