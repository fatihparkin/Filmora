@file:OptIn(ExperimentalMaterial3Api::class)

package com.fatihparkin.filmora.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.fatihparkin.filmora.data.model.Movie
import com.fatihparkin.filmora.presentation.profile.viewmodel.ProfileViewModel
import com.fatihparkin.filmora.presentation.navigation.ScreenRoutes
import com.fatihparkin.filmora.util.NetworkUtils
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val isConnected = NetworkUtils.isNetworkAvailable(context)
    val viewedMovies by viewModel.viewedMovies.collectAsState()
    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        if (isConnected) {
            viewModel.fetchViewedMovies()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        if (!isConnected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAEAEA)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Profil bilgilerini görüntülemek için internet bağlantınızı açın.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp),
                        color = Color.Red
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "👤 Kullanıcı: ${user?.email ?: "Bilinmiyor"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("❤️ Toplam Favori Sayısı", fontWeight = FontWeight.Medium)
                        Text("(Bu özellik ileride eklenecek)", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Text(
                    text = "Son Göz Atılan Filmler",
                    style = MaterialTheme.typography.titleMedium
                )

                if (viewedMovies.isEmpty()) {
                    Text(
                        text = "Henüz hiçbir filme göz atmadınız.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(viewedMovies) { movie ->
                            ViewedMovieItem(movie)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(ScreenRoutes.LOGIN) {
                            popUpTo(0)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Çıkış Yap", color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    }
}

@Composable
fun ViewedMovieItem(movie: Movie) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .height(220.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${movie.poster_path}"),
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            )
            Text(
                text = movie.title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
