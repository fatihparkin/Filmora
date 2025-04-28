package com.fatihparkin.filmora.presentation.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatihparkin.filmora.data.model.Movie
import com.fatihparkin.filmora.data.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _favoriteMovies = MutableStateFlow<List<Movie>>(emptyList())
    val favoriteMovies: StateFlow<List<Movie>> = _favoriteMovies

    fun loadFavorites() {
        viewModelScope.launch {
            _favoriteMovies.value = favoriteRepository.getAllFavorites()
        }
    }

    fun removeFavorite(movieId: Int) {
        viewModelScope.launch {
            favoriteRepository.removeFromFavorites(movieId)
            loadFavorites()
        }
    }

    fun addFavorite(movie: Movie) {
        viewModelScope.launch {
            favoriteRepository.addToFavorites(movie)
            loadFavorites()
        }
    }

    suspend fun isFavorite(movieId: Int): Boolean {
        return favoriteRepository.isFavorite(movieId)
    }
}
