package com.orrie.deputychallenge.dagger

import android.content.Context
import com.orrie.deputychallenge.utils.DateUtils
import com.orrie.deputychallenge.utils.LocationManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val context: Context) {

    @Provides
    fun providesContext(): Context {
        return context
    }

    @Provides
    @Singleton
    fun providesLocationManager(context: Context): LocationManager {
        return LocationManager(context)
    }

    @Provides
    @Singleton
    fun providesDateUtils(): DateUtils {
        return DateUtils()
    }

}