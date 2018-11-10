package com.orrie.deputychallenge.dagger

import com.orrie.deputychallenge.apis.ShiftsApi
import com.orrie.deputychallenge.repositories.ShiftsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ShiftsModule {

    @Provides
    @Singleton
    fun providesShiftsRepository(): ShiftsRepository {
        return ShiftsRepository()
    }

    @Provides
    @Singleton
    fun providesShiftsApi(): ShiftsApi {
        return ShiftsApi()
    }
}