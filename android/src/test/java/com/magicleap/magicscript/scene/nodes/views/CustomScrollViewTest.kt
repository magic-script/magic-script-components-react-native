/*
 * Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

package com.magicleap.magicscript.scene.nodes.views

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.magicleap.magicscript.R
import com.magicleap.magicscript.scene.nodes.props.ScrollBarVisibility
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CustomScrollViewTest {

    private lateinit var context: Context
    private lateinit var scrollView: CustomScrollView

    private lateinit var verticalBar: CustomScrollBar
    private lateinit var horizontalBar: CustomScrollBar

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.scrollView = CustomScrollView(context)

        val verticalAttrsBuilder = Robolectric.buildAttributeSet()
        verticalAttrsBuilder.addAttribute(R.attr.orientation, "vertical")

        val horizontalAttrsBuilder = Robolectric.buildAttributeSet()
        horizontalAttrsBuilder.addAttribute(R.attr.orientation, "horizontal")

        this.verticalBar = CustomScrollBar(context, verticalAttrsBuilder.build())
        this.horizontalBar = CustomScrollBar(context, horizontalAttrsBuilder.build())

        scrollView.addView(verticalBar)
        scrollView.addView(horizontalBar)
    }

    @Test
    fun `scrolling should be enabled by default`() {
        scrollView.scrollingEnabled shouldBe true
    }

    @Test
    fun `should hide scroll bars when visibility is off`() {
        scrollView.scrollBarsVisibility = ScrollBarVisibility.OFF

        scrollView.vBar shouldNotBe null
        scrollView.vBar!!.visibility shouldEqual View.INVISIBLE
        scrollView.hBar shouldNotBe null
        scrollView.hBar!!.visibility shouldEqual View.INVISIBLE
    }

    @Test
    fun `should show scroll bars when visibility is set to always`() {
        scrollView.scrollBarsVisibility = ScrollBarVisibility.ALWAYS

        scrollView.vBar shouldNotBe null
        scrollView.vBar!!.visibility shouldEqual View.VISIBLE
        scrollView.hBar shouldNotBe null
        scrollView.hBar!!.visibility shouldEqual View.VISIBLE
    }

}