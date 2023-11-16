package com.strimm.application.di

import com.google.gson.GsonBuilder
import com.strimm.application.retrofit.StrimmApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {


    @Singleton
    @Provides
    fun providesMoviesApi(): StrimmApi {

        val loggingInterceptor = HttpLoggingInterceptor()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val client = OkHttpClient.Builder()


        val clientBuilder = client.connectTimeout(150, TimeUnit.SECONDS)
            .readTimeout(150, TimeUnit.SECONDS)
            .callTimeout(150, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder().baseUrl("https://services-api.strimm.com/api/v1/services/mobile/")
            .addConverterFactory(ScalarsConverterFactory.create()).client(clientBuilder)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(StrimmApi::class.java)

    }

}