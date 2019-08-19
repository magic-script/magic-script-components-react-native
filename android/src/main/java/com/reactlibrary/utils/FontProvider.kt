package com.reactlibrary.utils

import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat

/**
 * Typeface returned from this provider should be used by every
 * node that contains a text
 */
class FontProvider {
    companion object {

        private const val LOMINO_FONT_NAME = "lomino_font" // stored optionally in res/font

        fun provideFont(context: Context): Typeface {
            val lominoFontId: Int = context.resources
                    .getIdentifier(LOMINO_FONT_NAME, "font", context.packageName)

            if (lominoFontId != 0) {
                logMessage("using Lomino font")
                return ResourcesCompat.getFont(context, lominoFontId) ?: Typeface.DEFAULT
            }

            return Typeface.DEFAULT
        }
    }

}