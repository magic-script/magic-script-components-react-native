package com.magicleap.magicscript.utils

import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap

interface ReactObjectsProvider {
    fun createMap(): WritableMap
    fun createArray(): WritableArray
}