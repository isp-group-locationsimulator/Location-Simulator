package com.ispgr5.locationsimulator.domain.useCase

import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.repository.ConfigurationRepository

/**
 * Interface to delete a Configuration from Database
 */
class DeleteConfiguration(
	private val repository: ConfigurationRepository
) {

	/**
	 * calls the matching Configurations Repository function
	 */
	suspend operator fun invoke(configuration: Configuration) {
		repository.deleteConfiguration(configuration = configuration)
	}
}