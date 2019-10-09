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

package com.reactlibrary.icons

import android.content.Context
import android.graphics.drawable.Drawable
import java.util.*

class IconsProviderImpl(private val context: Context,
                        private val defaultIconsProvider: DefaultIconsProvider) : IconsProvider {

    companion object {
        private const val ICONS_DIR = "lumin_system_icons"
    }

    /**
     * Returns an icon for a given name or null if not found.
     *
     * If an external icon (.png) is present in assets this function returns it,
     * else it returns a default icon.
     *
     * @return icon or null (if both external and default icon was not found)
     */
    override fun provideIcon(name: String): Drawable? {
        val externalIcon = getExternalIcon(name)
        if (externalIcon != null) {
            return externalIcon
        }
        return defaultIconsProvider.provideIcon(name)
    }

    private fun getExternalIcon(name: String): Drawable? {
        val iconsCatalogExists = context.assets.list("")?.contains(ICONS_DIR) ?: false
        if (!iconsCatalogExists) {
            return null
        }

        val assetsIconName = name.split("-").joinToString { it.toUpperCase(Locale.US) }
        val iconExists = context.assets.list(ICONS_DIR)?.contains(assetsIconName) ?: false
        if (!iconExists) {
            return null
        }

        val iconPath = "$ICONS_DIR/$assetsIconName.png"
        return Drawable.createFromStream(context.assets.open(iconPath), null)
    }

}