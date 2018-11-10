package com.orrie.deputychallenge.dagger

import com.orrie.deputychallenge.apis.ShiftsApi
import com.orrie.deputychallenge.repositories.ShiftsRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class ShiftsModule {

    @Provides
    @Singleton
    fun providesShiftsApi(retrofit: Retrofit): ShiftsApi {
        return retrofit.create(ShiftsApi::class.java)
    }

    @Provides
    @Singleton
    fun providesShiftsRepository(
        shiftsApi: ShiftsApi
    ): ShiftsRepository {
        return ShiftsRepository(shiftsApi)
    }
}