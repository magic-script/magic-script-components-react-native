package com.magicleap.magicscript

import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.magicleap.magicscript.utils.ReactObjectsProvider

class TestReactObjectsProvider: ReactObjectsProvider {
    override fun createMap(): WritableMap {
        return JavaOnlyMap()
    }

    override fun createArray(): WritableArray {
        return JavaOnlyArray()
    }
}