package com.example.ontime.ui.auth.logout

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ontime.data.api.AuthApi
import com.example.ontime.data.auth.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val authManager: AuthManager
) : ViewModel() {
    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Initial)
    val logoutState: StateFlow<LogoutState> = _logoutState.asStateFlow()


    var isLoading by mutableStateOf(false)
        private set


    fun logout() {
        viewModelScope.launch {
            val userId = authManager.getUserId()
            Log.d("ITM", "$userId")
            // userId가 null이 아닌 경우에만 로그아웃 요청을 진행
            if (userId != null) {
                viewModelScope.launch {
                    Log.d("ITM", "$userId")

                    try {
                        _logoutState.value = LogoutState.Loading
                        isLoading = true
                        val response = authApi.logout(userId)
                        Log.d(
                            "ITM",
                            "Logout Response Code: ${response.code()}"
                        )
                        if (response.isSuccessful) {
                            authManager.clearAuthInfo()
                            _logoutState.value = LogoutState.Success
                            Log.d("ITM", "Logout Successful")
                        } else {
                            _logoutState.value = LogoutState.Error("Logout failed")
                            Log.d(
                                "ITM",
                                "Logout Error Body: ${response.errorBody()?.string()}"
                            )
                        }
                    } catch (e: Exception) {
                        _logoutState.value = LogoutState.Error(e.message ?: "Unknown error")
                    } finally {
                        isLoading = false
                    }
                }
            } else {
                // userId가 null일 경우 에러 처리
                _logoutState.value = LogoutState.Error("User ID is required for logout")
            }
        }
    }

}


sealed class LogoutState {
    object Initial : LogoutState()    // 초기 상태
    object Loading : LogoutState()    // 로딩 중
    object Success : LogoutState()    // 로그인 성공
    data class Error(val message: String) : LogoutState()  // 로그인 실패
}