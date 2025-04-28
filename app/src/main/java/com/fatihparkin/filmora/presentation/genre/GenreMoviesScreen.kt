package com.fatihparkin.filmora.presentation.genre

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.fatihparkin.filmora.data.model.Movie
import com.fatihparkin.filmora.presentation.favorite.viewmodel.FavoriteViewModel

@Composable
fun GenreMoviesScreen(
    genreId: Int,
    genreName: String,
    viewModel: GenreViewModel,
    navController: NavController,
    onMovieClick: (Int) -> Unit,
    favoriteViewModel: FavoriteViewModel = hiltViewModel()
) {
    val movies = viewModel.moviesByGenre.collectAsState().value
    val error = viewModel.errorMessage.collectAsState().value

    LaunchedEffect(genreId) {
        viewModel.fetchMoviesByGenre(genreId)
        favoriteViewModel.loadFavorites()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = genreName,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        error?.let {
            Text(text = it, color = Color.Red)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(movies) { movie ->
                GenreMovieCard(
                    movie = movie,
                    onClick = { onMovieClick(movie.id) },
                    isFavorite = favoriteViewModel.favoriteMovies.collectAsState().value.any { it.id == movie.id },
                    onToggleFavorite = {
                        if (favoriteViewModel.favoriteMovies.value.any { it.id == movie.id }) {
                            favoriteViewModel.removeFavorite(movie.id)
                        } else {
                            favoriteViewModel.addFavorite(movie)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun GenreMovieCard(
    movie: Movie,
    onClick: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${movie.poster_path}"),
                contentDescription = movie.title,
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxHeight()
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
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Release: ${movie.release_date}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )

                    IconButton(onClick = { onToggleFavorite() }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favori",
                            tint = if (isFavorite) Color.Red else Color.Gray
                        )
                    }
                }
            }
        }
    }
}
