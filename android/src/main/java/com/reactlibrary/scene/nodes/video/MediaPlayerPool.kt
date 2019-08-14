package com.reactlibrary.scene.nodes.video

import android.content.Context
import android.media.MediaPlayer
import com.reactlibrary.utils.Utils

object MediaPlayerPool {

    private val mediaPlayers = ArrayList<MediaPlayer?>()

    fun destroy() {
        mediaPlayers.forEachIndexed { index, player ->
            if(player != null) {
                player.release()
                mediaPlayers[index] = null
            }
        }
    }

    fun createMediaPlayer(path: String, context: Context): MediaPlayer {
        val mediaPlayer = MediaPlayer.create(context, Utils.getFilePath(path, context))
        mediaPlayers.add(mediaPlayer)
        return mediaPlayer
    }
}