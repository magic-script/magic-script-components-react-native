package com.reactlibrary.utils

import android.graphics.Color
import org.amshove.kluent.shouldEqual
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExtensionsKtTest {

    @Test
    fun `should return array of double colors R,G,B,A`() {
        val color = Color.parseColor("#BF9CACBC")

        color.toJsColorArray().run {
            size shouldEqual 4
            get(0) shouldEqual Color.red(color).toDouble() / 255
            get(1) shouldEqual Color.green(color).toDouble() / 255
            get(2) shouldEqual Color.blue(color).toDouble() / 255
            get(3) shouldEqual Color.alpha(color).toDouble() / 255
        }
    }
}
