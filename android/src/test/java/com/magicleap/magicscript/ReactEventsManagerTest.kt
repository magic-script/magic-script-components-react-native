package com.magicleap.magicscript

import com.facebook.react.bridge.JavaOnlyMap
import com.magicleap.magicscript.scene.NodesManager
import com.magicleap.magicscript.scene.nodes.*
import com.magicleap.magicscript.scene.nodes.base.UiNode
import com.magicleap.magicscript.scene.nodes.dropdown.UiDropdownListNode
import com.magicleap.magicscript.scene.nodes.toggle.UiToggleNode
import com.magicleap.magicscript.scene.nodes.video.VideoNode
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ReactEventsManagerTest {

    private lateinit var eventsManager: ReactEventsManager

    @Mock
    private lateinit var nodesManager: NodesManager

    @Mock
    private lateinit var eventsEmitter: EventsEmitter

    private lateinit var uiNode: UiNode
    private val uiNodeId = "id"

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        eventsManager = ReactEventsManager(eventsEmitter, nodesManager)
        uiNode = UiTextEditNode(JavaOnlyMap(), mock(), mock(), mock())
        whenever(nodesManager.findNodeWithId(uiNodeId)).thenReturn(uiNode)
    }

    @Test
    fun `should register on click listener`() {
        eventsManager.addOnActivateEventHandler(uiNodeId)
        uiNode.onClickListener?.invoke()

        verify(eventsEmitter).sendEvent(eq(ReactEventsManager.EVENT_CLICK), any())
        verify(eventsEmitter).sendEvent(eq(ReactEventsManager.EVENT_ACTIVATE), any())
    }

    @Test
    fun `should register on press listener`() {
        eventsManager.addOnPressEventHandler(uiNodeId)
        uiNode.onPressListener?.invoke()

        verify(eventsEmitter).sendEvent(eq(ReactEventsManager.EVENT_PRESS), any())
    }

    @Test
    fun `should register on long press listener`() {
        eventsManager.addOnLongPressEventHandler(uiNodeId)
        uiNode.onLongPressListener?.invoke()

        verify(eventsEmitter).sendEvent(eq(ReactEventsManager.EVENT_LONG_PRESS), any())
    }

    @Test
    fun `should register on release listener`() {
        eventsManager.addOnReleaseEventHandler(uiNodeId)
        uiNode.onReleaseListener?.invoke()

        verify(eventsEmitter).sendEvent(eq(ReactEventsManager.EVENT_RELEASE), any())
    }

    @Test
    fun `should register on focus gained listener`() {
        eventsManager.addOnFocusGainedEventHandler(uiNodeId)
        uiNode.onFocusGainedListener?.invoke()

        verify(eventsEmitter).sendEvent(eq(ReactEventsManager.EVENT_FOCUS_GAINED), any())
    }

    @Test
    fun `should register on focus lost listener`() {
        eventsManager.addOnFocusLostEventHandler(uiNodeId)
        uiNode.onFocusLostListener?.invoke()

        verify(eventsEmitter).sendEvent(eq(ReactEventsManager.EVENT_FOCUS_LOST), any())
    }

    @Test
    fun `should register on updated listener`() {
        eventsManager.addOnUpdateEventHandler(uiNodeId)
        uiNode.onUpdatedListener?.invoke()

        verify(eventsEmitter).sendEvent(eq(ReactEventsManager.EVENT_NODE_UPDATED), any())
    }

    @Test
    fun `should register on deleted listener`() {
        eventsManager.addOnDeleteEventHandler(uiNodeId)
        uiNode.onDeletedListener?.invoke()

        verify(eventsEmitter).sendEvent(eq(ReactEventsManager.EVENT_NODE_DELETED), any())
    }

    @Test
    fun `should register on enabled listener`() {
        eventsManager.addOnEnabledEventHandler(uiNodeId)
        uiNode.onEnabledListener?.invoke()

        verify(eventsEmitter).sendEvent(eq(ReactEventsManager.EVENT_NODE_ENABLED), any())
    }

    @Test
    fun `should register on disabled listener`() {
        eventsManager.addOnDisabledEventHandler(uiNodeId)
        uiNode.onDisabledListener?.invoke()

        verify(eventsEmitter).sendEvent(eq(ReactEventsManager.EVENT_NODE_DISABLED), any())
    }

    @Test
    fun `should register on text changed listener`() {
        val node: UiTextEditNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnTextChangedEventHandler("id")

        verify(node).onTextChangedListener = any()
    }

    @Test
    fun `should register on toggle changed listener`() {
        val node: UiToggleNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnToggleChangedEventHandler("id")

        verify(node).toggleChangedListener = any()
    }

    @Test
    fun `should register on video prepared listener`() {
        val node: VideoNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnVideoPreparedEventHandler("id")

        verify(node).onVideoPreparedListener = any()
    }

    @Test
    fun `should register on slider changed listener`() {
        val node: UiSliderNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnSliderChangedEventHandler("id")

        verify(node).setOnSliderChangedListener(any())
    }

    @Test
    fun `should register on selection changed listener`() {
        val node: UiDropdownListNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnSelectionChangedEventHandler("id")

        verify(node).onSelectionChangedListener = any()
    }

    @Test
    fun `should register on color confirmed listener`() {
        val node: UiColorPickerNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnColorConfirmedEventHandler("id")

        verify(node).onColorConfirmed = any()
    }

    @Test
    fun `should register on color canceled listener`() {
        val node: UiColorPickerNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnColorCanceledEventHandler("id")

        verify(node).onColorCanceled = any()
    }

    @Test
    fun `should register on color changed listener`() {
        val node: UiColorPickerNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnColorChangedEventHandler("id")

        verify(node).onColorChanged = any()
    }

    @Test
    fun `should register on date changed listener`() {
        val node: UiDatePickerNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnDateChangedEventHandler("id")

        verify(node).onDateChanged = any()
    }

    @Test
    fun `should register on date confirmed listener`() {
        val node: UiDatePickerNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnDateConfirmedEventHandler("id")

        verify(node).onDateConfirmed = any()
    }

    @Test
    fun `should register on scroll changed listener`() {
        val node: UiScrollViewNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnScrollChangedEventHandler("id")

        verify(node).onScrollChangeListener = any()
    }

    @Test
    fun `should register on time changed listener`() {
        val node: UiTimePickerNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnTimeChangedEventHandler("id")

        verify(node).onTimeChanged = any()
    }

    @Test
    fun `should register on time confirmed listener`() {
        val node: UiTimePickerNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnTimeConfirmedEventHandler("id")

        verify(node).onTimeConfirmed = any()
    }

    @Test
    fun `should register on dialog confirmed listener`() {
        val node: DialogNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnDialogConfirmedEventHandler("id")

        verify(node).onDialogConfirmListener = any()
    }

    @Test
    fun `should register on dialog canceled listener`() {
        val node: DialogNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnDialogCanceledEventHandler("id")

        verify(node).onDialogCancelListener = any()
    }

    @Test
    fun `should register on dialog expired listener`() {
        val node: DialogNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnDialogTimeExpiredEventHandler("id")

        verify(node).onDialogExpiredListener = any()
    }

    @Test
    fun `should register on confirmation completed listener`() {
        val node: UiCircleConfirmationNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnConfirmationCompletedEventHandler("id")

        verify(node).onConfirmationCompletedListener = any()
    }

    @Test
    fun `should register on confirmation updated listener`() {
        val node: UiCircleConfirmationNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnConfirmationUpdatedEventHandler("id")

        verify(node).onConfirmationUpdatedListener = any()
    }

    @Test
    fun `should register on confirmation canceled listener`() {
        val node: UiCircleConfirmationNode = mock()
        whenever(nodesManager.findNodeWithId("id")).thenReturn(node)
        eventsManager.addOnConfirmationCanceledEventHandler("id")

        verify(node).onConfirmationCanceledListener = any()
    }

}