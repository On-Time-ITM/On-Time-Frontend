package com.example.ontime.ui.auth.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ontime.data.api.AuthApi
import com.example.ontime.data.auth.AuthManager
import com.example.ontime.data.model.request.LoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val authManager: AuthManager
) : ViewModel() {
    var phoneNumber by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var phoneNumberError by mutableStateOf<String?>(null)
        private set

    var passwordError by mutableStateOf<String?>(null)
        private set

    fun onPhoneNumberChanged(newValue: String) {
        phoneNumber = newValue
        phoneNumberError = validatePhoneNumber(newValue)
    }

    fun onPasswordChanged(newValue: String) {
        password = newValue
        passwordError = validatePassword(newValue)
    }

    // 로그인 상태 관리
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private fun login(phoneNumber: String, password: String) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading
                isLoading = true

                val request = LoginRequest(phoneNumber, password)
                val response = authApi.login(request)
                Log.d("ITM", "Request: ${request}") // 실제 전송되는 요청 데이터 확인


                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        authManager.saveAuthInfo(
                            userInfo = loginResponse.userInfo,
                            tokenInfo = loginResponse.tokenInfo
                        )
                        Log.d("ITM", "$loginResponse")
                    }
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Login failed")
                    val errorBody = response.errorBody()?.string()
                    Log.d("ITM", "Status Code: ${response.code()}")
                    Log.d("ITM", "Error Body: $errorBody")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unknown error")
            } finally {
                isLoading = false
            }
        }
    }


    private fun validatePhoneNumber(phone: String): String? {
        return when {
            phone.isEmpty() -> "Phone number is required"
            !phone.matches(Regex("^\\d{10,11}$")) -> "Invalid phone number format"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "Password is required"
            password.length < 5 -> "Password must be at least 8 characters"
            else -> null
        }
    }

    fun onLoginClick() {
        val phoneNumberError = validatePhoneNumber(phoneNumber)
        val passwordError = validatePassword(password)

        this.phoneNumberError = phoneNumberError
        this.passwordError = passwordError

        if (phoneNumberError == null && passwordError == null) {
            login(phoneNumber, password)
        }
    }


}

sealed class LoginState {
    object Initial : LoginState()    // 초기 상태
    object Loading : LoginState()    // 로딩 중
    object Success : LoginState()    // 로그인 성공
    data class Error(val message: String) : LoginState()  // 로그인 실패
}