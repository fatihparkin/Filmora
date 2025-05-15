package com.fatihparkin.filmora.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import kotlinx.coroutines.flow.MutableStateFlow

object ConnectivityState {
    val isConnected = MutableStateFlow(false)
}

class ConnectivityReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        ConnectivityState.isConnected.value = activeNetwork?.isConnectedOrConnecting == true
    }
}
