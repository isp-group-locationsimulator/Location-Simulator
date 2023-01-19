package com.ispgr5.locationsimulator.presentation.run

import android.media.MediaPlayer

/**
 * This class provides methods to manage the MediaPlayer and play audio with it.
 */
class SoundPlayer {

    /**
     * This function starts a audio file.
     * @param uriAsString This is the Uri as a String to the Audio-File that should be played.
     */
    fun startSound(uriAsString : String) {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener {
            @Override
            fun onCompletion(mediaPlayer: MediaPlayer) {
                println("Sound off")
                mediaPlayer.release();
            }
        }
        mediaPlayer.setDataSource(uriAsString)
        mediaPlayer.prepare()
        mediaPlayer.start()
        // startImport()

        // TODO: "MediaPlayer finalized without being released"
    }
}