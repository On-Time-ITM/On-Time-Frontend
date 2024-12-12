package com.example.ontime.ui.main

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ontime.data.api.MeetingApi
import com.example.ontime.data.auth.AuthManager
import com.example.ontime.data.model.request.DeleteMeetingRequest
import com.example.ontime.data.model.response.MeetingResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val meetingApi: MeetingApi,
) : ViewModel() {


    data class UiState(
        val meetingList: List<MeetingResponse> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isSuccess: Boolean = false,
        val isDeleteLoading: Boolean = false,
        val deleteError: String? = null,
        val deleteSuccess: Boolean = false,
        val showDeleteSuccessMessage: Boolean = false
    )


    var uiState by mutableStateOf(UiState())
        private set

    var userId by mutableStateOf(authManager.getUserId())
        private set

    init {
        getMeetingList()
    }

    fun getMeetingList() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)
                val response = meetingApi.getMeetingList(userId = userId.toString())

                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->

//                        Log.d("ITM", "=== Response Details ===")
                        // 여러 개의 MeetingResponse가 있을 수 있으므로 반복문 사용
                        responseBody.forEach { meetingResponse ->
//                            Log.d("ITM", "Response ID: ${meetingResponse.id}")
//                            Log.d("ITM", "Response name: ${meetingResponse.name}")
//                            Log.d(
//                                "ITM",
//                                "Response meetingDateTime: ${meetingResponse.meetingDateTime}"
//                            )
//                            Log.d("ITM", "Response location: ${meetingResponse.location}")
//                            Log.d(
//                                "ITM",
//                                "Response Latitude: ${meetingResponse.location.latitude}, Longitude: ${meetingResponse.location.longitude}"
//                            )
//                            Log.d(
//                                "ITM",
//                                "Profile Image: ${meetingResponse.profileImage.take(10)}"
//                            )
//                            // 앞의 10글자만 출력
//                            Log.d("ITM", "Response lateFee: ${meetingResponse.lateFee}")
//                            Log.d(
//                                "ITM",
//                                "Response participantCount: ${meetingResponse.participantCount}"
//                            )
                        }
//                        Log.d("ITM", "Meeting DateTime format: ${responseBody[0].meetingDateTime}")
                        uiState = uiState.copy(
                            meetingList = responseBody,
                            isLoading = false,
                            isSuccess = true,
                            error = null  // 성공 시 에러 초기화
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
//
//                    // 실패 시 5초 후 재시도
//                    delay(5000)
//                    getMeetingList()
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

    fun deleteMeeting(meetingId: String) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isDeleteLoading = true)

                val request = DeleteMeetingRequest(
                    meetingId = meetingId,
                    hostId = userId.toString()
                )

                val response = meetingApi.deleteMeeting(request)

                when (response.code()) {

                    200 -> {
                        // 삭제 성공 시 메시지 표시 설정
                        uiState = uiState.copy(
                            showDeleteSuccessMessage = true,
                            deleteSuccess = true,
                            isDeleteLoading = false,
                            deleteError = null
                        )

                        Log.d("ITM", "Delete successful: ${response.body()}") // 삭제 성공 로그
                        // 삭제 성공 시 목록 갱신
                        getMeetingList()
                    }

                    400 -> {
                        uiState = uiState.copy(
                            deleteError = "Only the host can delete this team",
                            isDeleteLoading = false
                        )
                        Log.d("ITM", "Delete failed: Only the host can delete this team") // 오류 로그
                    }

                    403 -> {
                        uiState = uiState.copy(
                            deleteError = "Only the host can delete this team",
                            isDeleteLoading = false
                        )
                        Log.d("ITM", "Delete failed: Only the host can delete this team") // 오류 로그
                    }

                    else -> {
                        uiState = uiState.copy(
                            deleteError = "Failed to delete meeting: ${response.message()}",
                            isDeleteLoading = false
                        )
                        Log.d("ITM", "Delete failed: ${response}") // 오류 로그
                    }
                }
            } catch (e: Exception) {
                Log.e("ITM", "Error deleting meeting", e)
                uiState = uiState.copy(
                    deleteError = e.message ?: "An unexpected error occurred",
                    isDeleteLoading = false
                )
            }
        }
    }


    fun getUserName(): String {
        return authManager.getName() ?: "User name not found"
    }

    fun getTardinessRate(): Float {
        return authManager.getTardinessRate()
    }

    fun refreshTeams() {
        getMeetingList()
    }

    fun logout() {
        authManager.clearAuthInfo()
    }

    // 메시지 표시 후 상태 초기화를 위한 함수
    fun clearDeleteSuccessMessage() {
        uiState = uiState.copy(showDeleteSuccessMessage = false)
    }
}