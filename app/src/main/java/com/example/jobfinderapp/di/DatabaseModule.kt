package com.example.jobfinderapp.di

import android.content.Context
import androidx.room.Room
import com.example.jobfinderapp.data.local.JobDatabase
import com.example.jobfinderapp.data.local.dao.JobDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providedatabase(@ApplicationContext context: Context): JobDatabase {
        return Room.databaseBuilder(context, JobDatabase::class.java, "job_finder_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideJobDao(database: JobDatabase): JobDao{
        return database.jobDao()
    }


}