package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.view.View
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.magicleap.magicscript.scene.nodes.UIWebViewNode.Companion.ACTION_BACK
import com.magicleap.magicscript.scene.nodes.UIWebViewNode.Companion.ACTION_FORWARD
import com.magicleap.magicscript.scene.nodes.UIWebViewNode.Companion.ACTION_RELOAD
import com.magicleap.magicscript.scene.nodes.UIWebViewNode.Companion.PROP_ACTION
import com.magicleap.magicscript.scene.nodes.UIWebViewNode.Companion.PROP_HEIGHT
import com.magicleap.magicscript.scene.nodes.UIWebViewNode.Companion.PROP_SCROLL_BY
import com.magicleap.magicscript.scene.nodes.UIWebViewNode.Companion.PROP_URL
import com.magicleap.magicscript.scene.nodes.UIWebViewNode.Companion.PROP_WIDTH
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UIWebViewNodeTest {

    lateinit var containerSpy: WebView
    lateinit var tested: UIWebViewNode
    lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        containerSpy = spy(WebView(context))
        tested = createNodeWithViewSpy()
    }

    @Test
    fun `should setup default properties`() {
        tested.getProperty(PROP_WIDTH) shouldEqual 1.0
        tested.getProperty(PROP_HEIGHT) shouldEqual 1.0
    }

    @Test
    fun `should override default properties`() {
        val testedHeight = 2.0
        val testedWith = 3.0
        val props = JavaOnlyMap.of(PROP_HEIGHT, testedHeight, PROP_WIDTH, testedWith)

        tested = createNodeWithViewSpy(props)

        tested.getProperty(PROP_HEIGHT) shouldEqual testedHeight
        tested.getProperty(PROP_WIDTH) shouldEqual testedWith
    }

    @Test
    fun `should set url`() {
        val url = "https://asasd.dsa"
        val props = JavaOnlyMap.of(PROP_URL, url)

        tested = createNodeWithViewSpy(props)
        tested.build()

        verify(containerSpy).loadUrl(eq(url))
    }

    @Test
    fun `should reload page`() {
        val props = JavaOnlyMap.of(PROP_ACTION, ACTION_RELOAD)

        tested = createNodeWithViewSpy(props)
        tested.build()

        verify(containerSpy).reload()
    }

    @Test
    fun `should forward page`() {
        val props = JavaOnlyMap.of(PROP_ACTION, ACTION_FORWARD)

        tested = createNodeWithViewSpy(props)
        tested.build()

        verify(containerSpy).goForward()
    }

    @Test
    fun `should go back on the page`() {
        val props = JavaOnlyMap.of(PROP_ACTION, ACTION_BACK)

        tested = createNodeWithViewSpy(props)
        tested.build()

        verify(containerSpy).goBack()
    }

    @Test
    fun `should scroll by`() {
        val scrollBy = 15.0
        val props = JavaOnlyMap.of(PROP_SCROLL_BY, scrollBy)

        tested = createNodeWithViewSpy(props)
        tested.build()

        verify(containerSpy).scrollBy(eq(0), eq(scrollBy.toInt()    ))
    }


    fun createNodeWithViewSpy(props: ReadableMap = JavaOnlyMap()): UIWebViewNode {
        return object : UIWebViewNode(props, context, mock()) {
            override fun provideView(context: Context): View {
                return containerSpy
            }
        }
    }

}