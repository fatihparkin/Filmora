package com.fatihparkin.filmora.presentation.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatihparkin.filmora.data.model.Movie
import com.fatihparkin.filmora.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _viewedMovies = MutableStateFlow<List<Movie>>(emptyList())
    val viewedMovies: StateFlow<List<Movie>> = _viewedMovies

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchViewedMovies() {
        viewModelScope.launch {
            try {
                _viewedMovies.value = repository.getViewedMovies()
            } catch (e: Exception) {
                _errorMessage.value = "Veriler alınamadı: ${e.localizedMessage}"
            }
        }
    }

    fun saveViewedMovie(movie: Movie) {
        viewModelScope.launch {
            try {
                repository.addViewedMovie(movie)
            } catch (e: Exception) {
                // sessiz geçilebilir ya da loglanabilir
            }
        }
    }
}
