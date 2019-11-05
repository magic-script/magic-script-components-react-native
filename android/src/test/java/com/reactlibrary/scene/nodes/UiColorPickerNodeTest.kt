package com.reactlibrary.scene.nodes

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.nhaarman.mockitokotlin2.*
import com.reactlibrary.scene.nodes.UiColorPickerNode.Companion.PROP_COLOR
import com.reactlibrary.scene.nodes.UiColorPickerNode.Companion.PROP_HEIGHT
import com.reactlibrary.scene.nodes.views.ColorPickerDialog
import kotlinx.android.synthetic.main.color_picker.view.*
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UiColorPickerNodeTest {


    lateinit var containerSpy: View
    lateinit var tested: UiColorPickerNode
    lateinit var context: Context
    val selectedColorFrame = mock<FrameLayout>()
    val onColorSelected: ((color: Array<Double>) -> Unit)? = mock()
    lateinit var colorPickerDialog: ColorPickerDialog

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        containerSpy = spy(FrameLayout(context))
        colorPickerDialog = ColorPickerDialog(context)
        tested = createNodeWithViewSpy()

        whenever(containerSpy.selectedColor).thenReturn(selectedColorFrame)
    }

    @Test
    fun `should setup default properties`() {
        tested.getProperty(PROP_HEIGHT) shouldEqual 1.0
        tested.getProperty(PROP_COLOR) shouldEqual "[1.0, 1.0, 1.0, 1.0]"
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
        colorPickerDialog.onResult.shouldNotBeNull()
    }

    @Test
    fun `should update selectedColorFrameLayout background`() {
        val color = Color.DKGRAY

        colorPickerDialog.onResult?.invoke(color)

        verify(selectedColorFrame).setBackgroundColor(eq(color))
    }


    fun createNodeWithViewSpy(
        props: ReadableMap = JavaOnlyMap()
    ): UiColorPickerNode {
        return object : UiColorPickerNode(props, context, mock(), colorPickerDialog) {
            override fun provideView(context: Context): View {
                return containerSpy
            }
        }.apply {
            onColorSelected = this@UiColorPickerNodeTest.onColorSelected
            build()
        }
    }
}