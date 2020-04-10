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
import android.os.Bundle
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.magicleap.magicscript.ar.CustomArFragment
import com.magicleap.magicscript.utils.logMessage
import java.util.*

class ArFragmentContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val fragmentId: String = UUID.randomUUID().toString()

    private lateinit var fragmentManager: FragmentManager

    fun setupFragment(tag: String, fragmentManager: FragmentManager) {
        this.fragmentManager = fragmentManager

        val fragment = fragmentManager.findFragmentByTag(tag) as? CustomArFragment
        if (fragment == null) {
            addFragment(CustomArFragment(), tag)
            return
        }

        removeAllViews()

        // fragment already exists
        val hasCorrectId = fragment.arguments?.getString(KEY_FRAGMENT_ID) == fragmentId
        val hasCorrectParent = fragment.view?.parent == this

        if (!hasCorrectId) {
            logMessage("incorrect id, removing")
            // when using React's reload option the fragment may be duplicated,
            // so we get rid of it and create new
            fragmentManager.beginTransaction().remove(fragment).commitNow()
            addFragment(CustomArFragment(), tag)
            return
        }

        if (!hasCorrectParent) {
            logMessage("incorrect parent, removing")
            // set correct parent
            fragmentManager.beginTransaction().remove(fragment).commitNow()
            addFragment(fragment, tag)
        }

    }

    private fun addFragment(fragment: Fragment, tag: String) {
        if (fragment.arguments == null) {
            fragment.arguments = Bundle().apply { putString(KEY_FRAGMENT_ID, fragmentId) }
        } else {
            fragment.arguments?.putString(KEY_FRAGMENT_ID, fragmentId)
        }

        fragmentManager.beginTransaction().add(fragment, tag).commitNow()
        addView(fragment.view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        logMessage("added fragment, views count = $childCount")

        val fragmentCount = fragmentManager.fragments.filterIsInstance<CustomArFragment>().count()
        logMessage("fragmentCount= $fragmentCount")
    }

    fun removeFragment(tag: String) {
        val fragmentManager = this.fragmentManager ?: return
        val arFragment = fragmentManager.findFragmentByTag(tag)
        if (arFragment != null) {
            fragmentManager.beginTransaction().remove(arFragment).commitNowAllowingStateLoss()
            logMessage("removed fragment CustomArFragment")
        }
    }

    companion object {
        private const val KEY_FRAGMENT_ID = "fragmentId"
    }

}