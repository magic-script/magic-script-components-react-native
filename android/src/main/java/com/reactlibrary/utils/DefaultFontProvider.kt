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

package com.reactlibrary.utils

import android.graphics.Typeface
import com.reactlibrary.font.FontStyle
import com.reactlibrary.font.FontWeight

class DefaultFontProvider {

    companion object {

        private const val FAMILY_EXTRA_LIGHT = "sans-serif-thin"
        private const val FAMILY_LIGHT = "sans-serif-light"
        private const val FAMILY_REGULAR = "sans-serif"
        private const val FAMILY_MEDIUM = "sans-serif-medium" // since api 21
        private const val FAMILY_BOLD = "sans-serif" // applying bold style
        private const val FAMILY_EXTRA_BOLD = "sans-serif-black" // since api 21

        private val fontsCache = hashMapOf<String, Typeface>()

        fun provideFont(weight: FontWeight, style: FontStyle): Typeface {
            val fontFamily = when (weight) {
                FontWeight.EXTRA_LIGHT -> FAMILY_EXTRA_LIGHT
                FontWeight.LIGHT -> FAMILY_LIGHT
                FontWeight.REGULAR -> FAMILY_REGULAR
                FontWeight.MEDIUM -> FAMILY_MEDIUM
                FontWeight.BOLD -> FAMILY_BOLD
                FontWeight.EXTRA_BOLD -> FAMILY_EXTRA_BOLD
            }

            val fontStyleId = when (weight) {
                FontWeight.BOLD -> {
                    if (style == FontStyle.NORMAL) {
                        Typeface.BOLD
                    } else {
                        Typeface.BOLD_ITALIC
                    }
                }
                else -> {
                    if (style == FontStyle.NORMAL) {
                        Typeface.NORMAL
                    } else {
                        Typeface.ITALIC
                    }
                }
            }

            val name: String = fontFamily + fontStyleId
            var typeface = fontsCache[name]
            if (typeface == null) {
                typeface = Typeface.create(fontFamily, fontStyleId)
                fontsCache[name] = typeface
            }
            return typeface!!
        }
    }
}