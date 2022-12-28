package com.ispgr5.locationsimulator.domain.useCase

import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.repository.ConfigurationRepository

/**
 * Interface to delete a Configuration to Database
 */
class DeleteConfiguration(
    private val repository: ConfigurationRepository
) {

    suspend operator fun invoke(configuration: Configuration) {
        repository.deleteConfiguration(configuration = configuration)
    }
}