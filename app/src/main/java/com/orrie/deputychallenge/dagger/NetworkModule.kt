package com.orrie.deputychallenge.dagger

import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton




@Module
class NetworkModule {

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", "Deputy 7e21f70117762b70759098504e763c96f5255eaf  -")
                .method(original.method(), original.body())
                .build()
            chain.proceed(request)
        }
        okHttpClientBuilder.addInterceptor(loggingInterceptor)

        val client = okHttpClientBuilder.build()

        val builder = Retrofit.Builder()
            .baseUrl("https://apjoqdqpi3.execute-api.us-west-2.amazonaws.com/dmc/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
        return builder.build()
    }

}