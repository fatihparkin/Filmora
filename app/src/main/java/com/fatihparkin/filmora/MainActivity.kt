package com.fatihparkin.filmora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.fatihparkin.filmora.presentation.home.HomeViewModel
import com.fatihparkin.filmora.ui.theme.FilmoraTheme
import dagger.hilt.android.AndroidEntryPoint
import com.fatihparkin.filmora.presentation.home.HomeScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Hilt ile HomeViewModel'i enjekte ediyoruz
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FilmoraTheme {
                // HomeScreen composable'ını çağırıyoruz ve homeViewModel'i parametre olarak veriyoruz
                HomeScreen(homeViewModel = homeViewModel)
            }
        }

        // Popüler filmleri çekmek için fonksiyonu çağırıyoruz
        homeViewModel.fetchPopularMovies()
    }
}
