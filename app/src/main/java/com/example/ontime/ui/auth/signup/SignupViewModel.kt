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

// SignupViewModel - 회원가입 관련 비즈니스 로직과 상태 관리를 담당

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val authManager: AuthManager
) : ViewModel() {
    // 회원가입 진행 상태를 저장하는 StateFlow
    private val _signupState = MutableStateFlow<SignupState>(SignupState.Initial)
    val signupState: StateFlow<SignupState> = _signupState.asStateFlow()

    // 회원가입 API 호출 함수
    fun signup(phoneNumber: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                // 로딩 상태로 변경
                _signupState.value = SignupState.Loading

                // API 요청 객체 생성
                val request = SignupRequest(name, phoneNumber, password)
                // 회원가입 API 호출
                val response = authApi.signup(request)

                // 응답 처리
                if (response.isSuccessful) {
                    response.body()?.let { signupResponse ->
                        authManager.saveAuthInfo(
                            signupResponse.accessToken,
                            signupResponse.refreshToken,
                            signupResponse.expiresIn,
                            signupResponse.userId
                        )
                    }
                    _signupState.value = SignupState.Success
                    Log.d("ITM", "Status Code: ${response.code()}")
                    Log.d("ITM", "Headers: ${response.headers()}")
                    Log.d("ITM", "Body: ${response.body()}")
                } else {
                    _signupState.value = SignupState.Error("Signup failed")
                    Log.d("ITM", "fail")
                }
            } catch (e: Exception) {
                // 에러 처리
                _signupState.value = SignupState.Error(e.message ?: "Unknown error")
                Log.d("ITM", e.message ?: "Unknown error")
            }
        }
    }
}

// 회원가입 진행 상태를 나타내는 sealed class
sealed class SignupState {
    object Initial : SignupState()    // 초기 상태
    object Loading : SignupState()    // API 호출 중
    object Success : SignupState()    // API 호출 성공
    data class Error(val message: String) : SignupState()  // API 호출 실패
}