package com.example.ontime.ui.friend.addFriend

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ontime.data.auth.AuthManager
import com.example.ontime.ui.friend.usecase.AddFriendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFriendViewModel @Inject constructor(
    private val addFriendUseCase: AddFriendUseCase,  // FriendApi, AuthManager 대신 UseCase 주입
    private val authManager: AuthManager  // userId를 위해 필요
) : ViewModel() {
    data class UiState(
        val phoneNumber: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val isSuccess: Boolean = false,
        val phoneNumberError: String? = null
    )

    var uiState by mutableStateOf(UiState())
        private set

    var userId by mutableStateOf(authManager.getUserId())
        private set

    fun onPhoneNumberChange(number: String) {
        Log.d("ITM", "Phone number changed to: $number")
        // validatePhoneNumber도 UseCase로 이동했으므로 UseCase의 함수 사용
        uiState = uiState.copy(
            phoneNumber = number,
            phoneNumberError = addFriendUseCase.validatePhoneNumber(number)
        )
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    fun addFriend(phoneNumber: String) {
        // 전화번호 유효성 검사
        val phoneNumberError = addFriendUseCase.validatePhoneNumber(phoneNumber)
        if (phoneNumberError != null) {
            uiState = uiState.copy(phoneNumberError = phoneNumberError)
            return
        }

        // 친구 추가 요청
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)

                // UseCase를 통해 친구 추가 요청
                val result = addFriendUseCase.addFriend(phoneNumber)

                if (result.isSuccess) {
                    uiState = uiState.copy(
                        isSuccess = true,
                        isLoading = false
                    )
                } else {
                    uiState = uiState.copy(
                        error = result.exceptionOrNull()?.message ?: "Failed to add friend",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("ITM", "Exception in ViewModel", e)
                uiState = uiState.copy(
                    error = e.message ?: "Unknown error occurred",
                    isLoading = false
                )
            }
        }
    }
}