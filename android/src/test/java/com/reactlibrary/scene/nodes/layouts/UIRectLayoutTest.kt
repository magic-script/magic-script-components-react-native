package com.reactlibrary.scene.nodes.layouts

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.math.Vector3
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.scene.nodes.base.UiLayout
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.layouts.manager.RectLayoutManager
import com.reactlibrary.scene.nodes.props.Alignment
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.utils.Vector2
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.math.exp

@RunWith(RobolectricTestRunner::class)
class UIRectLayoutTest {

    private lateinit var rectLayoutManager: RectLayoutManager

    @Before
    fun setUp() {
        rectLayoutManager = mock()
    }

    @Test
    fun shoulSetCenterCenterAlignmentWhenNoAlignmentIsPassed() {
        val props = JavaOnlyMap()
        val node = UiRectLayout(props, rectLayoutManager)
        node.build()

        verify(rectLayoutManager).contentHorizontalAlignment = Alignment.HorizontalAlignment.CENTER
        verify(rectLayoutManager).contentVerticalAlignment = Alignment.VerticalAlignment.CENTER
    }

    @Test
    fun shouldSetCorrespondingAlignmentWhenContentAlignmentIsPassed() {
        val props = JavaOnlyMap.of(UiRectLayout.PROP_CONTENT_ALIGNMENT, "bottom-left")
        val node = UiRectLayout(props, rectLayoutManager)
        node.build()

        verify(rectLayoutManager).contentHorizontalAlignment = Alignment.HorizontalAlignment.LEFT
        verify(rectLayoutManager).contentVerticalAlignment = Alignment.VerticalAlignment.BOTTOM
    }

    @Test
    fun shouldReturnCorrectBoundingAndSetParentBoundingInManagerWhenSizeIsSpecified() {
        val props = JavaOnlyMap.of(UiLayout.PROP_WIDTH, 2.0, UiLayout.PROP_HEIGHT, 1.0)
        val node = UiRectLayout(props, rectLayoutManager)
        val expectedBounds = Bounding(-1F, -0.5F, 1F, 0.5F)
        node.build()

        val bounds = node.getBounding()

        assertTrue(Bounding.equalInexact(expectedBounds, bounds))
        verify(rectLayoutManager).parentBounds = expectedBounds
    }

    @Test
    fun shouldRescaleChildIfChildHasBiggerSizeThanMax() {
        val props = JavaOnlyMap.of(UiLayout.PROP_WIDTH, 1.0, UiLayout.PROP_HEIGHT, 1.0)
        val node = UiRectLayout(props, rectLayoutManager)
        val context: Context = ApplicationProvider.getApplicationContext()
        val viewRenderableLoader: ViewRenderableLoader = mock()
        val childNode = object : UiNode(JavaOnlyMap(), context, viewRenderableLoader) {
            override fun provideView(context: Context): View {
                return mock()
            }

            override fun provideDesiredSize(): Vector2 {
                return Vector2(2f, 1F)
            }

            override fun getContentBounding(): Bounding {
                return Bounding(0f, 0f, 2f, 1f)
            }
        }
        childNode.build()
        node.contentNode.addChild(childNode)
        node.addContent(childNode)
        node.build()

        assertEquals(Vector3(0.5f, 0.5f, 1f), childNode.localScale)
    }
}