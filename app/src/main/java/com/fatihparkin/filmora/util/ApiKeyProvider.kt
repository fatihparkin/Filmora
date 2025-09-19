package com.fatihparkin.filmora.util

object ApiKeyProvider {
    fun getApiKey(): String {
        return RemoteConfigHelper.getApiKey()
    }
}
