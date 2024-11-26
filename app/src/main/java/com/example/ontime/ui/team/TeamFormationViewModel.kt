package com.example.ontime.ui.team

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ontime.data.model.account.AccountData
import com.example.ontime.repository.AccountRepository
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
    object Success : TeamFormationState
    data class Error(val message: String) : TeamFormationState
}

@HiltViewModel
class TeamFormationViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _formState = MutableStateFlow(TeamFormationData())
    val formState = _formState.asStateFlow()

    private val _uiState = MutableStateFlow<TeamFormationState>(TeamFormationState.Initial)
    val uiState = _uiState.asStateFlow()

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

    fun updateMembers(members: List<String>) {
        _formState.update { it.copy(members = members) }
    }

    // ISO 8601 형식으로 변환
    private fun convertToISODateTime(date: LocalDate, time: LocalTime): String {
        val dateTime = LocalDateTime.of(date, time)
        val zoned = dateTime.atZone(ZoneId.systemDefault())
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
                Log.e("TeamFormation", "Error saving account info", e)
            }
        }
    }

    fun createTeam() {
        viewModelScope.launch {
            _uiState.value = TeamFormationState.Loading
            try {
                // API를 직접 호출
//                api.createTeam(_formState.value)
                _uiState.value = TeamFormationState.Success
            } catch (e: Exception) {
                _uiState.value = TeamFormationState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun getValue() {
        Log.d("ITM", "Current formState value: ${_formState.value}")
    }

}