package com.magicleap.magicscript.plane

import com.facebook.react.bridge.WritableMap

interface OnPlanesRemoved {
    fun invoke(payload: WritableMap)
}