package com.reactlibrary.scene.nodes.layouts

import com.facebook.react.bridge.ReadableMap

class UiGridLayout(props: ReadableMap) : UiLayout(props) {

    override fun loadRenderable(): Boolean {
        // it does not contain its own renderable
        return false
    }

}