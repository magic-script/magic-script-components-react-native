package com.magicleap.magicscript

import com.magicleap.magicscript.scene.NodesManager
import com.magicleap.magicscript.scene.nodes.base.UiNode
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
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
    private lateinit var uiNode: UiNode

    private val uiNodeId = "id"

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        eventsManager = ReactEventsManager(mock(), nodesManager)
        whenever(nodesManager.findNodeWithId(uiNodeId)).thenReturn(uiNode)
    }

    @Test
    fun `should register on click listener`() {
        eventsManager.addOnActivateEventHandler(uiNodeId)

        verify(uiNode).onActivateListener = any()
    }

    @Test
    fun `should register on press listener`() {
        eventsManager.addOnPressEventHandler(uiNodeId)

        verify(uiNode).onPressListener = any()
    }

    @Test
    fun `should register on long press listener`() {
        eventsManager.addOnLongPressEventHandler(uiNodeId)

        verify(uiNode).onLongPressListener = any()
    }

    @Test
    fun `should register on release listener`() {
        eventsManager.addOnReleaseEventHandler(uiNodeId)

        verify(uiNode).onReleaseListener = any()
    }

    @Test
    fun `should register on focus gained listener`() {
        eventsManager.addOnFocusGainedEventHandler(uiNodeId)

        verify(uiNode).onFocusGainedListener = any()
    }

    @Test
    fun `should register on focus lost listener`() {
        eventsManager.addOnFocusLostEventHandler(uiNodeId)

        verify(uiNode).onFocusLostListener = any()
    }

    @Test
    fun `should register on update listener`() {
        eventsManager.addOnUpdateEventHandler(uiNodeId)

        verify(uiNode).onUpdatedListener = any()
    }

    @Test
    fun `should register on delete listener`() {
        eventsManager.addOnDeleteEventHandler(uiNodeId)

        verify(uiNode).onDeletedListener = any()
    }

}