package com.example.ontime.ui.main

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ontime.data.api.FriendApi
import com.example.ontime.data.auth.AuthManager
import com.example.ontime.data.model.response.FriendResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FriendSelectionViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val friendApi: FriendApi
) : ViewModel() {
    data class UiState(
        val friends: List<FriendResponse> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isSuccess: Boolean = false,
    )

    var uiState by mutableStateOf(UiState())
        private set

    var userId by mutableStateOf(authManager.getUserId())
        private set


    fun getFriendsList() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)
                val response = friendApi.getFriendList(userId = userId.toString())
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        Log.d("ITM", "${responseBody}")
                        // 친구 목록을 uiState에 저장하고 로딩 상태를 false로 변경
                        uiState = uiState.copy(
                            friends = responseBody,
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("ITM", "Status Code: ${response.code()}")
                    Log.d("ITM", "Error Body: $errorBody")
                    // 에러 발생 시 에러 메시지 저장하고 로딩 상태를 false로 변경
                    uiState = uiState.copy(
                        error = errorBody ?: "Unknown error occurred",
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
