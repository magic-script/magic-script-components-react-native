package com.magicleap.magicscript.plane

import com.facebook.react.bridge.WritableMap

interface OnPlaneTapped {
    fun invoke(payload: WritableMap)
}