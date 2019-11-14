package com.reactlibrary.converter

import com.google.ar.sceneform.math.Vector3
import com.google.gson.GsonBuilder
import com.reactlibrary.scene.nodes.audio.model.SpatialSoundPosition

object GsonProvider {
    val gson = GsonBuilder().apply {
        registerTypeAdapter(Vector3::class.java, Vector3Converter())
        registerTypeAdapter(SpatialSoundPosition::class.java, SpatialSoundPositionConverter())
    }.create()
}