package com.example.ontime.ui.team.teamDetail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ontime.R
import com.example.ontime.data.model.request.GetParticipantLocationInfo
import com.example.ontime.data.model.response.MeetingResponse
import com.example.ontime.ui.component.AppBar
import com.example.ontime.ui.team.SafeBase64Image
import com.example.ontime.ui.team.teamDetail.qr.QRScannerDialog
import com.example.ontime.ui.theme.MainColor
import com.example.ontime.ui.theme.SubColor
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun createColoredMarkerBitmap(color: Int): Bitmap {
    val size = 50 // 마커의 크기 (픽셀)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    paint.color = color
    paint.style = Paint.Style.FILL
    canvas.drawOval(RectF(0f, 0f, size.toFloat(), size.toFloat()), paint)
    return bitmap
}

fun getMainColor(): Int {
    return android.graphics.Color.rgb(255, 110, 112)
}

@Composable
fun TeamDetailScreen(
    viewModel: TeamDetailViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    var showArrivedDialog by remember { mutableStateOf(false) }
    var showQrScanner by remember { mutableStateOf(false) }


    var showQrDialog by remember { mutableStateOf(false) }
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 현재 사용자의 도착 상태 확인
    val currentUserId = viewModel.getUserId()
    val isCurrentUserArrived = currentUserId?.let { userId ->
        uiState.arrivalStatus[userId]?.participantArrivalStatus == "ARRIVED"
    } ?: false


    val context = LocalContext.current
    val scrollState = rememberScrollState()


    // QR 스캔 결과 처리
    LaunchedEffect(uiState.qrScanResult) {
        when (val result = uiState.qrScanResult) {
            is QrScanResult.Success -> {
                Toast.makeText(context, "체크인 되었습니다!", Toast.LENGTH_SHORT).show()
                viewModel.refreshTeamDetail()
                viewModel.clearQrScanResult()
            }

            is QrScanResult.Error -> {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                viewModel.clearQrScanResult()
            }

            null -> {} // 초기 상태
        }
    }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // 화면이 표시될 때마다 데이터 새로고침
    LaunchedEffect(Unit) {
        viewModel.refreshTeamDetail()
        viewModel.startParticipantLocationUpdates()
        viewModel.startLocationUpdates(fusedLocationClient)
    }


    // shake 이벤트를 위한 상태 추가
    var shakeTriggered by remember { mutableStateOf(false) }

    // shake 이벤트에 대한 처리
    LaunchedEffect(shakeTriggered, isCurrentUserArrived) {
        if (shakeTriggered) {
            if (isCurrentUserArrived) {
                showQrDialog = true
            } else {
                showArrivedDialog = true
            }
            shakeTriggered = false  // 상태 리셋
        }
    }

    // ShakeDetector 수정
    val shakeDetector = remember {
        ShakeDetector(context) {
            shakeTriggered = true  // shake 감지시 상태만 변경
        }
    }

    DisposableEffect(Unit) {
        shakeDetector.startListening()
        onDispose {
            shakeDetector.stopListening()
            viewModel.stopParticipantLocationUpdates()
            viewModel.stopLocationUpdates()
        }
    }

    // Location Permission handling
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions.values.reduce { acc, isGranted -> acc && isGranted }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MainColor)
            }
        }

        uiState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Log.d("ITM", "${uiState.error}")
                Text(
                    text = "Error: ${uiState.error}",
                    color = Color.Red
                )
            }
        }

        uiState.meeting != null -> {
            val meeting = uiState.meeting
            val teamInfo = viewModel.createTeamInfo(meeting)

            Column {
                AppBar()

//                Row {
//
//                    Button(onClick = { viewModel.getMeetingId() }) {
//                        Text(text = "meeting")
//                    }
//                    Button(onClick = { viewModel.getUserId() }) {
//                        Text(text = "user")
//                    }
//                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    // Team Header
                    TeamHeader(teamInfo, meeting)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Late Fee Section
                    teamInfo.bankAccount?.let { bankAccount ->
                        LateFeeSection(bankAccount = bankAccount)
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // ArrivedButton 수정
                    ArrivedButton(
                        onQrClick = {
                            if (isCurrentUserArrived) {
                                showQrDialog = true
                            } else {
                                showArrivedDialog = true
                            }
                        },
                        isArrived = isCurrentUserArrived
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 도착/미도착 참가자 분류
                    val (arrivedParticipants, notArrivedParticipants) = uiState.participantStatistics.entries.partition { (participantId, _) ->
                        uiState.arrivalStatus[participantId]?.participantArrivalStatus == "ARRIVED"
                    }

                    // Arrived Members Section
                    if (arrivedParticipants.isNotEmpty()) {
                        Text(
                            "Arrived",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // 도착한 참가자들을 정렬하여 인덱스를 부여
                        arrivedParticipants.sortedBy { entry: Map.Entry<String, TeamDetailViewModel.ParticipantInfo> ->
                            uiState.arrivalStatus[entry.key]?.arrivalTime // arrivalTime을 기준으로 정렬
                        }.forEachIndexed { index, entry ->
                            val participantInfo = entry.value

                            MemberCard(
                                member = MemberInfo(
                                    name = participantInfo.name,
                                    phoneNumber = participantInfo.phoneNumber,
                                    tardinessRate = participantInfo.statistics.lateRate.toFloat(),
                                    arrivalOrder = index + 1
                                ),
                                isArrived = true,
                                onCallClick = { phoneNumber ->
                                    val intent =
                                        Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                                    context.startActivity(intent)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

// Not Arrived Members Section
                    if (notArrivedParticipants.isNotEmpty()) {
                        Text(
                            "Not Arrived",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        notArrivedParticipants.forEach { entry ->
                            val participantInfo = entry.value

                            MemberCard(
                                member = MemberInfo(
                                    name = participantInfo.name,
                                    phoneNumber = participantInfo.phoneNumber,
                                    tardinessRate = participantInfo.statistics.lateRate.toFloat()
                                ),
                                isArrived = false,
                                onCallClick = { phoneNumber ->
                                    val intent =
                                        Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                                    context.startActivity(intent)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Team Location Map
                    Text(
                        "Team's Location",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TeamLocationMap(
                        coordinates = teamInfo.coordinates,
                        hasLocationPermission = hasLocationPermission,
                        participantLocations = uiState.participantLocations,
                    )
                }
            }
        }
    }
    if (showArrivedDialog) {
        CheckInConfirmDialog(
            viewModel = viewModel,
            onDismiss = { showArrivedDialog = false },
            onShowScanner = {
                showQrScanner = true  // QR 스캐너 표시
            },
            onShowQrDialog = {
                showQrDialog = true  // QR 코드 다이얼로그 표시
            }
        )
    }

    if (showQrScanner) {
        QRScannerDialog(
            onDismiss = { showQrScanner = false },
            onQrCodeScanned = { qrCode ->
                viewModel.handleQrCodeScan(qrCode)
                showQrScanner = false
            }
        )
    }

    // QR 코드 다이얼로그
    if (showQrDialog) {
        LaunchedEffect(Unit) {
            try {
                qrCodeBitmap = viewModel.getQRCode()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "QR 코드를 불러오는데 실패했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        QrCodeDialog(
            qrCodeBitmap = qrCodeBitmap,
            onDismiss = {
                showQrDialog = false
                qrCodeBitmap = null
            }
        )
    }
}

@Composable
private fun MemberCard(
    member: MemberInfo,
    isArrived: Boolean,
    onCallClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = member.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.alignByBaseline()
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isArrived)
                            "arrived ${member.arrivalOrder}${getOrdinalSuffix(member.arrivalOrder)}!"
                        else
                            "has not arrived..!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light,
                        color = if (isArrived) Color.Black else SubColor,
                        modifier = Modifier.alignByBaseline()
                    )
                }
                Text(
                    text = "Tardiness rate: ${member.tardinessRate}%",
                    fontSize = 12.sp,
                    color = SubColor
                )
            }
            IconButton(onClick = { onCallClick(member.phoneNumber) }) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call",
                    tint = MainColor
                )
            }
        }
    }
}

@Composable
private fun TeamHeader(teamInfo: TeamInfo, meeting: MeetingResponse) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {


        // Team Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = teamInfo.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            val formattedDateTime = teamInfo.time
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))

            Text(
                text = formattedDateTime,
                fontSize = 14.sp,
                color = SubColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val address = teamInfo.location
                val shortenedAddress =
                    address.split(",").firstOrNull() ?: address

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MainColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = shortenedAddress,
                    fontSize = 14.sp,
                )
            }
        }
        // Team Logo
        SafeBase64Image(
            base64String = meeting.profileImage,
            contentDescription = "Team Logo",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(1.dp, Color.LightGray, CircleShape)
        )
    }
}

