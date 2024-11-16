package com.ispgr5.locationsimulator.presentation.run

import android.media.MediaPlayer

/**
 * This class provides methods to manage the MediaPlayer and play audio with it.
 */
class SoundPlayer (private val onSoundFinish: () -> Unit) {
	private val mediaPlayer: MediaPlayer = MediaPlayer()

	/**
	 * when the MediaPlayer is created, the functions are set that should be called when the sound
	 * that is played is finished or an error occurs
	 */
	init {
		mediaPlayer.setOnErrorListener { mediaPlayer, _, _ ->
			mediaPlayer.reset()
			true
		}
		mediaPlayer.setOnCompletionListener {
			onSoundFinish()
		}
	}

	/**
	 * This function starts an audio file.
	 * @param uriAsString This is the Uri as a String to the Audio-File that should be played.
	 * @param volume The Volume the sound should be played at.
	 * The value should be in the range of 0 to 1, with 1 being the full volume and 0 being no volume.
	 * @return We return the length of the file in ms, as we don't save it in the configuration.
	 */
	fun startSound(uriAsString: String, volume: Float): Int {
		mediaPlayer.reset()
		mediaPlayer.setDataSource(uriAsString)
		mediaPlayer.setVolume(volume, volume)
		mediaPlayer.prepare()
		mediaPlayer.start()
		return mediaPlayer.duration
		// TODO: "MediaPlayer finalized without being released"
	}

	/**
	 * stops the currently running audio file
	 */
	fun stopPlayback() {
		if (mediaPlayer.isPlaying) {
			mediaPlayer.stop()
		}
	}
}