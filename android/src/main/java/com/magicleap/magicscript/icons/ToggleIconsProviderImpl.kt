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

import com.magicleap.magicscript.R
import com.magicleap.magicscript.scene.nodes.toggle.UiToggleNode

class ToggleIconsProviderImpl : ToggleIconsProvider {

    override fun provideIconId(toggleType: String, checked: Boolean): Int {
        return when (toggleType) {
            UiToggleNode.TYPE_RADIO -> {
                if (checked) R.drawable.radio_on else R.drawable.radio_off
            }
            UiToggleNode.TYPE_CHECKBOX -> {
                if (checked) R.drawable.checkbox_on else R.drawable.checkbox_off
            }
            else -> {
                if (checked) R.drawable.switch_on else R.drawable.switch_off
            }
        }
    }
}