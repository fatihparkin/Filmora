package com.fatihparkin.filmora.data.model

data class VideoResponse(
    val results: List<Video>
)

data class Video(
    val id: String,
    val key: String,           // YouTube video key
    val name: String,
    val site: String,          // Örneğin: "YouTube"
    val type: String,          // Örneğin: "Trailer"
    val official: Boolean,
    val published_at: String
)
