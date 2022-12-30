package com.ispgr5.locationsimulator.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ispgr5.locationsimulator.domain.model.Configuration

/**
 * The Room Database, that stores Configurations
 */
@Database(
    entities = [Configuration::class],
    version = 2
)
abstract class ConfigurationDatabase : RoomDatabase() {
    abstract val configurationDao: ConfigurationDao

    companion object {
        const val DATABASE_NAME = "configuration_db"
    }
}