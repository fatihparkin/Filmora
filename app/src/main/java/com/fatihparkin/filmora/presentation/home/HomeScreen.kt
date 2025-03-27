package com.fatihparkin.filmora.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    // LiveData'yı gözlemlemek için observeAsState
    val movieResponse = homeViewModel.movieResponse.collectAsState(initial = null)
    val errorMessage = homeViewModel.errorMessage.collectAsState(initial = null)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        errorMessage.value?.let {
            Text(text = it)
        }

        // Film varsa göster
        movieResponse.value?.let { response ->
            response.results?.forEach { movie ->
                Text(text = movie.title ?: "No Title")
            }
        } ?: run {
            Text(text = "Loading...")
        }
    }
}
