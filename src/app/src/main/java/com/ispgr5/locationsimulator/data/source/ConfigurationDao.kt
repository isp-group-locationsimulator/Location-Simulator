package com.ispgr5.locationsimulator.data.source

import androidx.room.*
import com.ispgr5.locationsimulator.domain.model.Configuration
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Database
 * SQL Query's defined here
 */
@Dao
interface ConfigurationDao {

	@Query("SELECT * FROM configuration")
	fun getConfigurations(): Flow<List<Configuration>>

	@Query("SELECT * FROM configuration WHERE id = :id")
	suspend fun getConfigurationById(id: Int): Configuration?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertConfiguration(configuration: Configuration)

	@Delete
	suspend fun deleteConfiguration(configuration: Configuration)

	@Query("SELECT * FROM configuration WHERE isFavorite = 1")
	fun getFavoriteConfigurations(): Flow<List<Configuration>>
}