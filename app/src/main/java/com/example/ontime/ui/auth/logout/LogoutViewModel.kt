package com.example.ontime.ui.auth.logout

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
            try {
                _logoutState.value = LogoutState.Loading
                isLoading = true
                val response = authApi.logout()

                if (response.isSuccessful) {
                    authManager.clearAuthInfo()
                    _logoutState.value = LogoutState.Success
                } else {
                    _logoutState.value = LogoutState.Error("Logout failed")
                }
            } catch (e: Exception) {
                _logoutState.value = LogoutState.Error(e.message ?: "Unknown error")
            } finally {
                isLoading = false
            }
        }
    }

    fun onLogoutClick() {
        logout()
    }
}


sealed class LogoutState {
    object Initial : LogoutState()    // 초기 상태
    object Loading : LogoutState()    // 로딩 중
    object Success : LogoutState()    // 로그인 성공
    data class Error(val message: String) : LogoutState()  // 로그인 실패
}