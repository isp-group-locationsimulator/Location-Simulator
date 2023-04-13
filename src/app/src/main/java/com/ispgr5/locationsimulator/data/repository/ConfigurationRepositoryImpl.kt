package com.ispgr5.locationsimulator.data.repository

import com.ispgr5.locationsimulator.data.source.ConfigurationDao
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.repository.ConfigurationRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of the Database Repository
 * functions, that can be called by use cases to interact with the Database
 */
class ConfigurationRepositoryImpl(
	private val dao: ConfigurationDao
) : ConfigurationRepository {

	/**
	 * reads the Database and returns all Configurations
	 */
	override fun getConfigurations(): Flow<List<Configuration>> {
		return dao.getConfigurations()
	}

	/**
	 * reads the Database und returns the Configuration with the given id else null will be returned
	 */
	override suspend fun getConfigurationById(id: Int): Configuration? {
		return dao.getConfigurationById(id = id)
	}

	/**
	 * inserts a Configuration into the Database
	 */
	override suspend fun insertConfiguration(configuration: Configuration) {
		return dao.insertConfiguration(configuration = configuration)
	}

	/**
	 * deletes the provided Configuration from Database
	 */
	override suspend fun deleteConfiguration(configuration: Configuration) {
		return dao.deleteConfiguration(configuration = configuration)
	}

	/**
	 * reads the Database and returns the Configurations, that are marked as favorite
	 */
	override fun getFavoriteConfigurations(): Flow<List<Configuration>> {
		return dao.getFavoriteConfigurations()
	}
}