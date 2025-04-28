package com.fatihparkin.filmora.data.repository

import com.fatihparkin.filmora.data.model.Movie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private val currentUser get() = auth.currentUser
    private val favoritesCollection
        get() = firestore.collection("users")
            .document(currentUser?.uid ?: "")
            .collection("favorites")

    suspend fun addToFavorites(movie: Movie) {
        favoritesCollection
            .document(movie.id.toString())
            .set(movie)
            .await()
    }

    suspend fun removeFromFavorites(movieId: Int) {
        favoritesCollection
            .document(movieId.toString())
            .delete()
            .await()
    }

    suspend fun isFavorite(movieId: Int): Boolean {
        val document = favoritesCollection
            .document(movieId.toString())
            .get()
            .await()
        return document.exists()
    }

    suspend fun getAllFavorites(): List<Movie> {
        val snapshot = favoritesCollection.get().await()
        return snapshot.documents.mapNotNull { it.toObject(Movie::class.java) }
    }
}
