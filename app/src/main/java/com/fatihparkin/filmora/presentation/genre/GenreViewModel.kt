package com.fatihparkin.filmora.presentation.genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatihparkin.filmora.data.model.Genre
import com.fatihparkin.filmora.data.model.Movie
import com.fatihparkin.filmora.data.repository.GenreRepository
import com.fatihparkin.filmora.presentation.home.SortOption
import com.fatihparkin.filmora.presentation.home.FilterOption
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

    private val _selectedFilters = MutableStateFlow<List<FilterOption>>(emptyList())
    val selectedFilters: StateFlow<List<FilterOption>> = _selectedFilters

    private var originalMovies: List<Movie> = emptyList()

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
                    val movies = response.body()?.results ?: emptyList()
                    _moviesByGenre.value = movies
                    originalMovies = movies
                    _currentSortOption.value = null
                    _selectedFilters.value = emptyList()
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
        _moviesByGenre.value = originalMovies
    }

    fun updateFilters(selected: List<FilterOption>) {
        _selectedFilters.value = selected
        applyFilters()
    }

    private fun applyFilters() {
        var filtered = originalMovies

        selectedFilters.value.forEach { filter ->
            filtered = when (filter) {
                FilterOption.IMDB_ABOVE_5 -> filtered.filter { it.vote_average >= 5 }
                FilterOption.IMDB_ABOVE_7 -> filtered.filter { it.vote_average >= 7 }
                FilterOption.IMDB_ABOVE_8 -> filtered.filter { it.vote_average >= 8 }
                FilterOption.YEAR_AFTER_1990 -> filtered.filter { (it.release_date.take(4).toIntOrNull() ?: 0) >= 1990 }
                FilterOption.YEAR_AFTER_2000 -> filtered.filter { (it.release_date.take(4).toIntOrNull() ?: 0) >= 2000 }
                FilterOption.YEAR_AFTER_2010 -> filtered.filter { (it.release_date.take(4).toIntOrNull() ?: 0) >= 2010 }
                FilterOption.YEAR_AFTER_2020 -> filtered.filter { (it.release_date.take(4).toIntOrNull() ?: 0) >= 2020 }
            }
        }

        _moviesByGenre.value = filtered
    }
}
