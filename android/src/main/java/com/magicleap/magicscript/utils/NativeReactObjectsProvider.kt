package com.magicleap.magicscript.utils

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap

class NativeReactObjectsProvider: ReactObjectsProvider {
    override fun createMap(): WritableMap {
        return Arguments.createMap()
    }

    override fun createArray(): WritableArray {
        return Arguments.createArray()
    }

    companion object {
        val INSTANCE = NativeReactObjectsProvider()
    }
}