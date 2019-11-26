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

package com.magicleap.magicscript.utils

import android.os.Bundle
import com.magicleap.magicscript.font.FontParams
import com.magicleap.magicscript.font.FontStyle
import com.magicleap.magicscript.font.FontWeight

class FontParamsReader {
    companion object {
        const val PROP_WEIGHT = "weight"
        const val PROP_STYLE = "style"
        const val PROP_ALL_CAPS = "allCaps"

        fun readFontParams(props: Bundle, paramName: String): FontParams? {
            val paramsBundle = props.getBundle(paramName) ?: return null
            val weightName = paramsBundle.getString(PROP_WEIGHT, "")
            val styleName = paramsBundle.getString(PROP_STYLE, "")
            val allCaps = paramsBundle.getBoolean(PROP_ALL_CAPS, false)

            val weight = FontWeight.fromName(weightName) ?: FontWeight.DEFAULT
            val style = FontStyle.fromName(styleName) ?: FontStyle.DEFAULT

            return FontParams(weight, style, allCaps)
        }
    }
}