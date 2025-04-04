package com.fatihparkin.filmora.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.fatihparkin.filmora.data.model.Movie

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onNavigateToGenres: () -> Unit,
    onMovieClick: (Int) -> Unit
) {
    val movieResponse = homeViewModel.movieResponse.collectAsState(initial = null)
    val errorMessage = homeViewModel.errorMessage.collectAsState(initial = null)

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Button(
            onClick = onNavigateToGenres,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "🎬 Kategorilere Göz At")
        }

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage.value?.let {
            Text(text = it, color = Color.Red)
        }

        movieResponse.value?.results?.let { movies ->
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(movies) { movie ->
                    MovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
                }
            }
        } ?: run {
            Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
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
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = movie.title ?: "No Title",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = movie.overview ?: "No overview available",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Release: ${movie.release_date ?: "Unknown"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}
