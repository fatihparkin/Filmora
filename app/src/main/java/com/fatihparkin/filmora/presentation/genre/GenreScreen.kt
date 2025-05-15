@file:OptIn(ExperimentalMaterial3Api::class)

package com.fatihparkin.filmora.presentation.genre

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fatihparkin.filmora.data.model.Genre
import com.fatihparkin.filmora.util.NetworkUtils

@Composable
fun GenreScreen(
    viewModel: GenreViewModel,
    navController: NavController,
    onGenreClick: (genreId: Int, genreName: String) -> Unit
) {
    val context = LocalContext.current
    val genreList = viewModel.genreList.collectAsState().value
    val errorMessage = viewModel.errorMessage.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.fetchGenres()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kategoriler", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri Dön")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF9F9F9),
                            Color(0xFFECEFF1)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            if (!NetworkUtils.isNetworkAvailable(context)) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Kategorileri görüntülemek için internet bağlantınızı açınız.", color = Color.Red)
                }
            } else {
                errorMessage?.let {
                    Text(text = it, color = Color.Red)
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(genreList) { genre ->
                        AnimatedGenreCard(genre = genre, onClick = onGenreClick)
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedGenreCard(
    genre: Genre,
    onClick: (Int, String) -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                pressed = true
                onClick(genre.id, genre.name)
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp)
        ) {
            Text(
                text = genre.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
