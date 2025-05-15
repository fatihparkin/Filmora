@file:OptIn(ExperimentalMaterial3Api::class)

package com.fatihparkin.filmora.presentation.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.fatihparkin.filmora.data.model.Cast
import com.fatihparkin.filmora.data.model.Movie
import com.fatihparkin.filmora.data.model.Review
import com.fatihparkin.filmora.presentation.favorite.viewmodel.FavoriteViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@Composable
fun MovieDetailScreen(
    movieId: Int,
    viewModel: MovieDetailViewModel,
    navController: NavController,
    favoriteViewModel: FavoriteViewModel = hiltViewModel()
) {
    val movie = viewModel.movieDetail.collectAsState().value
    val videos = viewModel.videoList.collectAsState().value
    val castList = viewModel.castList.collectAsState().value
    val reviewList = viewModel.reviewList.collectAsState().value
    val similarMovies = viewModel.similarMovies.collectAsState().value
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val favoriteMovies = favoriteViewModel.favoriteMovies.collectAsState()
    val isFavorite = favoriteMovies.value.any { it.id == movie?.id }

    LaunchedEffect(movieId) {
        viewModel.fetchMovieDetail(movieId)
        viewModel.fetchMovieVideos(movieId)
        viewModel.fetchCast(movieId)
        viewModel.fetchReviews(movieId)
        viewModel.fetchSimilarMovies(movieId)
        favoriteViewModel.loadFavorites()
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
                        // Favori ikonu
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

                        // Paylaş ikonu
                        IconButton(onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "🎬 ${movie.title}\nhttps://www.themoviedb.org/movie/${movie.id}"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Filmi paylaş"))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Paylaş"
                            )
                        }
                    }
                }

            )
        },
        containerColor = Color(0xFFEAF6FF),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        movie?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // Backdrop
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

                // IMDB
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

                // Fragman Embed
                if (videos.isNotEmpty()) {
                    val trailer = videos.first()
                    val videoUrl = "https://www.youtube.com/embed/${trailer.key}"

                    AndroidView(
                        factory = {
                            android.webkit.WebView(it).apply {
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

                // Oyuncular
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
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Yorumlar
                Text(
                    text = "Yorumlar",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (reviewList.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        reviewList.take(3).forEach { review ->
                            ReviewCard(review)
                        }
                    }
                } else {
                    Text(
                        text = "Henüz bu film için bir yorum yapılmamış.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Benzer Filmler
                if (similarMovies.isNotEmpty()) {
                    Text(
                        text = "Benzer Filmler",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
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
                text = review.content,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
