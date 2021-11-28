package com.kedaireka.monitoring_biomassa.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.kedaireka.monitoring_biomassa.service.MonitoringService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Singleton
    @Provides
    fun provideMoshiBuilder(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient.Builder = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()

                val request = original.newBuilder()
//                    .header("User-Agent", "PostmanRuntime/7.28.3")
//                    .header("Accept", "*/*")
//                    .header("Accept-Encoding", "gzip, deflate, br")
//                    .header("Connection", "keep-alive")
//                    .header("api-key", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IidhZG1pbicnMjEyMzJmMjk3YTU3YTVhNzQzODk0YTBlNGE4MDFmYzMnIn0.nwRSoKhCeArz7xie5hD26SyWjX0qqUZY6_IV3AkXvi0")
                    .build()

                chain.proceed(request)
            }
            .addInterceptor(interceptor)

        return client.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(moshi: Moshi, client: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("https://web-biomassa.000webhostapp.com/api/v1/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
    }


    @Singleton
    @Provides
    fun provideMonitoringService(retrofit: Retrofit.Builder): MonitoringService {
        return retrofit
            .build()
            .create(MonitoringService::class.java)
    }
}