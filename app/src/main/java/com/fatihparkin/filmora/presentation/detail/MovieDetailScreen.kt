@file:OptIn(ExperimentalMaterial3Api::class)

package com.fatihparkin.filmora.presentation.detail

import android.content.Intent
import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.fatihparkin.filmora.data.model.Cast
import com.fatihparkin.filmora.data.model.Movie
import com.fatihparkin.filmora.data.model.MovieReview
import com.fatihparkin.filmora.data.model.Review
import com.fatihparkin.filmora.presentation.favorite.viewmodel.FavoriteViewModel
import com.fatihparkin.filmora.presentation.profile.viewmodel.ProfileViewModel
import com.fatihparkin.filmora.presentation.review.viewmodel.ReviewModel
import com.fatihparkin.filmora.presentation.review.viewmodel.ReviewViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.fatihparkin.filmora.util.ConnectivityState



@Composable
fun MovieDetailScreen(
    movieId: Int,
    viewModel: MovieDetailViewModel,
    navController: NavController,
    favoriteViewModel: FavoriteViewModel = hiltViewModel(),
    reviewViewModel: ReviewModel = hiltViewModel()
) {
    val isConnected by ConnectivityState.isConnected.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // ⚠️ Eğer internet yoksa direkt uyarı göster ve sayfadan çık
    if (!isConnected) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Film Detayı") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color(0xFFEAF6FF)
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "⚠️ Detayları görüntülemek için internet bağlantısı gereklidir.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        return // Sayfanın geri kalanını çalıştırma
    }

    // ✅ İnternet varsa devam...
    val movie = viewModel.movieDetail.collectAsState().value
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val isLoggedIn = firebaseUser != null
    val userReviews by reviewViewModel.userReviews.collectAsState()
    val isLoading by reviewViewModel.isLoading.collectAsState()
    val reviewText = remember { mutableStateOf(TextFieldValue()) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val favoriteMovies = favoriteViewModel.favoriteMovies.collectAsState()
    val isFavorite = favoriteMovies.value.any { it.id == movie?.id }
    val videos = viewModel.videoList.collectAsState().value
    val castList = viewModel.castList.collectAsState().value
    val reviewList = viewModel.reviewList.collectAsState().value
    val similarMovies = viewModel.similarMovies.collectAsState().value
    val currentUserId = firebaseUser?.uid
    var showAllTmdbReviews by remember { mutableStateOf(false) }

    // Verileri çek (sadece bir kez)
    LaunchedEffect(movieId) {
        viewModel.fetchMovieDetail(movieId)
        viewModel.fetchMovieVideos(movieId)
        viewModel.fetchCast(movieId)
        viewModel.fetchReviews(movieId)
        viewModel.fetchSimilarMovies(movieId)
        favoriteViewModel.loadFavorites()
        reviewViewModel.fetchReviews(movieId)
        movie?.let { profileViewModel.saveViewedMovie(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = movie?.title ?: "Film Detayı") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    movie?.let {
                        IconButton(onClick = {
                            if (isFavorite) {
                                favoriteViewModel.removeFavorite(it.id)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Favorilerden çıkarıldı")
                                }
                            } else {
                                favoriteViewModel.addFavorite(it)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Favorilere eklendi")
                                }
                            }
                        }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favori",
                                tint = if (isFavorite) Color.Red else Color.Gray
                            )
                        }
                        IconButton(onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "🎬 ${movie.title}\nhttps://www.themoviedb.org/movie/${movie.id}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Filmi paylaş"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Paylaş")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFEAF6FF)
    ) { innerPadding ->
        movie?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // BACKDROP
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${movie.backdrop_path}"),
                        contentDescription = "Backdrop",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                )
                            )
                    )
                    Text(
                        text = movie.title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // IMDB PUANI
                Text("IMDB Puanı", style = MaterialTheme.typography.labelLarge)
                LinearProgressIndicator(
                    progress = (movie.vote_average?.toFloat() ?: 0f) / 10f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(50)),
                    color = Color(0xFF1E88E5),
                    trackColor = Color(0xFFBBDEFB)
                )
                val formattedRating = DecimalFormat("#.#").format(movie.vote_average ?: 0.0)
                Text(
                    text = "$formattedRating/10",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                    color = Color.DarkGray
                )
                Text(
                    text = "Yayın Tarihi: ${movie.release_date ?: "-"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = movie.overview ?: "Açıklama bulunamadı.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(16.dp))

                // FRAGMAN
                if (videos.isNotEmpty()) {
                    val trailer = videos.first()
                    val videoUrl = "https://www.youtube.com/embed/${trailer.key}"
                    AndroidView(
                        factory = {
                            WebView(it).apply {
                                settings.javaScriptEnabled = true
                                loadUrl(videoUrl)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Text(
                        text = "Fragman bulunamadı.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // OYUNCULAR
                if (castList.isNotEmpty()) {
                    Text(
                        text = "Oyuncular",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(castList.take(10)) { cast ->
                            CastItem(cast = cast)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // TMDB YORUMLARI
                Text("TMDB Yorumları", style = MaterialTheme.typography.titleMedium)

                if (reviewList.isNotEmpty()) {
                    val reviewsToShow = if (showAllTmdbReviews) reviewList else reviewList.take(2)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        reviewsToShow.forEach { review ->
                            ReviewCard(review)
                        }

                        if (!showAllTmdbReviews && reviewList.size > 2) {
                            Text(
                                text = "Devamını Gör",
                                color = Color(0xFF1E88E5),
                                modifier = Modifier
                                    .clickable { showAllTmdbReviews = true }
                                    .padding(top = 8.dp),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                } else {
                    Text("TMDB'den yorum bulunamadı.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }


                Spacer(modifier = Modifier.height(24.dp))

                // FIREBASE YORUMLARI
                Text("Kullanıcı Yorumları", style = MaterialTheme.typography.titleMedium)
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(12.dp))
                } else {
                    if (userReviews.isEmpty()) {
                        Text("Henüz kullanıcı yorumu yok.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            userReviews.forEach { review ->
                                MovieReviewCard(
                                    review = review,
                                    currentUserId = currentUserId,
                                    onDelete = { id ->
                                        reviewViewModel.deleteReview(id, movieId) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Yorum silindi")
                                            }
                                        }
                                    },
                                    onEdit = { id, newText ->
                                        reviewViewModel.updateReview(id, newText, movieId) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Yorum güncellendi")
                                            }
                                        }
                                    }
                                )
                            }
                        }

                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoggedIn) {
                    OutlinedTextField(
                        value = reviewText.value,
                        onValueChange = { reviewText.value = it },
                        placeholder = { Text("Yorum yaz...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (reviewText.value.text.isNotBlank()) {
                                reviewViewModel.submitReview(movieId, reviewText.value.text) {
                                    reviewText.value = TextFieldValue("")
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Yorum gönderildi")
                                    }
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        enabled = reviewText.value.text.isNotBlank()
                    ) {
                        Text("Gönder")
                    }
                } else {
                    Text("Yorum yapabilmek için giriş yapmalısınız.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // BENZER FİLMLER
                if (similarMovies.isNotEmpty()) {
                    Text("Benzer Filmler", style = MaterialTheme.typography.titleMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(similarMovies.take(10)) { similar ->
                            Card(
                                modifier = Modifier
                                    .width(140.dp)
                                    .clickable {
                                        navController.navigate("movie_detail/${similar.id}")
                                    },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column {
                                    Image(
                                        painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${similar.poster_path}"),
                                        contentDescription = similar.title,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(
                                        text = similar.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun ReviewCard(review: Review) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = review.author,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF1E88E5)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (review.content.length > 200) review.content.take(200) + "..." else review.content,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
@Composable
fun MovieReviewCard(
    review: MovieReview,
    currentUserId: String?,
    onDelete: (String) -> Unit,
    onEdit: (String, String) -> Unit
) {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val formattedDate = formatter.format(Date(review.timestamp))

    var showEditDialog by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(TextFieldValue("")) }

    // Yorum kutusu her açıldığında içerik güncellenir
    LaunchedEffect(showEditDialog) {
        if (showEditDialog) {
            editText = TextFieldValue(review.content)
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
                editText = TextFieldValue("")
            },
            confirmButton = {
                TextButton(onClick = {
                    onEdit(review.id, editText.text)
                    showEditDialog = false
                    editText = TextFieldValue("")
                }) { Text("Kaydet") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditDialog = false
                    editText = TextFieldValue("")
                }) { Text("İptal") }
            },
            title = { Text("Yorumu Düzenle") },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it }, // 🔥 kritik düzeltme
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = review.userEmail,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1E88E5)
                    )
                    Text(
                        text = "$formattedDate${if (review.isEdited) " (Düzenlendi)" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                if (review.userId == currentUserId) {
                    Row {
                        IconButton(onClick = {
                            showEditDialog = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Düzenle", tint = Color.DarkGray)
                        }
                        IconButton(onClick = {
                            onDelete(review.id)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Sil", tint = Color.Red)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = review.content,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


@Composable
fun CastItem(cast: Cast) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${cast.profile_path}"),
            contentDescription = cast.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )
        Text(
            text = cast.name,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
        Text(
            text = cast.character,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
