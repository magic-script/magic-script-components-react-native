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

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.magicleap.magicscript.scene.nodes.UiColorPickerNode.Companion.PROP_COLOR
import com.magicleap.magicscript.scene.nodes.UiColorPickerNode.Companion.PROP_HEIGHT
import com.magicleap.magicscript.scene.nodes.UiColorPickerNode.Companion.PROP_STARTING_COLOR
import com.magicleap.magicscript.scene.nodes.views.ColorPickerDialog
import com.magicleap.magicscript.scene.nodes.views.CustomButton
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UiColorPickerNodeTest {


    lateinit var containerSpy: CustomButton
    lateinit var tested: UiColorPickerNode
    lateinit var context: Context
    lateinit var colorPickerDialog: ColorPickerDialog

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        containerSpy = spy(CustomButton(context))
        colorPickerDialog = ColorPickerDialog(context)
        tested = createNodeWithViewSpy()
    }

    @Test
    fun `should setup default properties`() {
        tested.getProperty(PROP_STARTING_COLOR) shouldEqual "[1.0, 1.0, 1.0, 1.0]"
    }

    @Test
    fun `should override default properties`() {
        val testedHeight = 2.0
        val testedColor = "[0.3, 1.0, 0.3, 1.0]"
        val props = JavaOnlyMap.of(PROP_HEIGHT, testedHeight, PROP_COLOR, testedColor)

        tested = createNodeWithViewSpy(props)

        tested.getProperty(PROP_HEIGHT) shouldEqual testedHeight
        tested.getProperty(PROP_COLOR) shouldEqual testedColor
    }

    @Test
    fun `should attach listeners into dialog`() {
        colorPickerDialog.onConfirm.shouldNotBeNull()
    }

    fun createNodeWithViewSpy(
        props: ReadableMap = JavaOnlyMap()
    ): UiColorPickerNode {
        return object : UiColorPickerNode(
            props, context, mock(), mock(), mock(),
            colorPickerDialog
        ) {
            override fun provideView(context: Context): View {
                return containerSpy
            }
        }.apply {
            onColorConfirmed = mock()
            onColorCanceled = mock()
            onColorChanged = mock()
        }
    }
}