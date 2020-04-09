package com.magicleap.magicscript.ar

import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.collision.Ray
import com.google.ar.sceneform.collision.RayHit
import com.google.ar.sceneform.math.Vector3

// Wrapper around com.google.ar.sceneform.collision.Box used to
// disclose useful protected methods.
class BoundingBox(size: Vector3, center: Vector3) : Box(size, center) {

    fun getRayIntersection(ray: Ray, hit: RayHit): Boolean {
        return rayIntersection(ray, hit)
    }

}