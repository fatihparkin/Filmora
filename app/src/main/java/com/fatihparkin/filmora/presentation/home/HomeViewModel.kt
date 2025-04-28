package com.fatihparkin.filmora.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatihparkin.filmora.data.repository.MovieRepository
import com.fatihparkin.filmora.data.model.MovieResponse
import com.fatihparkin.filmora.data.mapper.toMovieEntityList
import com.fatihparkin.filmora.data.mapper.toMovieList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _movieResponse = MutableStateFlow<MovieResponse?>(null)
    val movieResponse: StateFlow<MovieResponse?> = _movieResponse

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchPopularMovies() {
        viewModelScope.launch {
            try {
                val response = movieRepository.getPopularMovies()
                if (response.isSuccessful && response.body() != null) {
                    val movieResponse = response.body()!!
                    _movieResponse.value = movieResponse

                    // Room'a kaydet
                    movieRepository.refreshPopularMovies(movieResponse.toMovieEntityList())
                } else {
                    _errorMessage.value = "Sunucudan veri alınamadı: ${response.message()}"
                    loadLocalMovies()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Hata: ${e.localizedMessage}"
                loadLocalMovies()
            }
        }
    }

    private suspend fun loadLocalMovies() {
        val localMovies = movieRepository.getLocalPopularMovies()
        _movieResponse.value = MovieResponse(
            page = 1,
            results = localMovies.toMovieList(),
            total_pages = 1,
            total_results = localMovies.size
        )
    }
}
