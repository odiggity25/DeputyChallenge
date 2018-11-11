package com.orrie.deputychallenge.dagger

import com.orrie.deputychallenge.apis.ShiftsApi
import com.orrie.deputychallenge.repositories.ShiftsRepository
import com.orrie.deputychallenge.utils.DateUtils
import com.orrie.deputychallenge.utils.LocationManager
import com.orrie.deputychallenge.utils.ShiftUtils
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

    @Provides
    @Singleton
    fun providesShiftUtils(
        locationManager: LocationManager,
        dateUtils: DateUtils
    ): ShiftUtils {
        return ShiftUtils(locationManager, dateUtils)
    }
}