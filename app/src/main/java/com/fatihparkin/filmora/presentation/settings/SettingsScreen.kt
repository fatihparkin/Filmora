package com.fatihparkin.filmora.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = SettingsViewModel()) {
    val context = LocalContext.current

    var isNotificationsEnabled by rememberSaveable { mutableStateOf(false) }
    var isDarkModeEnabled by rememberSaveable { mutableStateOf(false) }
    var faqExpanded by rememberSaveable { mutableStateOf(false) }
    var contactExpanded by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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

        // SSS
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

        // İletişim
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
}
