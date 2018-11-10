package com.orrie.deputychallenge.dagger

import com.orrie.deputychallenge.views.ShiftsActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        ShiftsModule::class,
        NetworkModule::class
    ]
)
interface AppComponent {
    fun inject(shiftsActivity: ShiftsActivity)
}