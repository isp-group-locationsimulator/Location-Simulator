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

    override fun getConfigurations(): Flow<List<Configuration>> {
        return dao.getConfigurations()
    }

    override suspend fun getConfigurationById(id: Int): Configuration? {
        return dao.getConfigurationById(id = id)
    }

    override suspend fun insertConfiguration(configuration: Configuration) {
        return dao.insertConfiguration(configuration = configuration)
    }

    override suspend fun deleteConfiguration(configuration: Configuration) {
        return dao.deleteConfiguration(configuration = configuration)
    }

    override fun getFavoriteConfigurations(): Flow<List<Configuration>> {
        return dao.getFavoriteConfigurations()
    }
}