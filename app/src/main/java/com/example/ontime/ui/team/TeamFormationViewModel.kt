package com.example.ontime.ui.team

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TeamFormationState {
    object Initial : TeamFormationState
    object Loading : TeamFormationState
    object Success : TeamFormationState
    data class Error(val message: String) : TeamFormationState
}

@HiltViewModel
class TeamFormationViewModel @Inject constructor(
//    private val api: OnTimeApi
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

    fun updateDateTime(date: String, time: String) {
        _formState.update {
            it.copy(
                date = date,
                time = time
            )
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