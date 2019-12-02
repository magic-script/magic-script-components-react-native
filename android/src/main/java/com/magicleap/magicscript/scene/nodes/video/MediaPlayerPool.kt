package com.magicleap.magicscript.scene.nodes.video

import android.media.MediaPlayer

interface MediaPlayerPool {

    fun createMediaPlayer(): MediaPlayer

    fun destroy()

}