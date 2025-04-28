package com.fatihparkin.filmora.presentation.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var errorMessage = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)

    fun login(onSuccess: () -> Unit) {
        val emailValue = email.value.trim()
        val passwordValue = password.value.trim()

        if (emailValue.isEmpty() || passwordValue.isEmpty()) {
            errorMessage.value = "Email ve şifre boş bırakılamaz"
            return
        }

        isLoading.value = true
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener { task ->
                    isLoading.value = false
                    if (task.isSuccessful) {
                        errorMessage.value = null
                        onSuccess()
                    } else {
                        errorMessage.value = task.exception?.localizedMessage ?: "Giriş başarısız oldu"
                    }
                }
        }
    }
}
