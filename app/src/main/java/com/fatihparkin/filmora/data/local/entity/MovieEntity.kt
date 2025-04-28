package com.fatihparkin.filmora.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fatihparkin.filmora.data.model.Movie

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    val release_date: String,
    val backdrop_path: String,
    val vote_average: Double
) {
    fun toMovie(): Movie {
        return Movie(
            id = id,
            title = title,
            overview = overview,
            poster_path = poster_path,
            release_date = release_date,
            backdrop_path = backdrop_path,
            vote_average = vote_average
        )
    }
}
