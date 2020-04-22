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

import com.magicleap.magicscript.scene.nodes.prism.AppInfoProvider
import com.magicleap.magicscript.utils.Vector2

class TestAppInfoProvider() : AppInfoProvider {

    override fun getAppName(): String {
        return "Test app"
    }

    override fun getPackageName(): String {
        return "com.magicleap.magicscript"
    }

    override fun getScreenSizePx(): Vector2 {
        return Vector2(800f, 1280f)
    }

    override fun getScreenDpi(): Vector2 {
        return Vector2(320f, 320f)
    }
}