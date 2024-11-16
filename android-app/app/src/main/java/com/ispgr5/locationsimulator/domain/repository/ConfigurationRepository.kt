package com.ispgr5.locationsimulator.domain.repository

import com.ispgr5.locationsimulator.domain.model.Configuration
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Database Repository
 * The functions, that the Database provides
 */
interface ConfigurationRepository {

	/**
	 * reads the Database and return the List of Configurations
	 */
	fun getConfigurations(): Flow<List<Configuration>>

	/**
	 * reads the Database und returns the Configuration with the given id else null will be returned
	 */
	suspend fun getConfigurationById(id: Int): Configuration?

	/**
	 * inserts a Configuration into the Database
	 */
	suspend fun insertConfiguration(configuration: Configuration)

	/**
	 * deletes the provided Configuration from Database
	 */
	suspend fun deleteConfiguration(configuration: Configuration)

	/**
	 * reads the Database and returns the Configurations, that are marked as favorite
	 */
	fun getFavoriteConfigurations(): Flow<List<Configuration>>
}