package com.reactlibrary.scene.nodes.layouts

import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.scene.nodes.base.TransformNode

// Base class for layouts (grid, linear , rect)
abstract class UiLayout(props: ReadableMap) : TransformNode(props)