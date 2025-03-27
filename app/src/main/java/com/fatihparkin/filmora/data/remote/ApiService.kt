package com.fatihparkin.filmora.data.remote

import com.fatihparkin.filmora.BuildConfig
import com.fatihparkin.filmora.data.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY, // API key parametre olarak
        @Query("page") page: Int = 1 //
    ): Response<MovieResponse>
}
