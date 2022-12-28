package com.ispgr5.locationsimulator.domain.useCase

import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.model.InvalidConfigurationException
import com.ispgr5.locationsimulator.domain.repository.ConfigurationRepository

/**
 * Interface to add a Configuration to Database
 */
class AddConfiguration(
    private val repository: ConfigurationRepository
) {

    @Throws(InvalidConfigurationException::class)
    suspend operator fun invoke(configuration: Configuration) {
        if (configuration.duration < 1) {
            throw InvalidConfigurationException("The duration of the Configuration can't be lower than 1.")
        }
        if (configuration.pause < 0) {
            throw InvalidConfigurationException("The pause of the Configuration can't be lower than 0.")
        }
        repository.insertConfiguration(configuration)
    }
}