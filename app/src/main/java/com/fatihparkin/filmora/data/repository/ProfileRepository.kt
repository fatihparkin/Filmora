package com.fatihparkin.filmora.data.repository

import com.fatihparkin.filmora.data.model.Movie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepository @Inject constructor() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private fun getUserId(): String? = auth.currentUser?.uid

    suspend fun addViewedMovie(movie: Movie) {
        val userId = getUserId() ?: return
        val docRef = firestore.collection("users").document(userId)
            .collection("viewed_movies").document(movie.id.toString())

        val data = hashMapOf(
            "id" to movie.id,
            "title" to movie.title,
            "poster_path" to movie.poster_path,
            "release_date" to movie.release_date,
            "vote_average" to movie.vote_average,
            "timestamp" to System.currentTimeMillis()
        )

        docRef.set(data).await()
    }

    suspend fun getViewedMovies(): List<Movie> {
        val userId = getUserId() ?: return emptyList()
        val snapshot = firestore.collection("users").document(userId)
            .collection("viewed_movies")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val id = doc.getLong("id")?.toInt() ?: return@mapNotNull null
            val title = doc.getString("title") ?: return@mapNotNull null
            val posterPath = doc.getString("poster_path") ?: ""
            val releaseDate = doc.getString("release_date") ?: ""
            val voteAverage = doc.getDouble("vote_average") ?: 0.0

            Movie(id, title, posterPath, "", releaseDate, "", voteAverage)
        }
    }
}
