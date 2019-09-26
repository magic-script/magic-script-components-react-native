package com.reactlibrary.font.external

import com.reactlibrary.font.ExternalFont
import com.reactlibrary.font.FontStyle
import com.reactlibrary.font.FontWeight

class LominoFont : ExternalFont("LominoUIApp") {

    override fun getFileName(fontWeight: FontWeight, fontStyle: FontStyle): String {
        return when (fontWeight) {
            FontWeight.EXTRA_LIGHT,
            FontWeight.LIGHT -> if (fontStyle == FontStyle.NORMAL) {
                getFullName("Light")
            } else {
                getFullName("LightItalic")
            }
            FontWeight.REGULAR -> if (fontStyle == FontStyle.NORMAL) {
                getFullName("Regular")
            } else {
                getFullName("Italic")
            }
            FontWeight.MEDIUM -> if (fontStyle == FontStyle.NORMAL) {
                getFullName("Medium")
            } else {
                getFullName("MediumItalic")
            }
            FontWeight.BOLD -> if (fontStyle == FontStyle.NORMAL) {
                getFullName("Bold")
            } else {
                getFullName("BoldItalic")
            }
            FontWeight.EXTRA_BOLD -> if (fontStyle == FontStyle.NORMAL) {
                getFullName("ExtraBold")
            } else {
                getFullName("ExtraBoldItalic")
            }
        }
    }

    private fun getFullName(suffix: String): String {
        return "$baseName-$suffix.ttf"
    }

}