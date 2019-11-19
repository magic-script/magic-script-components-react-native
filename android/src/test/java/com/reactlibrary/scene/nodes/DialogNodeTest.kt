package com.reactlibrary.scene.nodes

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.reactlibrary.icons.IconsRepository
import com.reactlibrary.scene.nodes.views.CustomAlertDialogBuilder
import com.reactlibrary.utils.DialogProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DialogNodeTest {

    private lateinit var iconsRepository: IconsRepository
    private lateinit var testDialogProvider: DialogProvider
    private lateinit var context: Context

    @Before
    fun setUp() {
        iconsRepository = mock()
        testDialogProvider = mock()

        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `should set confirm icon and text if props are passed`() {
        val props = JavaOnlyMap.of(
                DialogNode.PROP_CONFIRM_ICON, "icon",
                DialogNode.PROP_CONFIRM_TEXT, "text"
        )
        val node = DialogNode(props, context, iconsRepository, testDialogProvider)
        val mockDialogBuilder: CustomAlertDialogBuilder = mock()
        val testDrawable = mock<Drawable>()
        whenever(testDialogProvider.provideCustomAlertDialogBuilder(any())).thenReturn(mockDialogBuilder)
        whenever(iconsRepository.getIcon("icon", false)).thenReturn(testDrawable)

        node.build()

        verify(mockDialogBuilder).setConfirmationText("text")
        verify(iconsRepository).getIcon("icon", false)
        verify(mockDialogBuilder).setConfirmationIcon(testDrawable)
    }

    @Test
    fun `should set cancel icon, text and listener if props are passed`() {
        val props = JavaOnlyMap.of(
                DialogNode.PROP_CANCEL_ICON, "icon",
                DialogNode.PROP_CANCEL_TEXT, "text"
        )
        val node = DialogNode(props, context, iconsRepository, testDialogProvider)
        val mockDialogBuilder: CustomAlertDialogBuilder = mock()
        val testDrawable = mock<Drawable>()
        whenever(testDialogProvider.provideCustomAlertDialogBuilder(any())).thenReturn(mockDialogBuilder)
        whenever(iconsRepository.getIcon("icon", false)).thenReturn(testDrawable)

        node.build()

        verify(mockDialogBuilder).setCancelText("text")
        verify(iconsRepository).getIcon("icon", false)
        verify(mockDialogBuilder).setCancelIcon(testDrawable)
    }

    @Test
    fun `should set title and message if props are passed`() {
        val props = JavaOnlyMap.of(
                DialogNode.PROP_TITLE, "title",
                DialogNode.PROP_TEXT, "text"
        )
        val node = DialogNode(props, context, iconsRepository, testDialogProvider)
        val mockDialogBuilder: CustomAlertDialogBuilder = mock()
        whenever(testDialogProvider.provideCustomAlertDialogBuilder(any())).thenReturn(mockDialogBuilder)

        node.build()

        verify(mockDialogBuilder).setTitle("title")
        verify(mockDialogBuilder).setDescription("text")
    }
}