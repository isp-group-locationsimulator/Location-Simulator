package com.ispgr5.locationsimulator.domain.repository

import com.ispgr5.locationsimulator.domain.model.Configuration
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Database Repository
 * The functions, that the Database provides
 */
interface ConfigurationRepository {

    fun getConfigurations(): Flow<List<Configuration>>

    suspend fun getConfigurationById(id: Int): Configuration?

    suspend fun insertConfiguration(configuration: Configuration)

    suspend fun deleteConfiguration(configuration: Configuration)

    fun getFavoriteConfigurations(): Flow<List<Configuration>>
}
