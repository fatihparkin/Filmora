package com.fatihparkin.filmora.data.repository


import com.fatihparkin.filmora.data.model.ReviewResponse
import com.fatihparkin.filmora.data.model.CastResponse
import com.fatihparkin.filmora.data.model.Movie
import com.fatihparkin.filmora.data.model.VideoResponse
import com.fatihparkin.filmora.data.remote.ApiService
import retrofit2.Response
import javax.inject.Inject

class MovieDetailRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getMovieDetail(movieId: Int): Response<Movie> {
        return apiService.getMovieDetail(movieId)
    }

    suspend fun getMovieVideos(movieId: Int): Response<VideoResponse> {
        return apiService.getMovieVideos(movieId)
    }

    suspend fun getMovieCredits(movieId: Int): Response<CastResponse> {
        return apiService.getMovieCredits(movieId)
    }
    suspend fun getMovieReviews(movieId: Int): Response<ReviewResponse> {
        return apiService.getMovieReviews(movieId)
    }

}
