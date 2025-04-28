package com.fatihparkin.filmora.data.mapper

import com.fatihparkin.filmora.data.local.entity.MovieEntity
import com.fatihparkin.filmora.data.model.Movie
import com.fatihparkin.filmora.data.model.MovieResponse

// API'den gelen veriyi Room için MovieEntity'ye çevir
fun MovieResponse.toMovieEntityList(): List<MovieEntity> {
    return results.map {
        MovieEntity(
            id = it.id,
            title = it.title ?: "",
            overview = it.overview ?: "",
            poster_path = it.poster_path ?: "",
            release_date = it.release_date ?: "",
            backdrop_path = it.backdrop_path ?: "",
            vote_average = it.vote_average ?: 0.0
        )
    }
}

// Room'dan gelen veriyi Movie formatına çevir (isteğe bağlı)
fun List<MovieEntity>.toMovieList(): List<Movie> {
    return map {
        Movie(
            id = it.id,
            title = it.title,
            overview = it.overview,
            poster_path = it.poster_path,
            release_date = it.release_date,
            backdrop_path = it.backdrop_path,
            vote_average = it.vote_average
        )
    }
}
