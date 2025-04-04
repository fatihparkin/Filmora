package com.fatihparkin.filmora.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatihparkin.filmora.data.model.Cast
import com.fatihparkin.filmora.data.model.Movie
import com.fatihparkin.filmora.data.model.Review
import com.fatihparkin.filmora.data.model.Video
import com.fatihparkin.filmora.data.repository.MovieDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieDetailRepository
) : ViewModel() {

    private val _movieDetail = MutableStateFlow<Movie?>(null)
    val movieDetail: StateFlow<Movie?> = _movieDetail

    private val _videoList = MutableStateFlow<List<Video>>(emptyList())
    val videoList: StateFlow<List<Video>> = _videoList

    private val _castList = MutableStateFlow<List<Cast>>(emptyList())
    val castList: StateFlow<List<Cast>> = _castList

    private val _reviewList = MutableStateFlow<List<Review>>(emptyList())
    val reviewList: StateFlow<List<Review>> = _reviewList

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchMovieDetail(movieId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getMovieDetail(movieId)
                if (response.isSuccessful) {
                    _movieDetail.value = response.body()
                } else {
                    _errorMessage.value = "Film bilgisi alınamadı"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            }
        }
    }

    fun fetchMovieVideos(movieId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getMovieVideos(movieId)
                if (response.isSuccessful) {
                    val videos = response.body()?.results ?: emptyList()
                    _videoList.value = videos.filter {
                        it.site == "YouTube" && it.type == "Trailer"
                    }
                } else {
                    _errorMessage.value = "Video bilgisi alınamadı"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            }
        }
    }

    fun fetchCast(movieId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getMovieCredits(movieId)
                if (response.isSuccessful) {
                    _castList.value = response.body()?.cast ?: emptyList()
                } else {
                    _errorMessage.value = "Oyuncu bilgisi alınamadı"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            }
        }
    }

    fun fetchReviews(movieId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getMovieReviews(movieId)
                if (response.isSuccessful) {
                    _reviewList.value = response.body()?.results ?: emptyList()
                } else {
                    _errorMessage.value = "Yorumlar alınamadı"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            }
        }
    }
}
