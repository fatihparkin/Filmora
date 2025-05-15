package com.fatihparkin.filmora

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.fatihparkin.filmora.presentation.home.HomeViewModel
import com.fatihparkin.filmora.presentation.navigation.FilmoraNavGraph
import com.fatihparkin.filmora.ui.theme.FilmoraTheme
import com.fatihparkin.filmora.util.ConnectivityReceiver
import com.fatihparkin.filmora.util.ConnectivityState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val connectivityReceiver = ConnectivityReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 Popüler filmleri çekiyoruz
        homeViewModel.fetchPopularMovies(context = this)

        setContent {
            FilmoraTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                // 🔁 İnternet bağlantısı değişimini dinle
                LaunchedEffect(Unit) {
                    ConnectivityState.isConnected.collectLatest { isConnected ->
                        if (isConnected) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "İnternet bağlantısı sağlandı. Giriş ekranına yönlendiriliyorsunuz...",
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Short
                                )
                                delay(1500)
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }
                }

                // 📡 BroadcastReceiver’ı register et
                DisposableEffect(Unit) {
                    val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                    registerReceiver(connectivityReceiver, filter)
                    onDispose {
                        unregisterReceiver(connectivityReceiver)
                    }
                }

                // 🎯 Uygulama içeriği
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { paddingValues ->
                    Surface(modifier = Modifier.padding(paddingValues)) {
                        FilmoraNavGraph(
                            navController = navController,
                            homeViewModel = homeViewModel
                        )
                    }
                }
            }
        }
    }
}
