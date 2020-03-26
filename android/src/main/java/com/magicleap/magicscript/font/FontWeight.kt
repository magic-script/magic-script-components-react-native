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

package com.magicleap.magicscript.font

enum class FontWeight {
    EXTRA_LIGHT,
    LIGHT,
    REGULAR,
    MEDIUM,
    BOLD,
    EXTRA_BOLD;

    companion object {
        val DEFAULT = REGULAR

        fun fromName(string: String): FontWeight? {
            return when (string) {
                "extra-light" -> EXTRA_LIGHT
                "light" -> LIGHT
                "regular" -> REGULAR
                "medium" -> MEDIUM
                "bold" -> BOLD
                "extra-bold" -> EXTRA_BOLD
                else -> null
            }
        }
    }

}