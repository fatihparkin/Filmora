package com.fatihparkin.filmora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.fatihparkin.filmora.presentation.home.HomeViewModel
import com.fatihparkin.filmora.presentation.navigation.FilmoraNavGraph
import com.fatihparkin.filmora.ui.theme.FilmoraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 Popüler filmleri çekiyoruz
        homeViewModel.fetchPopularMovies(context = this)

        setContent {
            FilmoraTheme {
                val navController = rememberNavController()
                Surface(modifier = Modifier) {
                    FilmoraNavGraph(
                        navController = navController,
                        homeViewModel = homeViewModel // viewModel’i gönderiyoruz
                    )
                }
            }
        }
    }
}
