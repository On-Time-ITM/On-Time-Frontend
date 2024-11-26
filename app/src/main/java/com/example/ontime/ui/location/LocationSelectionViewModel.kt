package com.example.ontime.ui.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ontime.data.model.nominatim.SearchResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationSelectionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationRepository: LocationRepository
) :
    ViewModel() {


    data class UiState(
        val searchText: String = "",
        val selectedLocation: LatLng? = null,
        val currentLocation: LatLng? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val isPermissionGranted: Boolean = false,
        val searchResults: List<SearchResult> = emptyList(), // 검색 결과 추가
        val selection: LocationSelection? = null
    )


    var uiState by mutableStateOf(UiState())
        private set

    // 권한 체크 함수
    fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun updatePermissionStatus(isGranted: Boolean) {
        uiState = uiState.copy(isPermissionGranted = isGranted)
    }


    // 위치가 선택되었을 때의 콜백을 위한 이벤트
    private val _navigationEvent = MutableSharedFlow<LocationSelectionEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onSearchChange(newText: String) {
        uiState = uiState.copy(searchText = newText)
        searchWithDebounce(newText)
    }

    private var searchJob: Job? = null

    private fun searchWithDebounce(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // 300ms 딜레이
            if (query.length >= 2) {
                searchLocations()
            } else {
                uiState = uiState.copy(searchResults = emptyList())
            }
        }
    }


    // FusedLocationProviderClient 추가
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // 현재 위치를 가져오는 함수
    fun getCurrentLocation() {
        if (!checkLocationPermission()) return

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val currentLocation = LatLng(it.latitude, it.longitude)
                        uiState = uiState.copy(
                            currentLocation = currentLocation,  // selectedLocation이 아닌 currentLocation을 업데이트
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    error = "Failed to get current location",
                    isLoading = false
                )
            }
        }
    }
//
//    // 위치 선택 완료 처리
//    fun confirmLocation() {
//        viewModelScope.launch {
//            try {
//                uiState.selectedLocation?.let { location ->
//                    uiState = uiState.copy(isLoading = true)
//                    val address = locationRepository.reverseGeocode(location)
//
//                    _navigationEvent.emit(
//                        LocationSelectionEvent.LocationConfirmed(
//                            address = address,
//                            latLng = location
//                        )
//                    )
//
//                    uiState = uiState.copy(isLoading = false)
//                }
//            } catch (e: Exception) {
//                Log.e("ITM", "Error: ${e.message}", e)
//                uiState = uiState.copy(
//                    error = "Failed to confirm location: ${e.message}",
//                    isLoading = false
//                )
//            }
//        }
//    }

    fun searchLocations() {
        if (uiState.searchText.isBlank()) return

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)
                val results = locationRepository.searchLocations(uiState.searchText)
                uiState = uiState.copy(
                    searchResults = results,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("ITM", "Error: ${e.message}", e)
                uiState = uiState.copy(
                    error = "Failed to search location: ${e.message}",
                    isLoading = false
                )
            }
        }
    }


    private val _cameraPositionEvent = MutableSharedFlow<LatLng>()
    val cameraPositionEvent = _cameraPositionEvent.asSharedFlow()

    fun selectSearchResult(result: SearchResult) {
        val newLocation = result.toLatLng()
        viewModelScope.launch {
            uiState = uiState.copy(
                selection = LocationSelection.FromSearch(
                    latLng = newLocation,
                    address = result.displayName
                ),
                selectedLocation = newLocation,  // selectedLocation도 함께 업데이트
                searchResults = emptyList(),
                searchText = result.displayName
            )
            _cameraPositionEvent.emit(newLocation)  // 카메라 이동을 위한 이벤트 발생
        }
    }


    fun updateSelectedLocation(latLng: LatLng) {
        uiState = uiState.copy(
            selection = LocationSelection.FromMap(latLng),
            selectedLocation = latLng  // selectedLocation도 함께 업데이트
        )
    }

    fun confirmLocation() {
        viewModelScope.launch {
            try {
                when (val selection = uiState.selection) {
                    is LocationSelection.FromSearch -> {
                        _navigationEvent.emit(
                            LocationSelectionEvent.LocationConfirmed(
                                address = selection.address,
                                latLng = selection.latLng
                            )
                        )
                    }

                    is LocationSelection.FromMap -> {
                        val address = locationRepository.reverseGeocode(selection.latLng)
                        _navigationEvent.emit(
                            LocationSelectionEvent.LocationConfirmed(
                                address = address,
                                latLng = selection.latLng
                            )
                        )
                    }

                    null -> { /* 처리 필요 없음 */
                    }
                }
            } catch (e: Exception) {
                // 에러 처리...
            }
        }
    }


    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    sealed class LocationSelection {
        data class FromSearch(
            val latLng: LatLng,
            val address: String
        ) : LocationSelection()

        data class FromMap(
            val latLng: LatLng
        ) : LocationSelection()
    }
//    init {
//        viewModelScope.launch {
//            searchText
//                .debounce(300L)  // 타이핑 중에는 API 호출 안함
//                .collect { query ->
//                    if (query.length >= 2) {
//                        searchLocation(query)
//                    }
//                }
//        }
//    }

//    private suspend fun searchLocation(query: String) {
//        _isLoading.value = true
//        try {
//            val results = locationRepository.search(query)
//            _searchResults.value = results
//        } catch (e: Exception) {
//            // 에러 처리
//        } finally {
//            _isLoading.value = false
//        }
//    }
}
