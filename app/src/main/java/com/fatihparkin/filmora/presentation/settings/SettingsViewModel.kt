package com.fatihparkin.filmora.presentation.settings

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
