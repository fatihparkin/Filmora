package com.fatihparkin.filmora.data.repository

import com.fatihparkin.filmora.data.model.GenreResponse
import com.fatihparkin.filmora.data.model.MovieResponse
import com.fatihparkin.filmora.data.remote.ApiService
import retrofit2.Response
import javax.inject.Inject

class GenreRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getGenres(): Response<GenreResponse> {
        return apiService.getGenres()
    }

    suspend fun getMoviesByGenre(genreId: Int): Response<MovieResponse> {
        return apiService.getMoviesByGenre(genreId = genreId)
    }
}
