package com.example.ontime.ui.friend.requestAccpet

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ontime.data.api.FriendApi
import com.example.ontime.data.auth.AuthManager
import com.example.ontime.data.model.request.FriendshipRequestAcceptRequest
import com.example.ontime.data.model.response.FriendshipRequestListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestListViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val friendApi: FriendApi
) : ViewModel() {
    data class UiState(
        val requests: List<FriendshipRequestListResponse> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isSuccess: Boolean = false,
        val acceptSuccess: Boolean = false
    )

    var uiState by mutableStateOf(UiState())
        private set

    var userId by mutableStateOf(authManager.getUserId())
        private set


    fun clearAcceptSuccess() {
        uiState = uiState.copy(acceptSuccess = false)
    }

    fun clearSuccess() {
        uiState = uiState.copy(isSuccess = false)
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    fun getRequestList() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)

                val response = friendApi.getFriendshipRequestList(userId = userId.toString())
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        Log.d("ITM", "${responseBody}")

                        uiState = uiState.copy(
                            requests = responseBody,  // 여기서 리스트 저장
                            isLoading = false,        // 로딩 완료
                            error = null              // 에러 초기화
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("ITM", "Status Code: ${response.code()}")
                    Log.d("ITM", "Error Body: $errorBody")
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

    fun acceptFriendshipRequest(friendshipId: String) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)

                val request = FriendshipRequestAcceptRequest(
                    friendshipId = friendshipId,
                    receiverId = userId.toString()
                )

                Log.d("ITM", "${request}")
                val response = friendApi.acceptFriendshipRequest(request)
                if (response.isSuccessful) {
                    uiState = uiState.copy(
                        acceptSuccess = true,  // 여기서 acceptSuccess를 true로 설정
                        error = null,
                        isLoading = false
                    )
                } else {
                    uiState = uiState.copy(
                        error = "Failed to accept request: ${response.code()}",
                        isLoading = false,
                        acceptSuccess = false
                    )
                    val errorBody = response.errorBody()?.string()
                    Log.d("ITM", "Accept Request Failed - Status Code: ${response.code()}")
                    Log.d("ITM", "Error Body: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("ITM", "Exception in acceptFriendRequest", e)
                uiState = uiState.copy(
                    error = e.message ?: "Unknown error occurred",
                    isLoading = false,
                    acceptSuccess = false
                )
            }
        }
    }

}