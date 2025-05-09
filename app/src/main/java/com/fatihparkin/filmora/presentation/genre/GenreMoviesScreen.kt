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
    onMovieClick: (Int) -> Unit
) {
    val movies = viewModel.moviesByGenre.collectAsState().value
    val errorMessage = viewModel.errorMessage.collectAsState().value
    val currentSortOption = viewModel.currentSortOption.collectAsState().value
    val selectedFilters = viewModel.selectedFilters.collectAsState().value

    val favoriteViewModel: FavoriteViewModel = hiltViewModel()
    val favoriteMovies = favoriteViewModel.favoriteMovies.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var expandedSort by remember { mutableStateOf(false) }
    var expandedFilter by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(genreId) {
        viewModel.fetchMoviesByGenre(genreId)
        favoriteViewModel.loadFavorites()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(genreName, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
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
            // Arama Alanı
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

            // Sıralama ve Filtreleme Alanı
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Sırala: ", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(8.dp))

                Box {
                    Button(
                        onClick = { expandedSort = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = currentSortOption?.displayName ?: "Varsayılan", color = Color.White)
                    }
                    DropdownMenu(
                        expanded = expandedSort,
                        onDismissRequest = { expandedSort = false }
                    ) {
                        SortOption.values().forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.displayName) },
                                onClick = {
                                    expandedSort = false
                                    viewModel.sortMovies(option)
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Varsayılan") },
                            onClick = {
                                expandedSort = false
                                viewModel.fetchMoviesByGenre(genreId)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box {
                    Button(
                        onClick = { expandedFilter = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(text = "Filtrele", color = Color.White)
                    }
                    DropdownMenu(
                        expanded = expandedFilter,
                        onDismissRequest = { expandedFilter = false }
                    ) {
                        FilterOption.values().forEach { filterOption ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = selectedFilters.contains(filterOption),
                                            onCheckedChange = null
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(filterOption.displayName)
                                    }
                                },
                                onClick = {
                                    val newFilters = selectedFilters.toMutableList()
                                    if (newFilters.contains(filterOption)) {
                                        newFilters.remove(filterOption)
                                    } else {
                                        newFilters.add(filterOption)
                                    }
                                    viewModel.updateFilters(newFilters)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            errorMessage?.let {
                Text(text = it, color = Color.Red)
            }

            // Film Listesi
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
