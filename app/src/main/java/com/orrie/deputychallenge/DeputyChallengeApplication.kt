package com.orrie.deputychallenge

import android.app.Application
import com.orrie.deputychallenge.dagger.AppComponent
import com.orrie.deputychallenge.dagger.AppModule
import com.orrie.deputychallenge.dagger.DaggerAppComponent

class DeputyChallengeApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }

    companion object {
        lateinit var appComponent: AppComponent
    }
}