package com.fatihparkin.filmora.presentation.genre

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.fatihparkin.filmora.data.model.Movie

@Composable
fun GenreMoviesScreen(
    genreId: Int,
    genreName: String,
    viewModel: GenreViewModel,
    navController: NavController,
    onMovieClick: (Int) -> Unit
) {
    val movies = viewModel.moviesByGenre.collectAsState().value
    val error = viewModel.errorMessage.collectAsState().value

    LaunchedEffect(genreId) {
        viewModel.fetchMoviesByGenre(genreId)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

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
                GenreMovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
            }
        }
    }
}

@Composable
fun GenreMovieCard(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Image(
                painter = rememberAsyncImagePainter(model = "https://image.tmdb.org/t/p/w500${movie.poster_path}"),
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(120.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 4.dp),
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
