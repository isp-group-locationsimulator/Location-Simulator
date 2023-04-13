package com.ispgr5.locationsimulator.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.model.ConfigurationComponentConverter

/**
 * The Room Database, that stores Configurations
 */
@Database(
	entities = [Configuration::class],
	version = 7,
	exportSchema = false
)
@TypeConverters(ConfigurationComponentConverter::class)
abstract class ConfigurationDatabase : RoomDatabase() {
	abstract val configurationDao: ConfigurationDao

	/**
	 * The name of the Database
	 */
	companion object {
		const val DATABASE_NAME = "configuration_db"
	}
}