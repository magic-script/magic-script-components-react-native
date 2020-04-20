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
import android.view.View
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

        val hasCorrectId = fragment.arguments?.getString(KEY_FRAGMENT_ID) == fragmentId
        val hasCorrectParent = fragment.view?.parent == this

        // Fragment already exists, but has incorrect id (it was recreated by Android Framework):
        // when using React's reload option or when app config changes, the fragment
        // may be duplicated, because new instance is created by [ViewGroupManager.createViewInstance]
        // and also the old instance is automatically re-created by the Android framework.
        // So we have to get rid of the auto created fragment and create a new with correct id.
        if (!hasCorrectId) {
            // here only commit() works
            fragmentManager.beginTransaction().remove(fragment).commit()
            // since we use commit to remove, we have to add new fragment on next thread frame
            post {
                addFragment(CustomArFragment(), tag)
                // since we wait until next frame, we have to layout view manually
                layoutFragmentView()
            }
            return
        }

        // Fragment already exists, but has incorrect parent
        if (!hasCorrectParent) {
            // set correct parent
            fragmentManager.beginTransaction().remove(fragment).commit()
            post {
                addFragment(fragment, tag)
                // since we wait until next frame, we have to layout view manually
                layoutFragmentView()
            }
        }

    }

    fun dropFragment(tag: String) {
        logMessage("dropFragment")
        val fragmentManager = this.fragmentManager
        val arFragment = fragmentManager.findFragmentByTag(tag)
        if (arFragment != null) {
            // Here we should use commit(), because commitNow() may return internal
            // ARCore exception, like SessionPausedException (it's possible the ARCore bug)
            fragmentManager.beginTransaction().remove(arFragment).commit()
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
    }

    private fun layoutFragmentView() {
        val child: View = getChildAt(0)
        child.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
        child.layout(0, 0, child.measuredWidth, child.measuredHeight)
    }

    companion object {
        private const val KEY_FRAGMENT_ID = "fragmentId"
    }

}