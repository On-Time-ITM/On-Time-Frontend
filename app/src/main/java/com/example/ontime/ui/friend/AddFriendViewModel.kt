package com.example.ontime.ui.friend

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ontime.data.api.FriendApi
import com.example.ontime.data.auth.AuthManager
import com.example.ontime.data.model.request.AddFriendRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddFriendViewModel @Inject constructor(
    private val friendApi: FriendApi,
    private val authManager: AuthManager
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

    private fun validatePhoneNumber(phone: String): String? {
        return when {
            phone.isEmpty() -> "Phone number is required"
            !phone.matches(Regex("^\\d{11}$")) -> "Please enter a 11-digit phone number"
            else -> null
        }
    }


    fun onPhoneNumberChange(number: String) {
        Log.d("ITM", "Phone number changed to: $number") // 전화번호 변경 로깅
        uiState = uiState.copy(
            phoneNumber = number,
            phoneNumberError = validatePhoneNumber(number)
        )
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    fun addFriend(phoneNumber: String) {
        val phoneNumberError = validatePhoneNumber(phoneNumber)

        if (phoneNumberError != null) {
            uiState = uiState.copy(phoneNumberError = phoneNumberError)
            return
        }

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)
                val request = AddFriendRequest(phoneNumber, userId.toString())
                Log.d("ITM", "Request: ${request}") // 실제 전송되는 요청 데이터 확인

                val response = friendApi.addFriend(request)

                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        Log.d("ITM", "Response: $responseBody") // 성공 응답 로깅
                        uiState = uiState.copy(
                            isSuccess = true,
                            isLoading = false
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("ITM", "Status Code: ${response.code()}") // 에러 상태 코드 로깅
                    Log.d("ITM", "Error Body: $errorBody") // 에러 응답 로깅

                    uiState = uiState.copy(
                        error = "Failed to add friend",
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                Log.e("ITM", "Exception occurred", e) // 예외 로깅
                uiState = uiState.copy(
                    error = e.message ?: "Unknown error occurred",
                    isLoading = false
                )
            }
        }
    }

}