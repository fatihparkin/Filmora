package com.fatihparkin.filmora.presentation.review.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatihparkin.filmora.data.model.MovieReview
import com.fatihparkin.filmora.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewModel @Inject constructor(
    private val repository: ReviewRepository
) : ViewModel() {

    private val _userReviews = MutableStateFlow<List<MovieReview>>(emptyList())
    val userReviews: StateFlow<List<MovieReview>> = _userReviews

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchReviews(movieId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val reviews = repository.getReviewsForMovie(movieId)
                _userReviews.value = reviews
            } catch (e: Exception) {
                _errorMessage.value = "Yorumlar alınamadı: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitReview(movieId: Int, content: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.addReview(movieId, content)
                onSuccess()
                fetchReviews(movieId)
            } catch (e: Exception) {
                _errorMessage.value = "Yorum eklenemedi: ${e.localizedMessage}"
            }
        }
    }

    fun deleteReview(reviewId: String, movieId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteReview(reviewId)
                fetchReviews(movieId)
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Yorum silinemedi: ${e.localizedMessage}"
            }
        }
    }

    fun updateReview(reviewId: String, newContent: String, movieId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateReview(reviewId, newContent)
                fetchReviews(movieId)
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Yorum güncellenemedi: ${e.localizedMessage}"
            }
        }
    }
}
