package com.example.ontime.ui.auth.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ontime.data.api.AuthApi
import com.example.ontime.data.auth.AuthManager
import com.example.ontime.data.model.request.SignupRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val authManager: AuthManager
) : ViewModel() {
    // 회원가입 진행 상태를 저장하는 StateFlow
    private val _signupState = MutableStateFlow<SignupState>(SignupState.Initial)
    val signupState: StateFlow<SignupState> = _signupState.asStateFlow()

    // 회원가입 API 호출 함수
    fun signup(name: String, phoneNumber: String, password: String) {
        viewModelScope.launch {
            try {
                // 로딩 상태로 변경
                _signupState.value = SignupState.Loading

                val request =
                    SignupRequest(name = name, phoneNumber = phoneNumber, password = password)
                val response = authApi.signup(request)

                if (response.isSuccessful) {
                    response.body()?.let { signupResponse ->
                        authManager.saveAuthInfo(
                            userInfo = signupResponse.userInfo,
                            tokenInfo = signupResponse.tokenInfo
                        )
                    }
                    _signupState.value = SignupState.Success
                    Log.d("ITM", "Status Code: ${response.code()}")
                    Log.d("ITM", "Headers: ${response.headers()}")
                    Log.d("ITM", "Body: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    _signupState.value = SignupState.Error("Signup failed")
                    Log.d("ITM", "Status Code: ${response.code()}")
                    Log.d("ITM", "Error Body: $errorBody")
                }
            } catch (e: Exception) {
                _signupState.value = SignupState.Error(e.message ?: "Unknown error")
                Log.d("ITM", e.message ?: "Unknown error")
            }
        }
    }
}


// 회원가입 진행 상태를 나타내는 sealed class
sealed class SignupState {
    object Initial : SignupState()
    object Loading : SignupState()
    object Success : SignupState()
    data class Error(val message: String) : SignupState()
}