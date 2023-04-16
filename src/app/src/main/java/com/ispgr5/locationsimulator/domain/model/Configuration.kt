package com.ispgr5.locationsimulator.domain.model

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey



/**
 * The Configuration Entity that the Database stores in the Entry's
 */
@Entity
data class Configuration(
	val name: String,
	val description: String,
	val randomOrderPlayback: Boolean,
	val components: List<ConfigComponent>,
	val isFavorite: Boolean = false,
	@PrimaryKey val id: Int? = null
){
	/**
	 * returns the minimal duration the configuration can take per iteration in seconds
	 */
	fun getMinDuration(context : Context, privateDirUri : String) : Float{
		var sum =0f
		for( configComponent in components){
			when (configComponent) {
				is Sound -> {
					sum += RangeConverter.msToS(configComponent.minPause)
					sum += getAudioFileLength(configComponent,context,privateDirUri)
				}
				is Vibration -> {
					sum += RangeConverter.msToS(configComponent.minPause)
					sum += RangeConverter.msToS(configComponent.minDuration)
				}
			}
		}
		return sum
	}

	/**
	 * calculates the length of a Sound
	 */
	private fun getAudioFileLength(sound : Sound, context : Context, privateDirUri : String) : Float{
		//Get audio file length
		val uri: Uri = Uri.parse(privateDirUri + "/" + sound.source)
		val mmr = MediaMetadataRetriever()
		mmr.setDataSource(context, uri)
		val durationStr =
			mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
		val ms = durationStr!!.toInt()
		return RangeConverter.msToS(ms)
	}

	/**
	 * returns the maximal duration the configuration can take per iteration in seconds
	 */
	fun getMaxDuration(context : Context, privateDirUri : String) : Float{
		var sum =0f
		for( configComponent in components){
			when (configComponent) {
				is Sound -> {
					sum += RangeConverter.msToS(configComponent.maxPause)
					sum += getAudioFileLength(configComponent,context,privateDirUri)
				}
				is Vibration -> {
					sum += RangeConverter.msToS(configComponent.maxPause)
					sum += RangeConverter.msToS(configComponent.maxDuration)
				}
			}
		}
		return sum
	}
}

/**
 * Exception thrown, when a invalid Configuration is created
 */
class InvalidConfigurationException(message: String) : Exception(message)