package com.fatihparkin.filmora.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    val currentSortOption by homeViewModel.currentSortOption.collectAsState()

    var expanded by remember { mutableStateOf(false) }

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
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onNavigateToGenres,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text(text = "🎬 Kategoriler")
                }

                Button(
                    onClick = onNavigateToFavorites,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text(text = "⭐ Favoriler")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sıralama başlığı
            Text(
                text = "Sıralama Seç:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Sıralama Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = currentSortOption?.displayName ?: "Varsayılan",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Sıralama Seç")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    SortOption.values().forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.displayName) },
                            onClick = {
                                homeViewModel.sortMovies(option)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            errorMessage.value?.let {
                Text(text = it, color = Color.Red)
            }

            movieResponse.value?.results?.let { movies ->
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(movies) { movie ->
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
