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

package com.magicleap.magicscript.icons

import android.content.Context
import android.graphics.drawable.Drawable

/**
 * Assets icons provider
 */
class ExternalIconsProvider(private val context: Context) : IconsProvider {

    companion object {
        private const val ICONS_DIR = "lumin_system_icons"
    }

    /**
     * Returns an icon from assets or null if not exists
     */
    override fun provideIcon(name: String): Drawable? {
        val iconsCatalogExists = context.assets.list("")?.contains(ICONS_DIR) ?: false
        if (!iconsCatalogExists) {
            return null
        }
        val assetsIconName = name.split("-").joinToString(separator = "", transform = {
            it.capitalize()
        }) + ".png"

        val iconExists = context.assets.list(ICONS_DIR)?.contains(assetsIconName) ?: false
        if (!iconExists) {
            return null
        }
        val iconPath = "$ICONS_DIR/$assetsIconName"
        return Drawable.createFromStream(context.assets.open(iconPath), null)
    }

}