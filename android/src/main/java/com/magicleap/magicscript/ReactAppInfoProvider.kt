/*
 * Copyright (c) 2020 Magic Leap, Inc. All Rights Reserved
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

package com.magicleap.magicscript

import android.content.Context
import com.magicleap.magicscript.scene.nodes.prism.AppInfoProvider
import com.magicleap.magicscript.utils.Vector2

class ReactAppInfoProvider(private val context: Context) : AppInfoProvider {

    override fun getAppName(): String {
        val appInfo = context.applicationInfo
        val labelId = appInfo.labelRes
        return if (labelId == 0) {
            appInfo.nonLocalizedLabel.toString()
        } else {
            context.getString(labelId)
        }
    }

    override fun getPackageName(): String {
        return context.packageName
    }

    override fun getScreenSizePx(): Vector2 {
        val screenWidthPx = context.resources.displayMetrics.widthPixels
        val screenHeightPx = context.resources.displayMetrics.heightPixels
        return Vector2(screenWidthPx.toFloat(), screenHeightPx.toFloat())
    }

    override fun getScreenDpi(): Vector2 {
        val xDpi = context.resources.displayMetrics.xdpi
        val yDpi = context.resources.displayMetrics.ydpi
        return Vector2(xDpi, yDpi)
    }

}