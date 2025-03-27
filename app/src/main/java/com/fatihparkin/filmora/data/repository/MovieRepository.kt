package com.fatihparkin.filmora.data.repository

import com.fatihparkin.filmora.data.remote.ApiService
import com.fatihparkin.filmora.data.model.MovieResponse
import retrofit2.Response
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getPopularMovies(page: Int = 1): Response<MovieResponse> {
        return apiService.getPopularMovies(page = page)
    }
}
