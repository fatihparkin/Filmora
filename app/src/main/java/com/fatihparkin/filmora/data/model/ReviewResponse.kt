package com.fatihparkin.filmora.data.model

data class ReviewResponse(
    val results: List<Review>
)

data class Review(
    val id: String,
    val author: String,
    val content: String,
    val created_at: String
)
