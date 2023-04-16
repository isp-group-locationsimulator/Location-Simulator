package com.ispgr5.locationsimulator.domain.useCase

import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.repository.ConfigurationRepository
import kotlinx.coroutines.flow.Flow

/**
 * Interface to get all Configurations from Database where isFavorite = true
 */
class GetFavoriteConfigurations(
	private val repository: ConfigurationRepository
) {

	/**
	 * calls the matching Configurations Repository function
	 */
	operator fun invoke(): Flow<List<Configuration>> {
		return repository.getFavoriteConfigurations()
	}
}