@Composable
fun ArrivedButton(
    onQrClick: () -> Unit,
    isArrived: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Button(
            onClick = onQrClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MainColor,
                contentColor = Color.White
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ontime_logo),
                    contentDescription = "QR Code",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isArrived) "QR code" else "Check in with QR code",
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = SubColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isArrived)
                    "Or shake your phone to view the QR code"
                else
                    "Or shake your phone to check in",
                fontSize = 12.sp,
                color = SubColor
            )
        }
    }
}

@Composable
private fun TeamLocationMap(
    coordinates: LatLng,
    hasLocationPermission: Boolean,
    participantLocations: Map<String, GetParticipantLocationInfo>,
) {
    // 디버깅을 위한 로그 추가
    Log.d("ITM", "Meeting coordinates: $coordinates")
    Log.d("ITM", "Participant locations: $participantLocations")

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(coordinates, 15f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = hasLocationPermission,
                zoomControlsEnabled = true
            )
        ) {
            // 목적지 마커 추가
            Marker(
                state = MarkerState(position = coordinates),
                title = "Meeting Location",
                snippet = "목적지"
            )

            // 참가자 마커
            participantLocations.forEach { (_, locationInfo) ->
                if (locationInfo != null && locationInfo.participantLocation != null) {
                    val randomColor = getMainColor()
                    val markerBitmap = createColoredMarkerBitmap(randomColor)

                    val latitude = locationInfo.participantLocation.latitude
                    val longitude = locationInfo.participantLocation.longitude

                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                latitude, longitude
                            )
                        ),
                        title = locationInfo.participantName,
                        icon = BitmapDescriptorFactory.fromBitmap(markerBitmap)
                    )
                } else {
                    Log.e("TeamLocationMap", "Invalid locationInfo or participantLocation is null.")
                }
            }

        }
    }
}

