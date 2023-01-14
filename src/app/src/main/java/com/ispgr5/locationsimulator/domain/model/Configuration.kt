package com.ispgr5.locationsimulator.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The Configuration Entity that the Database stores in the Entry's
 */
@Entity
data class Configuration(
    val name: String,
    val description: String,
    val components: List<ConfigComponent>,
    @PrimaryKey val id: Int? = null
)

class InvalidConfigurationException(message: String) : Exception(message)