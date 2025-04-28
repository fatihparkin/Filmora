package com.fatihparkin.filmora.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fatihparkin.filmora.presentation.login.LoginScreen
import com.fatihparkin.filmora.presentation.register.RegisterScreen
import com.fatihparkin.filmora.presentation.detail.MovieDetailScreen
import com.fatihparkin.filmora.presentation.detail.MovieDetailViewModel
import com.fatihparkin.filmora.presentation.genre.GenreMoviesScreen
import com.fatihparkin.filmora.presentation.genre.GenreScreen
import com.fatihparkin.filmora.presentation.genre.GenreViewModel
import com.fatihparkin.filmora.presentation.home.HomeScreen
import com.fatihparkin.filmora.presentation.home.HomeViewModel
import com.fatihparkin.filmora.presentation.settings.SettingsScreen


object ScreenRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register" // Eklendi
    const val HOME = "home"
    const val GENRES = "genres"
    const val GENRE_MOVIES = "genre_movies"
    const val MOVIE_DETAIL = "movie_detail"
    const val SETTINGS = "settings"
}

@Composable
fun FilmoraNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.LOGIN
    ) {

        // Giriş Sayfası
        composable(ScreenRoutes.LOGIN) {
            LoginScreen(navController = navController)
        }

        // Kayıt Sayfası
        composable(ScreenRoutes.REGISTER) {
            RegisterScreen(navController = navController)
        }

        // Ana Sayfa
        composable(ScreenRoutes.HOME) {
            HomeScreen(
                homeViewModel = homeViewModel,
                onNavigateToGenres = {
                    navController.navigate(ScreenRoutes.GENRES)
                },
                onMovieClick = { movieId ->
                    navController.navigate("${ScreenRoutes.MOVIE_DETAIL}/$movieId")
                },
                onNavigateToSettings = {
                    navController.navigate(ScreenRoutes.SETTINGS)
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
        composable("${ScreenRoutes.GENRE_MOVIES}/{genreId}/{genreName}") { backStackEntry ->
            val genreId =
                backStackEntry.arguments?.getString("genreId")?.toIntOrNull() ?: return@composable
            val genreName = backStackEntry.arguments?.getString("genreName") ?: return@composable
            val genreViewModel: GenreViewModel = hiltViewModel()

            GenreMoviesScreen(
                genreId = genreId,
                genreName = genreName,
                viewModel = genreViewModel,
                navController = navController,
                onMovieClick = { movieId ->
                    navController.navigate("${ScreenRoutes.MOVIE_DETAIL}/$movieId")
                }
            )
        }

        // Film Detay Sayfası
        composable("${ScreenRoutes.MOVIE_DETAIL}/{movieId}") { backStackEntry ->
            val movieId =
                backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: return@composable
            val viewModel: MovieDetailViewModel = hiltViewModel()

            MovieDetailScreen(
                movieId = movieId,
                viewModel = viewModel,
                navController = navController
            )
        }

        // Ayarlar Sayfası
        composable(ScreenRoutes.SETTINGS) {
            SettingsScreen()
        }
    }
}