@Composable
private fun LateFeeSection(
    bankAccount: BankAccountInfo,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 지각비 섹션 제목
            Text(
                text = "Late Fee",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // 지각비 금액 표시
            Text(
                text = "Amount: ${bankAccount.lateFee}won",
                fontSize = 16.sp,
                color = MainColor
            )

            // 계좌 정보와 송금 버튼을 포함하는 Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 계좌 정보 표시 영역
                Column {
                    Text(text = bankAccount.bankName)
                    Text(text = bankAccount.accountNumber)
                    Text(text = "Account Holder: ${bankAccount.accountHost}")
                }

                // 토스 송금 버튼
                Button(
                    onClick = {
                        try {
                            // 토스 딥링크 URI 생성
                            // bank: 은행명
                            // accountNo: 계좌번호
                            // amount: 송금액
                            // origin: 앱 식별자
                            val tossScheme = "supertoss://send?" +
                                    "bank=${bankAccount.bankName}&" +
                                    "accountNo=${bankAccount.accountNumber}&" +
                                    "amount=${bankAccount.lateFee}&" +
                                    "origin=OnTime"

                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tossScheme))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                            // 토스 앱이 설치되어 있는지 확인
                            if (intent.resolveActivity(context.packageManager) != null) {
                                // 토스 앱 실행
                                context.startActivity(intent)
                            } else {
                                // 토스 앱이 설치되어 있지 않은 경우 처리
                                Toast.makeText(
                                    context,
                                    "Toss app is not installed. Redirecting to Play Store...",
                                    Toast.LENGTH_SHORT
                                ).show()

                                try {
                                    // Play Store 앱으로 토스 앱 페이지 열기
                                    val playStoreIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=viva.republica.toss")
                                    )
                                    context.startActivity(playStoreIntent)
                                } catch (e: Exception) {
                                    // Play Store 앱이 없는 경우 웹 브라우저로 열기
                                    val webIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=viva.republica.toss")
                                    )
                                    context.startActivity(webIntent)
                                }
                            }
                        } catch (e: Exception) {
                            // 에러 발생 시 처리
                            Toast.makeText(
                                context,
                                "Error: ${e.localizedMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("TossPayment", "Error opening Toss app", e)
                        }
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainColor,
                        contentColor = Color.White
                    )

                ) {
                    Text("Pay Now")
                }
            }
        }
    }
}

private fun getOrdinalSuffix(number: Int): String {
    return when {
        number % 100 in 11..13 -> "th"
        number % 10 == 1 -> "st"
        number % 10 == 2 -> "nd"
        number % 10 == 3 -> "rd"
        else -> "th"
    }
}

// 팀 정보를 담는 데이터 클래스. 계좌 정보(bankAccount) 필드 추가
data class TeamInfo(
    val name: String,
    val time: LocalDateTime,
    val location: String,
    val coordinates: LatLng,
    val bankAccount: BankAccountInfo? = null  // 계좌 정보는 nullable로 설정
)

// 지각비 송금을 위한 계좌 정보를 담는 데이터 클래스
data class BankAccountInfo(
    val bankName: String,      // 은행명 (토스 딥링크에서 사용)
    val accountNumber: String, // 계좌번호 (토스 딥링크에서 사용)
    val accountHost: String, // 예금주
    val lateFee: Int          // 지각비 금액 (토스 딥링크에서 사용)
)

data class MemberInfo(
    val name: String,
    val phoneNumber: String,
    val tardinessRate: Float,
    val arrivalOrder: Int = 0
)
