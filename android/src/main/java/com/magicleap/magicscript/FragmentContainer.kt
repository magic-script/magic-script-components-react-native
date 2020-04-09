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
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var fragmentManager: FragmentManager? = null

    fun setupFragment(fragment: Fragment, tag: String, fragmentManager: FragmentManager) {
        this.fragmentManager = fragmentManager
        fragmentManager.beginTransaction().add(fragment, tag).commitNow()
        addView(fragment.view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    fun removeFragment(tag: String) {
        val fragmentManager = this.fragmentManager ?: return
        val arFragment = fragmentManager.findFragmentByTag(tag)
        if (arFragment != null) {
            fragmentManager.beginTransaction().remove(arFragment).commitNowAllowingStateLoss()
        }
    }

}