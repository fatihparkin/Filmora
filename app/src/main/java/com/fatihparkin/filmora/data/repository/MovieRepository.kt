package com.fatihparkin.filmora.data.repository

import com.fatihparkin.filmora.data.local.dao.MovieDao
import com.fatihparkin.filmora.data.local.entity.MovieEntity
import com.fatihparkin.filmora.data.model.MovieResponse
import com.fatihparkin.filmora.data.remote.ApiService
import retrofit2.Response
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val apiService: ApiService,
    private val movieDao: MovieDao
) {

    // API'den popüler filmleri al
    suspend fun getPopularMovies(page: Int = 1): Response<MovieResponse> {
        return apiService.getPopularMovies(page = page)
    }

    // Room DB'ye listeyi ekle (eskiyi sil, yeniyi kaydet)
    suspend fun refreshPopularMovies(movies: List<MovieEntity>) {
        movieDao.clearMovies()
        movieDao.insertAll(movies)
    }

    // Room'dan verileri getir (internetsiz kullanım için)
    suspend fun getLocalPopularMovies(): List<MovieEntity> {
        return movieDao.getAllMovies()
    }
}
