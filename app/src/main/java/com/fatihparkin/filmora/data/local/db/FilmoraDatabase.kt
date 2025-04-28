package com.fatihparkin.filmora.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fatihparkin.filmora.data.local.dao.MovieDao
import com.fatihparkin.filmora.data.local.entity.MovieEntity

@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class FilmoraDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
