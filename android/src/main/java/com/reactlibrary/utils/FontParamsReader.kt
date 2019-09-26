package com.reactlibrary.utils

import android.os.Bundle
import com.reactlibrary.font.FontParams
import com.reactlibrary.font.FontStyle
import com.reactlibrary.font.FontWeight

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

            val weight = FontWeight.fromName(weightName)
            val style = FontStyle.fromName(styleName)

            return FontParams(weight, style, allCaps)
        }
    }
}