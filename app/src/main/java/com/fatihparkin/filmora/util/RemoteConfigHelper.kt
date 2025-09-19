package com.fatihparkin.filmora.util

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

object RemoteConfigHelper {

    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    init {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // 1 saat cache süresi
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.setDefaultsAsync(
            mapOf("api_key" to "default_dummy_key") // Fallback değeri
        )

        // Verileri çek (async olarak)
        remoteConfig.fetchAndActivate()
    }

    fun getApiKey(): String {
        return remoteConfig.getString("api_key")
    }
}
