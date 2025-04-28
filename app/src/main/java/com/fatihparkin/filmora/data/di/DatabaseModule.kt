package com.fatihparkin.filmora.data.di

import android.content.Context
import androidx.room.Room
import com.fatihparkin.filmora.data.local.dao.MovieDao
import com.fatihparkin.filmora.data.local.db.FilmoraDatabase
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
    fun provideFilmoraDatabase(@ApplicationContext context: Context): FilmoraDatabase {
        return Room.databaseBuilder(
            context,
            FilmoraDatabase::class.java,
            "filmora.db"
        ).build()
    }

    @Provides
    fun provideMovieDao(database: FilmoraDatabase): MovieDao {
        return database.movieDao()
    }
}
