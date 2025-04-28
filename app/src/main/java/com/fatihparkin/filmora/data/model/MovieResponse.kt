package com.fatihparkin.filmora.data.model
import java.io.Serializable
data class MovieResponse(
    val page: Int,
    val results: List<Movie>,
    val total_pages: Int,
    val total_results: Int
)



data class Movie(
    val id: Int = 0,
    val title: String = "",
    val poster_path: String = "",
    val overview: String = "",
    val release_date: String = "",
    val backdrop_path: String = "",
    val vote_average: Double = 0.0
) : Serializable