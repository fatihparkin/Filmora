package com.fatihparkin.filmora.presentation.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatihparkin.filmora.data.repository.MovieRepository
import com.fatihparkin.filmora.data.model.MovieResponse
import com.fatihparkin.filmora.data.mapper.toMovieEntityList
import com.fatihparkin.filmora.data.mapper.toMovieList
import com.fatihparkin.filmora.util.NetworkUtils
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

    private val _currentSortOption = MutableStateFlow<SortOption?>(null)
    val currentSortOption: StateFlow<SortOption?> = _currentSortOption

    private val _selectedFilters = MutableStateFlow<List<FilterOption>>(emptyList())
    val selectedFilters: StateFlow<List<FilterOption>> = _selectedFilters

    private var originalMovies = emptyList<com.fatihparkin.filmora.data.model.Movie>()

    fun fetchPopularMovies(context: Context) {
        viewModelScope.launch {
            if (NetworkUtils.isNetworkAvailable(context)) {
                try {
                    val response = movieRepository.getPopularMovies()
                    if (response.isSuccessful && response.body() != null) {
                        val movieResponse = response.body()!!
                        originalMovies = movieResponse.results
                        _movieResponse.value = movieResponse
                        movieRepository.refreshPopularMovies(movieResponse.toMovieEntityList())
                    } else {
                        _errorMessage.value = "Sunucudan veri alınamadı: ${response.message()}"
                        loadLocalMovies()
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Hata: ${e.localizedMessage}"
                    loadLocalMovies()
                }
            } else {
                _errorMessage.value = "İnternet bağlantısı yok. Yerel veriler yüklendi."
                loadLocalMovies()
            }
        }
    }

    private suspend fun loadLocalMovies() {
        val localMovies = movieRepository.getLocalPopularMovies()
        originalMovies = localMovies.toMovieList()
        _movieResponse.value = MovieResponse(
            page = 1,
            results = originalMovies,
            total_pages = 1,
            total_results = originalMovies.size
        )
    }

    fun sortMovies(option: SortOption) {
        _currentSortOption.value = option
        applyFiltersAndSort()
    }

    fun resetSorting() {
        _currentSortOption.value = null
        applyFiltersAndSort()
    }

    fun updateFilters(selected: List<FilterOption>) {
        _selectedFilters.value = selected
        applyFiltersAndSort()
    }

    private fun applyFiltersAndSort() {
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

        currentSortOption.value?.let { option ->
            filtered = when (option) {
                SortOption.RATING_HIGH_TO_LOW -> filtered.sortedByDescending { it.vote_average }
                SortOption.RATING_LOW_TO_HIGH -> filtered.sortedBy { it.vote_average }
                SortOption.DATE_NEW_TO_OLD -> filtered.sortedByDescending { it.release_date }
                SortOption.DATE_OLD_TO_NEW -> filtered.sortedBy { it.release_date }
            }
        }

        _movieResponse.value = _movieResponse.value?.copy(results = filtered)
    }
}
