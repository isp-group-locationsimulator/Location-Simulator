package com.ispgr5.locationsimulator.domain.useCase

/**
 * Interface to Access the Database operations
 */
data class ConfigurationUseCases(
    val getConfigurations: GetConfigurations,
    val deleteConfiguration: DeleteConfiguration,
    val addConfiguration: AddConfiguration,
    val getConfiguration: GetConfiguration,
    val getFavoriteConfigurations: GetFavoriteConfigurations
)