package com.fatihparkin.filmora.presentation.genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatihparkin.filmora.data.model.Genre
import com.fatihparkin.filmora.data.model.Movie
import com.fatihparkin.filmora.data.repository.GenreRepository
import com.fatihparkin.filmora.presentation.home.SortOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreViewModel @Inject constructor(
    private val genreRepository: GenreRepository
) : ViewModel() {

    private val _genreList = MutableStateFlow<List<Genre>>(emptyList())
    val genreList: StateFlow<List<Genre>> = _genreList

    private val _moviesByGenre = MutableStateFlow<List<Movie>>(emptyList())
    val moviesByGenre: StateFlow<List<Movie>> = _moviesByGenre

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _currentSortOption = MutableStateFlow<SortOption?>(null)
    val currentSortOption: StateFlow<SortOption?> = _currentSortOption

    fun fetchGenres() {
        viewModelScope.launch {
            try {
                val response = genreRepository.getGenres()
                if (response.isSuccessful) {
                    _genreList.value = response.body()?.genres ?: emptyList()
                } else {
                    _errorMessage.value = "Türler alınamadı"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            }
        }
    }

    fun fetchMoviesByGenre(genreId: Int) {
        viewModelScope.launch {
            try {
                val response = genreRepository.getMoviesByGenre(genreId)
                if (response.isSuccessful) {
                    _moviesByGenre.value = response.body()?.results ?: emptyList()
                    _currentSortOption.value = null // sıfırlıyoruz
                } else {
                    _errorMessage.value = "Filmler alınamadı"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            }
        }
    }

    fun sortMovies(option: SortOption) {
        val movies = _moviesByGenre.value
        val sortedMovies = when (option) {
            SortOption.RATING_HIGH_TO_LOW -> movies.sortedByDescending { it.vote_average }
            SortOption.RATING_LOW_TO_HIGH -> movies.sortedBy { it.vote_average }
            SortOption.DATE_NEW_TO_OLD -> movies.sortedByDescending { it.release_date }
            SortOption.DATE_OLD_TO_NEW -> movies.sortedBy { it.release_date }
        }
        _moviesByGenre.value = sortedMovies
        _currentSortOption.value = option
    }

    fun resetSorting() {
        _currentSortOption.value = null
    }
}
