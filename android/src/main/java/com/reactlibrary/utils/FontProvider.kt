/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reactlibrary.utils

import android.content.Context
import android.graphics.Typeface

/**
 * Typeface returned from this provider should be used by every
 * node that contains a text
 */
class FontProvider {

    companion object {

        private const val FONTS_DIR = "fonts"
        private const val LOMINO_FONT_NAME = "LominoUIApp-Regular.ttf"

        // reusing the same instance
        private var font: Typeface = Typeface.DEFAULT

        private var initialized = false

        fun provideFont(context: Context): Typeface {
            if (!initialized) {
                val fontsCatalogExists = context.assets.list("")?.contains(FONTS_DIR) ?: false
                if (fontsCatalogExists) {
                    val fontExists = context.assets.list(FONTS_DIR)?.contains(LOMINO_FONT_NAME)
                            ?: false

                    if (fontExists) {
                        val fontPath = "$FONTS_DIR/$LOMINO_FONT_NAME"
                        font = Typeface.createFromAsset(context.assets, fontPath)
                        logMessage("Lomino font exists")
                    }
                }
                initialized = true
            }
            return font
        }
    }

}