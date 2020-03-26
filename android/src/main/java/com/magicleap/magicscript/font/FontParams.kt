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

import android.os.Bundle
import com.magicleap.magicscript.utils.read

data class FontParams(
    val weight: FontWeight?,
    val style: FontStyle?,
    val allCaps: Boolean?,
    val fontSize: Double?, // same as textSize
    val tracking: Double? // same as char spacing
) {
    companion object {

        private const val PROP_FONT_PARAMS = "fontParameters"
        private const val PROP_WEIGHT = "weight"
        private const val PROP_STYLE = "style"
        private const val PROP_ALL_CAPS = "allCaps"
        private const val PROP_FONT_SIZE = "fontSize"
        private const val PROP_TRACKING = "tracking"

        fun fromBundle(bundle: Bundle): FontParams {
            val fontParamsBundle = bundle.getBundle(PROP_FONT_PARAMS)

            val weightName = fontParamsBundle?.read(PROP_WEIGHT)
                ?: bundle.read(PROP_WEIGHT) ?: ""

            val styleName = fontParamsBundle?.read(PROP_STYLE)
                ?: bundle.read(PROP_STYLE) ?: ""

            val allCaps = fontParamsBundle?.read(PROP_ALL_CAPS)
                ?: bundle.read<Boolean>(PROP_ALL_CAPS)

            val fontSize = fontParamsBundle?.read<Double>(PROP_FONT_SIZE)

            val tracking = fontParamsBundle?.read(PROP_TRACKING)
                ?: bundle.read<Double>(PROP_TRACKING)

            val weight = FontWeight.fromName(weightName)
            val style = FontStyle.fromName(styleName)

            return FontParams(weight, style, allCaps, fontSize, tracking)
        }

    }

}