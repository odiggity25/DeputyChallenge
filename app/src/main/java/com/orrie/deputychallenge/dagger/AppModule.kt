package com.orrie.deputychallenge.dagger

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class AppModule(val context: Context) {

    @Provides
    fun providesContext(): Context {
        return context
    }

}