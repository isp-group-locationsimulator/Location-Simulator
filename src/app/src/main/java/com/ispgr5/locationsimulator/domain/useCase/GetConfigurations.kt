package com.ispgr5.locationsimulator.domain.useCase

import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.repository.ConfigurationRepository
import kotlinx.coroutines.flow.Flow

/**
 * Interface to get all Configurations from Database
 */
class GetConfigurations(
    private val repository: ConfigurationRepository
) {

    operator fun invoke(): Flow<List<Configuration>> {
        return repository.getConfigurations()
    }
}