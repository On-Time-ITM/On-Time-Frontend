package com.example.ontime.ui.auth.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ontime.data.api.AuthApi
import com.example.ontime.data.api.FcmApi
import com.example.ontime.data.auth.AuthManager
import com.example.ontime.data.model.request.FcmTokenRequest
import com.example.ontime.data.model.request.LoginRequest
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val authManager: AuthManager,
    private val fcmApi: FcmApi,
    @ApplicationContext private val context: Context
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
//    private fun login(phoneNumber: String, password: String) {
//        viewModelScope.launch {
//            try {
//                _loginState.value = LoginState.Loading
//                isLoading = true
//
//                val request = LoginRequest(phoneNumber, password)
//                val response = authApi.login(request)
//                Log.d("ITM", "Login Request: $request")
//
//                if (response.isSuccessful) {
//                    response.body()?.let { loginResponse ->
//                        authManager.saveAuthInfo(
//                            userInfo = loginResponse.userInfo,
//                            tokenInfo = loginResponse.tokenInfo
//                        )
//                        Log.d("ITM", "Login Response: $loginResponse")
//
//                        // FCM 토큰 전송 로직 시작
//                        Log.d("ITM", "Starting FCM token registration...")
//                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//                            Log.d("ITM", "FCM token task completed")
//                            if (task.isSuccessful) {
//                                val fcmToken = task.result
//                                Log.d("ITM", "Got FCM token: $fcmToken")
//                                val userId = authManager.getUserId()
//                                Log.d("ITM", "Current userId: $userId")
//
//                                viewModelScope.launch {
//                                    try {
//                                        val fcmRequest = FcmTokenRequest(userId!!, fcmToken)
//                                        Log.d("ITM", "Sending FCM token request: $fcmRequest")
//
//                                        val fcmResponse = fcmApi.saveToken(fcmRequest)
//                                        if (fcmResponse.isSuccessful) {
//                                            Log.d("ITM", "FCM token successfully sent to server")
//                                        } else {
//                                            Log.d(
//                                                "ITM",
//                                                "FCM token send failed: ${fcmResponse.code()}"
//                                            )
//                                            Log.d(
//                                                "ITM",
//                                                "Error body: ${fcmResponse.errorBody()?.string()}"
//                                            )
//                                        }
//                                    } catch (e: Exception) {
//                                        Log.d("ITM", "Error sending FCM token", e)
//                                        e.printStackTrace()
//                                    }
//                                }
//                            } else {
//                                Log.d("ITM", "Failed to get FCM token", task.exception)
//                            }
//                        }
//                    }
//                    _loginState.value = LoginState.Success
//                } else {
//                    _loginState.value = LoginState.Error("Login failed")
//                    val errorBody = response.errorBody()?.string()
//                    Log.d("ITM", "Login Status Code: ${response.code()}")
//                    Log.d("ITM", "Login Error Body: $errorBody")
//                }
//            } catch (e: Exception) {
//                _loginState.value = LoginState.Error(e.message ?: "Unknown error")
//                Log.d("ITM", "Login error", e)
//            } finally {
//                isLoading = false
//            }
//        }
//    }

    private val tokenPrefs = context.getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)

    private fun login(phoneNumber: String, password: String) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading
                isLoading = true

                val request = LoginRequest(phoneNumber, password)
                val response = authApi.login(request)
                Log.d("ITM", "Login Request: $request")

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        authManager.saveAuthInfo(
                            userInfo = loginResponse.userInfo,
                            tokenInfo = loginResponse.tokenInfo
                        )
                        Log.d("ITM", "Login Response: $loginResponse")

                        // 저장된 FCM 토큰 확인
                        val savedToken = tokenPrefs.getString("fcm_token", null)
                        Log.d("ITM", "Saved FCM token: $savedToken")

                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val newToken = task.result
                                // 새 토큰 저장
                                tokenPrefs.edit().putString("fcm_token", newToken).apply()
                                Log.d("ITM", "New FCM token saved: $newToken")

                                viewModelScope.launch {
                                    sendTokenToServer(loginResponse.userInfo.id, newToken)
                                }
                            } else {
                                Log.d("ITM", "xxxx")
                            }
                        }
                    }
                    _loginState.value = LoginState.Success
                } else {
//                    handleLoginError(response)
                }
            } catch (e: Exception) {
//                handleLoginException(e)
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun sendTokenToServer(userId: String, token: String) {
        try {
            val fcmRequest = FcmTokenRequest(userId, token)
            Log.d("ITM", "Sending FCM token request: $fcmRequest")

            val fcmResponse = fcmApi.saveToken(fcmRequest)
            if (fcmResponse.isSuccessful) {
                Log.d("ITM", "FCM token successfully sent to server")
            } else {
                Log.d("ITM", "FCM token send failed: ${fcmResponse.code()}")
                Log.d("ITM", "Error body: ${fcmResponse.errorBody()?.string()}")

                // 실패 시 재시도를 위한 플래그 설정
                tokenPrefs.edit().putBoolean("token_sync_needed", true).apply()
            }
        } catch (e: Exception) {
            Log.d("ITM", "Error sending FCM token", e)
            tokenPrefs.edit().putBoolean("token_sync_needed", true).apply()
            e.printStackTrace()
        }
    }

    private fun validatePhoneNumber(phone: String): String? {
        return when {
            phone.isEmpty() -> "Phone number is required"
//            !phone.matches(Regex("^\\d{10,11}$")) -> "Invalid phone number format"
            !phone.matches(Regex("^01[0-9]-\\d{4}-\\d{4}$")) -> "Invalid phone number format"
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