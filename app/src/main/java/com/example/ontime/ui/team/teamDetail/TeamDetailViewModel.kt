package com.example.ontime.ui.team.teamDetail

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ontime.data.api.MeetingApi
import com.example.ontime.data.auth.AuthManager
import com.example.ontime.data.model.request.CreateQRRequest
import com.example.ontime.data.model.request.GetParticipantLocationInfo
import com.example.ontime.data.model.request.LocationInfo
import com.example.ontime.data.model.response.MeetingParticipantsResponse
import com.example.ontime.data.model.response.MeetingResponse
import com.example.ontime.data.model.response.ParticipantArrivalInfo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

sealed class QrScanResult {
    object Success : QrScanResult()
    data class Error(val message: String) : QrScanResult()
}

@HiltViewModel
class TeamDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val meetingApi: MeetingApi,
    private val authManager: AuthManager,
    @ApplicationContext private val context: Context
) : ViewModel() {


    data class UiState(
        val meeting: MeetingResponse? = null,
        val participantsInfo: MeetingParticipantsResponse? = null,
        val participantStatistics: Map<String, ParticipantInfo> = emptyMap(),
        val arrivalStatus: Map<String, ParticipantArrivalInfo> = emptyMap(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val qrScanResult: QrScanResult? = null,  // QR 스캔 결과 상태 추가
        val participantLocations: Map<String, GetParticipantLocationInfo> = emptyMap()  // 타입 변경
    )

    data class ParticipantStatistics(
        val totalMeetings: Int,
        val totalArrivedMeetings: Int,
        val totalLateMeetings: Int,
        val lateRate: Double
    )

    data class ParticipantInfo(
        val id: String,
        val name: String,
        val phoneNumber: String,
        val statistics: ParticipantStatistics
    )

    var uiState by mutableStateOf(UiState())
        private set

    private val teamId: String = checkNotNull(savedStateHandle.get<String>("teamId"))

    init {
        getTeamDetail()
    }


    private var participantLocationJob: Job? = null

    // startParticipantLocationUpdates 함수도 수정
    fun startParticipantLocationUpdates() {
        participantLocationJob?.cancel()
        participantLocationJob = viewModelScope.launch {
            while (isActive) {
                try {
                    val meetingId = uiState.meeting?.id ?: throw Exception("Meeting ID not found")
                    val response = meetingApi.getParticipantLocations(meetingId)

                    if (response.isSuccessful) {
                        response.body()?.let { locations ->
                            // participantLocationInfos를 Map으로 변환하여 저장
                            val locationMap = locations.participantLocationInfos.associate {
                                it.participantId to it
                            }
                            uiState = uiState.copy(participantLocations = locationMap)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("TeamDetail", "Error fetching participant locations", e)
                }
                delay(3000)
            }
        }
    }

    fun stopParticipantLocationUpdates() {
        participantLocationJob?.cancel()
        participantLocationJob = null
    }

    // ViewModel이 clear될 때 위치 업데이트 중지
    override fun onCleared() {
        super.onCleared()
        stopParticipantLocationUpdates()
        stopLocationUpdates()
    }

    // 위치 업데이트 Job을 저장할 변수
    private var locationUpdateJob: Job? = null
    private val locationUpdateInterval = 3000L // 3초

    // 위치 업데이트 시작
    fun startLocationUpdates(fusedLocationClient: FusedLocationProviderClient) {
        // 이전 Job이 있다면 취소
        locationUpdateJob?.cancel()

        locationUpdateJob = viewModelScope.launch {
            while (isActive) {
                try {
                    // 현재 위치 가져오기
                    getCurrentLocation(fusedLocationClient)?.let { location ->
                        // Geocoder를 사용하여 주소 가져오기
                        val address = getAddressFromLocation(location.latitude, location.longitude)

                        // 위치 정보 업데이트
                        updateParticipantLocation(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            address = address ?: "Unknown location"
                        )
                    }
                } catch (e: Exception) {
                    Log.d("TeamDetail", "Error updating location", e)
                }
                delay(locationUpdateInterval)
            }
        }
    }

    suspend fun updateParticipantLocation(latitude: Double, longitude: Double, address: String) {
        try {
            val meetingId = uiState.meeting?.id ?: throw Exception("Meeting ID not found")
            val participantId = authManager.getUserId() ?: throw Exception("User ID not found")

            val locationInfo = LocationInfo(
                latitude = latitude,
                longitude = longitude,
                address = address
            )

            val response = meetingApi.updateParticipantLocation(
                meetingId = meetingId,
                participantId = participantId,
                request = locationInfo
            )

            if (!response.isSuccessful) {
                throw Exception("Failed to update location: ${response.message()}")
            }

            // 위치 업데이트 성공 후 팀 정보 새로고침
//            refreshTeamDetail()

        } catch (e: Exception) {
            Log.d("TeamDetail", "Error updating participant location", e)
            throw e
        }
    }

    // 위치 업데이트 중지
    fun stopLocationUpdates() {
        locationUpdateJob?.cancel()
        locationUpdateJob = null
    }

    // Geocoder를 사용하여 주소 가져오기
    private suspend fun getAddressFromLocation(latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                addresses?.firstOrNull()?.let { address ->
                    buildString {
                        append(address.thoroughfare ?: "")
                        if (!address.thoroughfare.isNullOrEmpty() && !address.subThoroughfare.isNullOrEmpty()) {
                            append(" ")
                        }
                        append(address.subThoroughfare ?: "")
                    }.ifEmpty { null }
                }
            } catch (e: Exception) {
                Log.d("TeamDetail", "Error getting address", e)
                null
            }
        }
    }


    fun getMeetingId(): String? {
        Log.d("ITM", "${uiState.meeting?.id}")
        Log.d("ITM", "${uiState.meeting?.profileImage}")
        return uiState.meeting?.id
    }

    fun getUserId(): String? {
        Log.d("ITM", "${authManager.getUserId()}")
        return authManager.getUserId()
    }

    // 데이터 새로고침을 위한 함수
    fun refreshTeamDetail() {
        viewModelScope.launch {
            try {
                getTeamDetail()
            } catch (e: Exception) {
                Log.d("TeamDetail", "Error refreshing team details", e)
            }
        }
    }

    // QR 코드 처리를 위한 새로운 함수 추가
    fun handleQrCodeScan(qrCode: String) {
        viewModelScope.launch {
            try {
                val success = processQrCode(qrCode)
                uiState = if (success) {
                    uiState.copy(qrScanResult = QrScanResult.Success)
                } else {
                    uiState.copy(qrScanResult = QrScanResult.Error("체크인에 실패했습니다"))
                }
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("Invalid QR code") == true -> "잘못된 QR 코드입니다"
                    else -> "체크인 중 오류가 발생했습니다: ${e.message}"
                }
                uiState = uiState.copy(qrScanResult = QrScanResult.Error(errorMessage))
            }
        }
    }

    // QR 스캔 결과 초기화 함수 추가
    fun clearQrScanResult() {
        uiState = uiState.copy(qrScanResult = null)
    }


    fun getTeamDetail() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)

                // 병렬로 데이터 요청
                val meetingDeferred = async { meetingApi.getMeeting(meetingId = teamId) }
                val locationDeferred = async { meetingApi.getMeetingLocation(meetingId = teamId) }
                val statisticsDeferred =
                    async { meetingApi.getMeetingStatistics(meetingId = teamId) }

                val meetingResponse = meetingDeferred.await()
                val locationResponse = locationDeferred.await()
                val statisticsResponse = statisticsDeferred.await()

                if (meetingResponse.isSuccessful &&
                    locationResponse.isSuccessful &&
                    statisticsResponse.isSuccessful
                ) {

                    // getTeamDetail() 함수 내부의 statisticsMap 생성 부분 수정
                    val statisticsMap = statisticsResponse.body()?.associate { participant ->
                        participant.id to ParticipantInfo(
                            id = participant.id,
                            name = participant.name,
                            phoneNumber = participant.phoneNumber,
                            statistics = ParticipantStatistics(
                                totalMeetings = participant.statistics.totalMeetings,
                                totalArrivedMeetings = participant.statistics.totalArrivedMeetings,
                                totalLateMeetings = participant.statistics.totalLateMeetings,
                                lateRate = participant.statistics.lateRate
                            )
                        )
                    } ?: emptyMap()
                    // 2. 각 참가자의 도착 상태 확인
                    val arrivalStatusMap = mutableMapOf<String, ParticipantArrivalInfo>()
                    statisticsResponse.body()?.forEach { participant ->
                        try {
                            val arrivalResponse =
                                meetingApi.getParticipantArrival(teamId, participant.id)
                            if (arrivalResponse.isSuccessful) {
                                arrivalResponse.body()?.let { arrival ->
                                    arrivalStatusMap[participant.id] = arrival
                                }
                            }
                        } catch (e: Exception) {
                            Log.d(
                                "ITM",
                                "Error fetching arrival status for participant: ${participant.id}",
                                e
                            )
                        }
                    }

                    uiState = uiState.copy(
                        meeting = meetingResponse.body(),
                        participantsInfo = locationResponse.body(),
                        participantStatistics = statisticsMap,
                        arrivalStatus = arrivalStatusMap,
                        isLoading = false,
                        error = null
                    )
                } else {
                    val errorMessage = buildString {
                        if (!meetingResponse.isSuccessful) append("Failed to load meeting details. ")
                        if (!locationResponse.isSuccessful) append("Failed to load location info. ")
                        if (!statisticsResponse.isSuccessful) append("Failed to load statistics. ")
                    }
                    uiState = uiState.copy(
                        error = errorMessage.trim(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.d("ITM", "Error loading team details", e)
                uiState = uiState.copy(
                    error = e.message ?: "An unexpected error occurred",
                    isLoading = false
                )
            }
        }
    }

    // QR 코드 검증을 위한 메서드
    suspend fun verifyQRCode(scannedQRCode: String): Boolean {
        try {
            val meetingId = uiState.meeting?.id ?: throw Exception("Meeting ID not found")

            // 서버에서 현재 유효한 QR 코드 가져오기
            val response = meetingApi.getGRCode(meetingId)

            if (response.isSuccessful) {
                val serverQRCode = response.body()?.qrCode
                    ?: throw Exception("Server QR code is empty")

                // 스캔한 QR 코드와 서버의 QR 코드 비교
                return scannedQRCode == serverQRCode
            } else {
                throw Exception("Failed to get QR code from server")
            }
        } catch (e: Exception) {
            Log.d("TeamDetail", "Error verifying QR code", e)
            throw e
        }
    }

    // processQrCode 메서드 수정
    suspend fun processQrCode(scannedQRCode: String): Boolean {
        return try {
            // 1. QR 코드 검증
            val isValid = verifyQRCode(scannedQRCode)

            if (isValid) {
                // 2. 검증 성공시 도착 등록
                val arrivalSuccess = registerArrival()

                if (arrivalSuccess) {
                    // 3. UI 상태 갱신
                    refreshTeamDetail()
                    true
                } else {
                    false
                }
            } else {
                throw Exception("Invalid QR code")
            }
        } catch (e: Exception) {
            Log.d("TeamDetail", "Error processing QR code: ${e.message}", e)
            throw e
        }
    }


    // Helper function to create TeamInfo from MeetingResponse
    fun createTeamInfo(meeting: MeetingResponse): TeamInfo {
        val time = try {
            val localDateTime = LocalDateTime.parse(
                meeting.meetingDateTime,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
            )
            // UTC를 KST로 변환
            localDateTime
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime()
        } catch (e: Exception) {
            LocalDateTime.now() // 파싱 실패시 현재 시간을 기본값으로 사용
        }
        return TeamInfo(
            name = meeting.name,
            time = time,
            location = meeting.location.address,
            coordinates =
            LatLng(meeting.location.latitude, meeting.location.longitude), // Hardcoded for now
            bankAccount = BankAccountInfo(
                bankName = meeting.accountInfo.bankName,
                accountNumber = meeting.accountInfo.accountNumber,
                accountHost = meeting.accountInfo.accountHost,
//                accountHolder = "name",
                lateFee = meeting.lateFee
            )
        )
    }


    // check-in process

    companion object {
        private const val LOCATION_THRESHOLD_METERS = 100 // 100m 반경 내에 있으면 도착으로 간주
    }

    suspend fun checkUserArrival(fusedLocationClient: FusedLocationProviderClient): Boolean {
        try {
            // 1. 현재 위치 가져오기
            val currentLocation = getCurrentLocation(fusedLocationClient)
                ?: throw Exception("현재 위치를 가져올 수 없습니다.")

            // 2. 약속 장소 위치 가져오기
            val meetingLocation = uiState.meeting?.location?.let { location ->
                LatLng(location.latitude, location.longitude)
            } ?: throw Exception("약속 장소 정보를 찾을 수 없습니다.")

            // 3. 두 위치 간의 거리 계산
            val results = FloatArray(1)
            Location.distanceBetween(
                currentLocation.latitude,
                currentLocation.longitude,
                meetingLocation.latitude,
                meetingLocation.longitude,
                results
            )

            // 4. 거리가 임계값보다 작으면 도착으로 판단
            return results[0] <= LOCATION_THRESHOLD_METERS
        } catch (e: Exception) {
            Log.d("TeamDetail", "위치 확인 중 오류 발생", e)
            throw e
        }
    }

    private suspend fun getCurrentLocation(fusedLocationClient: FusedLocationProviderClient): LatLng? {
        return try {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                throw Exception("위치 권한이 필요합니다.")
            }

            fusedLocationClient.lastLocation.await()?.let { location ->
                LatLng(location.latitude, location.longitude)
            }
        } catch (e: Exception) {
            Log.d("TeamDetail", "현재 위치 가져오기 실패", e)
            null
        }
    }


    suspend fun registerArrival(): Boolean {
        return try {
            val meetingId = uiState.meeting?.id ?: throw Exception("Meeting ID not found")
            val participantId = authManager.getUserId() ?: throw Exception("User ID not found")
            val arrivalTime = LocalDateTime.now(ZoneOffset.UTC).toString()

            val response = meetingApi.registerArrival(
                meetingId = meetingId,
                participantId = participantId,
                arrivalTime = arrivalTime
            )

            if (response.isSuccessful) {
                response.body()?.let { arrivalInfo ->
                    // UI 상태 즉시 업데이트
                    updateArrivalStatus(participantId, arrivalInfo)
                }
                // 전체 팀 정보 새로고침
                getTeamDetail()
                true
            } else {
                throw Exception("체크인 실패: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.d("TeamDetail", "Error registering arrival", e)
            throw e
        }
    }

    private fun updateArrivalStatus(participantId: String, arrivalInfo: ParticipantArrivalInfo) {
        Log.d("ITM", "Updating arrival status for participant: $participantId")
        Log.d("ITM", "New arrival info: $arrivalInfo")
        Log.d("ITM", "Current arrival status: ${uiState.arrivalStatus}")

        val currentArrivalStatus = uiState.arrivalStatus.toMutableMap()
        currentArrivalStatus[participantId] = arrivalInfo

        // 도착한 참가자 정렬
        val arrivedParticipants = currentArrivalStatus.values
            .filter { it.participantArrivalStatus == "ARRIVED" }
            .sortedBy { it.arrivalTime }

        Log.d("ITM", "Updated arrival status: $currentArrivalStatus")
        Log.d("ITM", "Arrived participants: $arrivedParticipants")

        uiState = uiState.copy(
            arrivalStatus = currentArrivalStatus
        )

        Log.d("ITM", "Final UI state arrival status: ${uiState.arrivalStatus}")
    }

    // QR code

    suspend fun createQRCode(): Bitmap? {
        try {
            val meetingId = uiState.meeting?.id ?: throw Exception("Meeting ID not found")
            val meetingName = uiState.meeting?.name ?: throw Exception("Meeting name not found")
            val response =
                meetingApi.createQRCode(CreateQRRequest(meetingId, meetingName))

            if (response.isSuccessful) {
                val qrData = response.body()?.qrCode ?: throw Exception("QR code data is empty")
                return generateQRBitmap(qrData)
            } else {
                throw Exception("Failed to get QR code: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.d("TeamDetail", "Error getting QR code", e)
            throw e
        }
    }

    suspend fun getQRCode(): Bitmap? {
        try {
            val meetingId = uiState.meeting?.id ?: throw Exception("Meeting ID not found")
            val response =
                meetingApi.getGRCode(meetingId)

            if (response.isSuccessful) {
                val qrData = response.body()?.qrCode ?: throw Exception("QR code data is empty")
                return generateQRBitmap(qrData)
            } else {
                throw Exception("Failed to get QR code: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.d("TeamDetail", "Error getting QR code", e)
            throw e
        }
    }

    private fun generateQRBitmap(qrData: String): Bitmap {
        val qrWriter = QRCodeWriter()
        try {
            val bitMatrix = qrWriter.encode(
                qrData,
                BarcodeFormat.QR_CODE,
                512, // QR 코드 너비
                512  // QR 코드 높이
            )
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) Color.Black.toArgb() else Color.White.toArgb()
                    )
                }
            }
            return bitmap
        } catch (e: Exception) {
            Log.d("TeamDetail", "QR Code generation failed", e)
            throw e
        }
    }
}