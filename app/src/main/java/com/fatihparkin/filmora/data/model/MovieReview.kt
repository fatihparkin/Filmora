package com.fatihparkin.filmora.data.model

data class MovieReview(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val movieId: Int = 0,
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isEdited: Boolean = false // Düzenlendi bilgisi için eklendi
)