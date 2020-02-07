package com.magicleap.magicscript.plane

import com.facebook.react.bridge.WritableMap

interface OnPlanesUpdated {
    fun invoke(payload: WritableMap)
}