package com.fatihparkin.filmora.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.fatihparkin.filmora.data.model.Movie
import com.fatihparkin.filmora.presentation.favorite.viewmodel.FavoriteViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onNavigateToGenres: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    val movieResponse = homeViewModel.movieResponse.collectAsState(initial = null)
    val errorMessage = homeViewModel.errorMessage.collectAsState(initial = null)
    val favoriteViewModel: FavoriteViewModel = hiltViewModel()
    val favoriteMovies = favoriteViewModel.favoriteMovies.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        favoriteViewModel.loadFavorites()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filmora") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Ayarlar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            //  Arama Alanı
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Film ara...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Ara")
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            //  Kategoriler Butonu
            Button(
                onClick = onNavigateToGenres,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "🎬 Kategorilere Göz At")
            }

            Spacer(modifier = Modifier.height(8.dp))

            //  Favoriler Butonu
            Button(
                onClick = onNavigateToFavorites,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "⭐ Favorilerim")
            }

            Spacer(modifier = Modifier.height(16.dp))

            errorMessage.value?.let {
                Text(text = it, color = Color.Red)
            }

            movieResponse.value?.results?.let { movies ->
                val filteredMovies = if (searchQuery.text.isEmpty()) {
                    movies
                } else {
                    movies.filter { it.title.contains(searchQuery.text, ignoreCase = true) }
                }

                if (filteredMovies.isEmpty()) {
                    Text(
                        text = "Aradığınız film bulunamadı.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(filteredMovies) { movie ->
                            val isFavorite = favoriteMovies.value.any { it.id == movie.id }
                            MovieCard(
                                movie = movie,
                                isFavorite = isFavorite,
                                onClick = { onMovieClick(movie.id) },
                                onFavoriteClick = {
                                    scope.launch {
                                        if (isFavorite) {
                                            favoriteViewModel.removeFavorite(movie.id)
                                            snackbarHostState.showSnackbar("Favorilerden kaldırıldı")
                                        } else {
                                            favoriteViewModel.addFavorite(movie)
                                            snackbarHostState.showSnackbar("Favorilere eklendi")
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            } ?: run {
                Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun MovieCard(
    movie: Movie,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(model = "https://image.tmdb.org/t/p/w500${movie.poster_path}"),
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(130.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(12.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Release: ${movie.release_date}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favori",
                    tint = if (isFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}
