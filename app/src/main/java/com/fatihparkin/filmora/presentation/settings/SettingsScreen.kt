@file:OptIn(ExperimentalMaterial3Api::class)

package com.fatihparkin.filmora.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fatihparkin.filmora.presentation.navigation.ScreenRoutes
import com.fatihparkin.filmora.util.NetworkUtils
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = SettingsViewModel()
) {
    val context = LocalContext.current
    val isConnected = remember { NetworkUtils.isNetworkAvailable(context) }

    var isNotificationsEnabled by rememberSaveable { mutableStateOf(false) }
    var isDarkModeEnabled by rememberSaveable { mutableStateOf(false) }
    var faqExpanded by rememberSaveable { mutableStateOf(false) }
    var contactExpanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayarlar") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri Dön")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (!isConnected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "⚠️ Ayarlar sayfasını görüntülemek için internet bağlantısı gereklidir.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            else {
                // Normal içerik
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        Text(
                            text = "Ayarlar",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text("🔔 Bildirimler", modifier = Modifier.weight(1f))
                            Switch(
                                checked = isNotificationsEnabled,
                                onCheckedChange = {
                                    isNotificationsEnabled = it
                                    viewModel.showToast(context, if (it) "Bildirim açıldı" else "Bildirim kapatıldı")
                                }
                            )
                        }
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text("🌙 Karanlık Mod", modifier = Modifier.weight(1f))
                            Switch(
                                checked = isDarkModeEnabled,
                                onCheckedChange = {
                                    isDarkModeEnabled = it
                                    viewModel.showToast(context, if (it) "Karanlık mod aktif" else "Karanlık mod pasif")
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (faqExpanded) "❓ SSS (kapat)" else "❓ SSS (aç)",
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { faqExpanded = !faqExpanded }
                                .padding(vertical = 8.dp)
                        )
                    }

                    if (faqExpanded) {
                        item {
                            Column(modifier = Modifier.padding(start = 12.dp)) {
                                Text("• Uygulama ne işe yarar?\nFilm önerileri sunar ve detaylarını gösterir.")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("• İnternetsiz çalışır mı?\nFavoriler kısmı için evet, diğerleri için hayır.")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("• Giriş yapmadan kullanılabilir mi?\nEvet, giriş zorunlu değildir.")
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = if (contactExpanded) "📬 Bize Ulaşın (kapat)" else "📬 Bize Ulaşın (aç)",
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { contactExpanded = !contactExpanded }
                                .padding(vertical = 8.dp)
                        )
                    }

                    if (contactExpanded) {
                        item {
                            Column(modifier = Modifier.padding(start = 12.dp)) {
                                Text(
                                    text = "🐱 GitHub: fatihparkin",
                                    modifier = Modifier
                                        .clickable {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fatihparkin"))
                                            context.startActivity(intent)
                                        }
                                        .padding(vertical = 6.dp)
                                )
                                Text(
                                    text = "📸 Instagram: fatihparkin",
                                    modifier = Modifier
                                        .clickable {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/fatihparkin"))
                                            context.startActivity(intent)
                                        }
                                        .padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text("Çıkış Yap", color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    }
}
