package com.fatihparkin.filmora.presentation.genre

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fatihparkin.filmora.data.model.Genre

@Composable
fun GenreScreen(
    viewModel: GenreViewModel,
    onGenreClick: (genreId: Int, genreName: String) -> Unit
) {
    val genreList = viewModel.genreList.collectAsState().value
    val errorMessage = viewModel.errorMessage.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.fetchGenres()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(genreList) { genre ->
                GenreItem(genre = genre, onClick = onGenreClick)
            }
        }
    }
}

@Composable
fun GenreItem(genre: Genre, onClick: (Int, String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable { onClick(genre.id, genre.name) }
            .padding(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = genre.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
