package com.example.jobfinderapp.di

import com.example.jobfinderapp.data.remote.api.JobApiService
import com.example.jobfinderapp.data.remote.api.PreferencesApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Remotive API with qualifier
    @Provides
    @Singleton
    @Named("RemotiveRetrofit")
    fun provideRemotiveRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://remotive.com/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideJobApiService(
        @Named("RemotiveRetrofit") retrofit: Retrofit
    ): JobApiService {
        return retrofit.create(JobApiService::class.java)
    }

    // AWS API with qualifier
    @Provides
    @Singleton
    @Named("AWS_BASE_URL")
    fun provideAwsBaseUrl(): String {
        return "https://dy4ijz0f53.execute-api.ap-south-1.amazonaws.com/dev/preferences/"
    }

    @Provides
    @Singleton
    @Named("AwsRetrofit")
    fun provideAwsRetrofit(
        okHttpClient: OkHttpClient,
        @Named("AWS_BASE_URL") baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePreferencesApiService(
        @Named("AwsRetrofit") retrofit: Retrofit
    ): PreferencesApiService {
        return retrofit.create(PreferencesApiService::class.java)
    }
}

