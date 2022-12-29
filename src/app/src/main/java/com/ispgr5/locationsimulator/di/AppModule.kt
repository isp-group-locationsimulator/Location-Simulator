package com.ispgr5.locationsimulator.di

import android.app.Application
import androidx.room.Room
import com.ispgr5.locationsimulator.data.repository.ConfigurationRepositoryImpl
import com.ispgr5.locationsimulator.data.source.ConfigurationDatabase
import com.ispgr5.locationsimulator.domain.repository.ConfigurationRepository
import com.ispgr5.locationsimulator.domain.useCase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * The Data Injection Module
 * Loads the Database by starting the app and provides data and object to ViewModels
 * dagger-hilt takes care of it
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * creates the Database
     */
    @Provides
    @Singleton
    fun provideConfigurationDatabase(app: Application): ConfigurationDatabase {
        return Room.databaseBuilder(
            app,
            ConfigurationDatabase::class.java,
            ConfigurationDatabase.DATABASE_NAME
        ).build()
    }

    /**
     * creates the Database Repository
     */
    @Provides
    @Singleton
    fun provideConfigurationRepository(db: ConfigurationDatabase): ConfigurationRepository {
        return ConfigurationRepositoryImpl(db.configurationDao)
    }

    /**
     * creates the Database Interface (UseCases)
     */
    @Provides
    @Singleton
    fun provideConfigurationUseCases(repository: ConfigurationRepository): ConfigurationUseCases {
        return ConfigurationUseCases(
            getConfigurations = GetConfigurations(repository),
            deleteConfiguration = DeleteConfiguration(repository),
            addConfiguration = AddConfiguration(repository),
            getConfiguration = GetConfiguration(repository)
        )
    }
}