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
import com.magicleap.magicscript.layoutUntilStableBounds
import com.magicleap.magicscript.scene.nodes.ContentNode
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.layouts.params.PageViewLayoutParams
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Alignment
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

    private lateinit var childrenList: List<ContentNode>
    private val childrenBounds = mutableMapOf<TransformNode, AABB>()
    private lateinit var itemsPadding: Map<TransformNode, Padding>
    private lateinit var itemsAlignment: Map<TransformNode, Alignment>
    private var visiblePage = 0

    @Before
    fun setUp() {
        pageViewManager = PageViewLayoutManager()
        childrenList = listOf(
            ContentNode(JavaOnlyMap()),
            ContentNode(JavaOnlyMap())
        )

        itemsPadding = mapOf(
            childrenList[0] to Padding(),
            childrenList[1] to Padding()
        )

        itemsAlignment = mapOf(
            childrenList[0] to Alignment(Alignment.Vertical.TOP, Alignment.Horizontal.LEFT),
            childrenList[1] to Alignment(Alignment.Vertical.TOP, Alignment.Horizontal.LEFT)
        )
    }

    @Test
    fun `should hide all children but the first when visible page is 0`() {
        visiblePage = 0

        pageViewManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        childrenList[0].isVisible shouldEqual true
        childrenList[1].isVisible shouldEqual false
    }

    private fun getLayoutParams() =
        PageViewLayoutParams(
            visiblePage = 0,
            size = Vector2(1f, 1f),
            itemsAlignment = itemsAlignment,
            itemsPadding = itemsPadding
        )
}