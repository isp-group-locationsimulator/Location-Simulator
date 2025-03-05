package com.ispgr5.locationsimulator.domain.model

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.math.BigDecimal


/**
 * The Configuration Entity that the Database stores in the Entry's
 */
@Serializable
@Entity
data class Configuration(
    val name: String,
    val description: String,
    val randomOrderPlayback: Boolean,
    val components: List<ConfigComponent>,
    val isFavorite: Boolean = false,
    @PrimaryKey val id: Int? = null
) {
    /**
     * returns the minimal duration the configuration can take per iteration in milliseconds
     */
    fun getMinDuration(context: Context, privateDirUri: String): BigDecimal {
        val sum = components.map { configComponent ->
            when (configComponent) {
                is ConfigComponent.Sound -> configComponent.minPause.toBigDecimal() + getAudioFileLength(
                    configComponent,
                    context,
                    privateDirUri
                ).toBigDecimal()

                is ConfigComponent.Vibration -> configComponent.minPause.toBigDecimal() + configComponent.minDuration.toBigDecimal()
            }
        }.reduce { acc, bigDecimal -> acc + bigDecimal }
        return sum
    }

    /**
     * returns the maximal duration the configuration can take per iteration in seconds
     */
    fun getMaxDuration(context: Context, privateDirUri: String): BigDecimal {
        val sum = components.map { configComponent ->
            when (configComponent) {
                is ConfigComponent.Sound -> configComponent.maxPause.toBigDecimal() + getAudioFileLength(
                    configComponent,
                    context,
                    privateDirUri
                ).toBigDecimal()

                is ConfigComponent.Vibration -> configComponent.maxPause.toBigDecimal() + configComponent.maxDuration.toBigDecimal()
            }
        }.reduce { acc, bigDecimal -> acc + bigDecimal }
        return sum
    }

    /**
     * calculates the length of a Sound
     */
    private fun getAudioFileLength(
        sound: ConfigComponent.Sound,
        context: Context,
        privateDirUri: String
    ): Float {
        //Get audio file length
        val uri: Uri = Uri.parse(privateDirUri + "/" + sound.source)
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, uri)
        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val ms = durationStr!!.toInt()
        return RangeConverter.msToS(ms)
    }
}

/**
 * Exception thrown, when a invalid Configuration is created
 */
class InvalidConfigurationException(message: String) : Exception(message)