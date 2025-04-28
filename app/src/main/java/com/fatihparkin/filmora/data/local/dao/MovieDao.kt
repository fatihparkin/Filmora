package com.fatihparkin.filmora.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fatihparkin.filmora.data.local.entity.MovieEntity

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    @Query("DELETE FROM movies")
    suspend fun clearMovies()
}
