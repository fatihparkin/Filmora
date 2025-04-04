package com.fatihparkin.filmora.data.model

data class CastResponse(
    val cast: List<Cast>
)

data class Cast(
    val id: Int,
    val name: String,
    val character: String,
    val profile_path: String?
)
