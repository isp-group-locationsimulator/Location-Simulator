package com.ispgr5.locationsimulator.domain.useCase

import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.repository.ConfigurationRepository

/**
 * Interface to get a Configuration from Database
 */
class GetConfiguration(
	private val repository: ConfigurationRepository
) {

	/**
	 * calls the matching Configurations Repository function
	 */
	suspend operator fun invoke(id: Int): Configuration? {
		return repository.getConfigurationById(id)
	}
}