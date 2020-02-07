package com.magicleap.magicscript.plane

import com.facebook.react.bridge.WritableMap

interface OnPlanesAdded {
    fun invoke(payload: WritableMap)
}