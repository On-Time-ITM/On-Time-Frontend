package com.example.ontime.ui.team

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ontime.data.api.MeetingApi
import com.example.ontime.data.auth.AuthManager
import com.example.ontime.data.model.account.AccountData
import com.example.ontime.data.model.request.AccountInfo
import com.example.ontime.data.model.request.CreateMeetingRequest
import com.example.ontime.data.model.request.Location
import com.example.ontime.data.model.response.FriendResponse
import com.example.ontime.repository.AccountRepository
import com.example.ontime.repository.StabilityRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed interface TeamFormationState {
    object Initial : TeamFormationState
    object Loading : TeamFormationState
    data class Success(
        val meetingTitle: String,
        val meetingId: String
    ) : TeamFormationState

    data class Error(val message: String) : TeamFormationState
}


@HiltViewModel
class TeamFormationViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val stabilityRepository: StabilityRepository,
    private val meetingApi: MeetingApi,
    private val authManager: AuthManager
) : ViewModel() {
    private val _formState = MutableStateFlow(TeamFormationData())
    val formState = _formState.asStateFlow()

    private val _uiState = MutableStateFlow<TeamFormationState>(TeamFormationState.Initial)
    val uiState = _uiState.asStateFlow()


    fun generateLogo(prompt: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val result = stabilityRepository.generateImage(prompt)
            onResult(result)
        }
    }

    fun updateLogo(url: String) {
        _formState.update { it.copy(logoUrl = url) }
    }

    fun updateTitle(title: String) {
        _formState.update { it.copy(title = title) }
    }

    fun updateLocation(address: String, latLng: LatLng) {
        Log.d("ITM", "Received address: $address")
        _formState.update {
            it.copy(
                location = address,
                latitude = latLng.latitude,
                longitude = latLng.longitude
            )
        }
        Log.d("ITM", "Updated state: ${_formState.value}")
    }


    fun updateMembersList(membersList: List<FriendResponse>) {
        _formState.update { it.copy(membersList = membersList) }
    }

    // ISO 8601 형식으로 변환
    private fun convertToISODateTime(date: LocalDate, time: LocalTime): String {
        val dateTime = LocalDateTime.of(date, time)
        // Asia/Seoul 시간대 명시적 설정
        val koreaZoneId = ZoneId.of("Asia/Seoul")
        val zoned = dateTime.atZone(koreaZoneId)
        return zoned.toInstant().toString()
    }

    //    ISO 8601 문자열을 LocalDateTime으로 파싱
//    fun parseISODateTime(isoDateTime: String): LocalDateTime {
//        return Instant.parse(isoDateTime)
//            .atZone(ZoneId.systemDefault())
//            .toLocalDateTime()
//    }


    fun updateDateTime(date: LocalDate, time: LocalTime) {
        val isoDateTime = convertToISODateTime(date, time)
        val formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        val formattedTime = time.format(DateTimeFormatter.ofPattern("HH:mm"))

        _formState.update { currentState ->
            currentState.copy(
                meetingDateTime = isoDateTime,
                date = formattedDate,
                time = formattedTime
            )
        }
    }

    fun updateAccount(account: AccountData) {
        _formState.update {
            it.copy(bankAccount = account)
        }
        // 계좌 정보 저장
        viewModelScope.launch {
            try {
                accountRepository.saveAccountInfo(account)
            } catch (e: Exception) {
                Log.d("TeamFormation", "Error saving account info", e)
            }
        }
    }

    fun createTeam() {
        viewModelScope.launch {
            try {
                _uiState.value = TeamFormationState.Loading
                // MeetingRequest 객체 생성
                val request = CreateMeetingRequest(
                    name = _formState.value.title, // 회의 제목
                    meetingDateTime = _formState.value.meetingDateTime, // ISO 8601 형식의 회의 날짜 및 시간
                    location = Location(
                        latitude = _formState.value.latitude,
                        longitude = _formState.value.longitude,
                        address = _formState.value.location
                    ), // 위치,
                    lateFee = _formState.value.bankAccount?.lateFee ?: 0, // lateFee는 기본값을 설정
                    accountInfo = AccountInfo(
                        bankName = _formState.value.bankAccount?.bankName
                            ?: "", // 은행 이름, null일 경우 빈 문자열
                        accountNumber = _formState.value.bankAccount?.accountNumber
                            ?: "",// 계좌 번호, null일 경우 빈 문자열
                        accountHost = _formState.value.bankAccount?.accountHost
                            ?: ""
                    ),
                    hostId = authManager.getUserId() ?: "",  // 호스트 ID
                    participantIds = _formState.value.membersList.map { it.id }, // 참가자 ID 리스트
                    profileImage = _formState.value.logoUrl ?: "" // 프로필 이미지 ID,

//                    profileImage = "11111111111111111" // 프로필 이미지 ID,

                )

                Log.d("ITM", "=== Request Details for Team Creation ===")
                Log.d("ITM", "Request Data: $request")
                Log.d("ITM", "Request name: ${request.name}")
                Log.d("ITM", "Request meetingDateTime: ${request.meetingDateTime}")
                Log.d("ITM", "Request location: ${request.location}")
                Log.d(
                    "ITM",
                    "Latitude: ${_formState.value.latitude}, Longitude: ${_formState.value.longitude}"
                )
                Log.d("ITM", "Request participantIds: ${request.participantIds}")

                try {
                    val response = meetingApi.createMeeting(request)

                    Log.d("ITM", "=== Response Details ===")
                    Log.d("ITM", "Response code: ${response}")

                    if (response.isSuccessful) {
                        response.body()?.let { meetingResponse ->
                            Log.d("ITM", "Response body: $meetingResponse")
                            _uiState.value = TeamFormationState.Success(
                                meetingTitle = request.name,
                                meetingId = meetingResponse
                            )
                        } ?: throw Exception("Response body is null")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.d("ITM", "=== Error Details ===")
                        Log.d("ITM", "Status Code: ${response.code()}")
                        Log.d("ITM", "Error Body: $errorBody")
                        Log.d("ITM", "Raw Response: ${response.raw()}")

                        _uiState.value = TeamFormationState.Error(
                            "Failed to create team: $errorBody"
                        )
                    }
                } catch (e: Exception) {
                    Log.d("ITM", "=== Network Error ===")
                    Log.d("ITM", "Error message: ${e.message}")
                    Log.d("ITM", "Stack trace: ", e)

                    _uiState.value = TeamFormationState.Error(
                        "Network error: ${e.message}"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = TeamFormationState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun getValue() {
        Log.d("ITM", "Current formState value: ${_formState.value}")
    }

    private var isFirstEntry = true

    fun resetState() {
        if (isFirstEntry) {
            _formState.value = TeamFormationData()
            _uiState.value = TeamFormationState.Initial
            isFirstEntry = false
        }
    }

    // 팀 생성 완료 후 상태 초기화
    fun resetAfterCreation() {
        _formState.value = TeamFormationData()
        _uiState.value = TeamFormationState.Initial
        isFirstEntry = true
    }
}