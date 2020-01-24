/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.magicleap.magicscript.scene.nodes.layouts.manager

import com.facebook.react.bridge.JavaOnlyMap
import com.magicleap.magicscript.scene.nodes.ContentNode
import com.magicleap.magicscript.scene.nodes.layouts.params.PageViewLayoutParams
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Vector2
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PageViewManagerTest {

    private lateinit var pageViewManager: PageViewLayoutManager

    private val layoutParams =
        PageViewLayoutParams(
            visiblePage = 0,
            size = Vector2(1f, 1f),
            itemsAlignment = mapOf(
                Pair(
                    0,
                    Alignment(Alignment.VerticalAlignment.TOP, Alignment.HorizontalAlignment.LEFT)
                ),
                Pair(
                    1,
                    Alignment(Alignment.VerticalAlignment.TOP, Alignment.HorizontalAlignment.LEFT)
                )
            ),
            itemsPadding = mapOf(
                Pair(0, Padding()),
                Pair(1, Padding())
            )
        )

    @Before
    fun setUp() {
        this.pageViewManager = PageViewLayoutManager()
    }

    @Test
    fun `should hide all children but the first`() {
        val child1 = ContentNode(JavaOnlyMap())
        val child2 = ContentNode(JavaOnlyMap())
        val childrenList = listOf(child1, child2)
        val boundsMap = mapOf(
            0 to Bounding(), 1 to Bounding()
        )

        pageViewManager.layoutChildren(layoutParams, childrenList, boundsMap)

        child1.isVisible shouldEqual true
        child2.isVisible shouldEqual false
    }
}