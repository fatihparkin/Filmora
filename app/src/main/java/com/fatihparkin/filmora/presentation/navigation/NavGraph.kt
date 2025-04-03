package com.fatihparkin.filmora.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fatihparkin.filmora.presentation.genre.GenreMoviesScreen
import com.fatihparkin.filmora.presentation.genre.GenreScreen
import com.fatihparkin.filmora.presentation.genre.GenreViewModel
import com.fatihparkin.filmora.presentation.home.HomeScreen
import com.fatihparkin.filmora.presentation.home.HomeViewModel

object ScreenRoutes {
    const val HOME = "home"
    const val GENRES = "genres"
    const val GENRE_MOVIES = "genre_movies"
}

@Composable
fun FilmoraNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.HOME
    ) {

        // Ana Sayfa
        composable(ScreenRoutes.HOME) {
            HomeScreen(
                homeViewModel = homeViewModel,
                onNavigateToGenres = {
                    navController.navigate(ScreenRoutes.GENRES)
                }
            )
        }

        // Kategoriler Sayfası
        composable(ScreenRoutes.GENRES) {
            val genreViewModel: GenreViewModel = hiltViewModel()
            GenreScreen(
                viewModel = genreViewModel,
                onGenreClick = { id, name ->
                    navController.navigate("${ScreenRoutes.GENRE_MOVIES}/$id/$name")
                }
            )
        }

        // Seçilen türe ait filmler
        composable(
            route = "${ScreenRoutes.GENRE_MOVIES}/{genreId}/{genreName}"
        ) { backStackEntry ->
            val genreId = backStackEntry.arguments?.getString("genreId")?.toIntOrNull() ?: return@composable
            val genreName = backStackEntry.arguments?.getString("genreName") ?: return@composable
            val genreViewModel: GenreViewModel = hiltViewModel()

            GenreMoviesScreen(
                genreId = genreId,
                genreName = genreName,
                viewModel = genreViewModel,
                navController = navController
            )
        }
    }
}
