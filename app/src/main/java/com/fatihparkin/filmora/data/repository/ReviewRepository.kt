package com.fatihparkin.filmora.data.repository

import android.util.Log
import com.fatihparkin.filmora.data.model.MovieReview
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewRepository @Inject constructor() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private fun getUserId(): String? = auth.currentUser?.uid
    private fun getUserEmail(): String? = auth.currentUser?.email

    suspend fun addReview(movieId: Int, content: String) {
        val userId = getUserId()
        val email = getUserEmail()

        if (userId == null || email == null) {
            Log.e("ReviewRepo", "Kullanıcı giriş yapmamış, yorum eklenemedi.")
            return
        }

        val newReview = hashMapOf(
            "userId" to userId,
            "userEmail" to email,
            "movieId" to movieId.toLong(), // Firestore uyumu için garantiye alındı
            "content" to content,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("movie_reviews")
            .add(newReview)
            .addOnSuccessListener {
                Log.d("ReviewRepo", "Yorum başarıyla eklendi: ${it.id}")
            }
            .addOnFailureListener {
                Log.e("ReviewRepo", "Yorum eklenemedi: ${it.message}")
            }
            .await()
    }

    suspend fun getReviewsForMovie(movieId: Int): List<MovieReview> {
        return try {
            val snapshot = firestore.collection("movie_reviews")
                .whereEqualTo("movieId", movieId.toLong()) // 🔧 kritik nokta!
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d("ReviewRepo", "Toplam ${snapshot.size()} yorum bulundu.")

            snapshot.documents.mapNotNull { doc ->
                val id = doc.id
                val userId = doc.getString("userId") ?: return@mapNotNull null
                val userEmail = doc.getString("userEmail") ?: "Bilinmeyen"
                val content = doc.getString("content") ?: ""
                val ts = doc.getLong("timestamp") ?: 0L
                val mId = doc.getLong("movieId")?.toInt() ?: return@mapNotNull null

                MovieReview(
                    id = id,
                    userId = userId,
                    userEmail = userEmail,
                    movieId = mId,
                    content = content,
                    timestamp = ts
                )
            }

        } catch (e: Exception) {
            Log.e("ReviewRepo", "Yorumları alırken hata oluştu: ${e.localizedMessage}")
            emptyList()
        }
    }
}
