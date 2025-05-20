// 🔽 SATIR 1
package com.fatihparkin.filmora.presentation.genre

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.fatihparkin.filmora.data.model.Movie
import com.fatihparkin.filmora.presentation.favorite.viewmodel.FavoriteViewModel
import com.fatihparkin.filmora.presentation.home.SortOption
import com.fatihparkin.filmora.presentation.home.FilterOption
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreMoviesScreen(
    genreId: Int,
    genreName: String,
    viewModel: GenreViewModel,
    navController: NavController,
    onMovieClick: (Int) -> Unit,
    onNavigateToFavorites: () -> Unit // ⭐ yeni eklendi
) {
    val movies = viewModel.moviesByGenre.collectAsState().value
    val errorMessage = viewModel.errorMessage.collectAsState().value
    val currentSortOption = viewModel.currentSortOption.collectAsState().value
    val selectedFilters = viewModel.selectedFilters.collectAsState().value

    val favoriteViewModel: FavoriteViewModel = hiltViewModel()
    val favoriteMovies = favoriteViewModel.favoriteMovies.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedSegment by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(genreId) {
        viewModel.fetchMoviesByGenre(genreId)
        favoriteViewModel.loadFavorites()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(genreName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favoriler")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Film ara...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // 🔽 Menü Butonları
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = {
                    selectedSegment = if (selectedSegment == "Filtre") null else "Filtre"
                }) {
                    Icon(Icons.Default.Tune, contentDescription = "Filtrele")
                }
                IconButton(onClick = {
                    selectedSegment = if (selectedSegment == "Sırala") null else "Sırala"
                }) {
                    Icon(Icons.Default.Sort, contentDescription = "Sırala")
                }
                IconButton(onClick = {
                    onNavigateToFavorites()
                    selectedSegment = null
                }) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Favoriler")
                }
            }

            when (selectedSegment) {
                "Filtre" -> {
                    Column {
                        FilterOption.values().forEach { filter ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val newFilters = selectedFilters.toMutableList()
                                        if (newFilters.contains(filter)) newFilters.remove(filter) else newFilters.add(filter)
                                        viewModel.updateFilters(newFilters)
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedFilters.contains(filter),
                                    onCheckedChange = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(filter.displayName)
                            }
                        }
                    }
                }
                "Sırala" -> {
                    Column {
                        SortOption.values().forEach { option ->
                            TextButton(
                                onClick = {
                                    viewModel.sortMovies(option)
                                    selectedSegment = null
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(option.displayName)
                            }
                        }
                        TextButton(
                            onClick = {
                                viewModel.resetSorting()
                                selectedSegment = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Varsayılan")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            errorMessage?.let {
                Text(text = it, color = Color.Red)
            }

            val filteredMovies = if (searchQuery.text.isEmpty()) {
                movies
            } else {
                movies.filter { it.title.contains(searchQuery.text, ignoreCase = true) }
            }

            if (filteredMovies.isEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Aradığınız film bulunamadı.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredMovies) { movie ->
                        val isFavorite = favoriteMovies.value.any { it.id == movie.id }
                        GenreMovieItem(
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
        }
    }
}

@Composable
fun GenreMovieItem(
    movie: Movie,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = "https://image.tmdb.org/t/p/w500${movie.poster_path}"),
                contentDescription = movie.title,
                modifier = Modifier
                    .width(120.dp)
                    .height(160.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Çıkış Tarihi: ${movie.release_date}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = { onFavoriteClick() },
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
