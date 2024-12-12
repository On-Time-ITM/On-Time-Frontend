package com.example.ontime.ui.friend.friendList

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ontime.data.api.FriendApi
import com.example.ontime.data.auth.AuthManager
import com.example.ontime.data.model.response.FriendResponse
import com.example.ontime.ui.friendSelection.FriendSelectionEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FriendListViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val friendApi: FriendApi
) : ViewModel() {
    data class UiState(
        val friends: List<FriendResponse> = emptyList(),
        val selectedFriends: List<FriendResponse> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isSuccess: Boolean = false,
    )

    var uiState by mutableStateOf(UiState())
        private set

    var userId by mutableStateOf(authManager.getUserId())
        private set

    fun addFriend(friend: FriendResponse) {
        if (!uiState.selectedFriends.contains(friend)) {
            uiState = uiState.copy(
                selectedFriends = uiState.selectedFriends + friend
            )
        }
        Log.d("ITM", "${uiState.selectedFriends}")
    }

    fun removeFriend(friend: FriendResponse) {
        uiState = uiState.copy(
            selectedFriends = uiState.selectedFriends.filter { it.id != friend.id }
        )
    }

    fun getFriendsList() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)
                val response = friendApi.getFriendList(userId = userId.toString())
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        Log.d("ITM", "${responseBody}")
                        uiState = uiState.copy(
                            friends = responseBody,
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("ITM", "Status Code: ${response.code()}")
                    Log.d("ITM", "Error Body: ${response.message()}")
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

    // 위치가 선택되었을 때의 콜백을 위한 이벤트
    private val _navigationEvent = MutableSharedFlow<FriendSelectionEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun confirmFriendSelection() {
        viewModelScope.launch {
            _navigationEvent.emit(
                FriendSelectionEvent.FriendsConfirmed(
                    selectedFriends = uiState.selectedFriends.map { it.id },
                    selectedFriendsList = uiState.selectedFriends.map { it }
                )
            )
        }
    }
}