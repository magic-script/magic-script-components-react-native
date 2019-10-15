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

package com.reactlibrary.scene

import android.content.Context
import android.widget.LinearLayout

class ViewWrapper(context: Context) : LinearLayout(context) {
    init {
        layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        )
    }

    // Workaround for https://github.com/magic-script/magic-script-components-react-native/issues/7
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val width = if (widthMode == MeasureSpec.AT_MOST) {
            MeasureSpec.UNSPECIFIED
        } else {
            widthMeasureSpec
        }

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val height = if (heightMode == MeasureSpec.AT_MOST) {
            MeasureSpec.UNSPECIFIED
        } else {
            heightMeasureSpec
        }

        super.onMeasure(width, height)
    }
}
