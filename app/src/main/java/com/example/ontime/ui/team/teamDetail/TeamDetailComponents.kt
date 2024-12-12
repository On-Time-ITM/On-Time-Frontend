package com.example.ontime.ui.team.teamDetail

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ontime.ui.theme.MainColor
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Composable
fun QrCodeDialog(
    qrCodeBitmap: Bitmap?,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "QR 코드",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (qrCodeBitmap != null) {
                    Image(
                        bitmap = qrCodeBitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(200.dp)
                    )
                } else {
                    CircularProgressIndicator(color = MainColor)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainColor,
                        contentColor = Color.White
                    )
                ) {
                    Text("Close")
                }
            }
        }
    }
}

//
//@Composable
//fun CheckInConfirmDialog(
//    viewModel: TeamDetailViewModel,
//    onDismiss: () -> Unit,
//    onShowScanner: () -> Unit,
//    onShowQrDialog: () -> Unit
//) {
//    val context = LocalContext.current
//    val fusedLocationClient = remember {
//        LocationServices.getFusedLocationProviderClient(context)
//    }
//    val scope = rememberCoroutineScope()
//
//    Dialog(onDismissRequest = onDismiss) {
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            shape = RoundedCornerShape(16.dp),
//            colors = CardDefaults.cardColors(containerColor = Color.White)
//        ) {
//            Column(
//                modifier = Modifier.padding(24.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    "Would you like to check in?",
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold
//                )
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Button(
//                        onClick = onDismiss,
//                        modifier = Modifier.weight(1f),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color.Gray
//                        )
//                    ) {
//                        Text("Cancel")
//                    }
//
//                    Button(
//                        onClick = {
//                            scope.launch {
//                                try {
//                                    // 1. 위치 확인
//                                    val isNearLocation =
//                                        viewModel.checkUserArrival(fusedLocationClient)
//
//                                    if (isNearLocation) {
//                                        // 2. 도착한 사람 수 확인
//                                        val arrivedCount = viewModel.uiState.arrivalStatus.count {
//                                            it.value.participantArrivalStatus == "ARRIVED"
//                                        }
//
//                                        if (arrivedCount == 0) {
//                                            // 3. 첫 번째 도착자 처리
//                                            viewModel.createQRCode()
//                                            viewModel.registerArrival()
//                                            onDismiss()
//                                            onShowQrDialog()
//                                            Toast.makeText(
//                                                context,
//                                                "First arrival! The QR code has been generated.",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                        } else {
//                                            // 4. 이후 도착자 처리
//                                            onDismiss()
//                                            onShowScanner()
//                                        }
//                                    } else {
//                                        Toast.makeText(
//                                            context,
//                                            "You have not arrived yet. Please move within 100m of the meeting place.",
//                                            Toast.LENGTH_LONG
//                                        ).show()
//                                        onDismiss()
//                                    }
//                                } catch (e: Exception) {
//                                    Toast.makeText(
//                                        context,
//                                        "오류가 발생했습니다: ${e.message}",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                    onDismiss()
//                                }
//                            }
//                        },
//                        modifier = Modifier.weight(1f),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = MainColor
//                        )
//                    ) {
//                        Text("Confirm")
//                    }
//                }
//            }
//        }
//    }
//}
@Composable
fun CheckInConfirmDialog(
    viewModel: TeamDetailViewModel,
    onDismiss: () -> Unit,
    onShowScanner: () -> Unit,
    onShowQrDialog: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val scope = rememberCoroutineScope()

    // 현재 시간과 약속 시간 비교
    val currentTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
    val meetingDateTime = viewModel.uiState.meeting?.meetingDateTime?.let {
        LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
            .toLocalDateTime()
    }
    val isLate = meetingDateTime?.let { currentTime.isAfter(it) } ?: false

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLate) {
                    // 지각한 경우의 UI
                    Text(
                        "You are late!!!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Check-in is not possible as the appointment time has passed.\n" +
                                "Please pay the late fee.",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainColor
                        )
                    ) {
                        Text("Confirm")
                    }
                } else {
                    // 정상 체크인 UI
                    Text(
                        "Would you like to check in?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray
                            )
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        val isNearLocation =
                                            viewModel.checkUserArrival(fusedLocationClient)
                                        if (isNearLocation) {
                                            val arrivedCount =
                                                viewModel.uiState.arrivalStatus.count {
                                                    it.value.participantArrivalStatus == "ARRIVED"
                                                }
                                            if (arrivedCount == 0) {
                                                viewModel.createQRCode()
                                                viewModel.registerArrival()
                                                onDismiss()
                                                onShowQrDialog()
                                                Toast.makeText(
                                                    context,
                                                    "First arrival! The QR code has been generated.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                onDismiss()
                                                onShowScanner()
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "You have not arrived yet. Please move within 100m of the meeting place.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            onDismiss()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "오류가 발생했습니다: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onDismiss()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MainColor
                            )
                        ) {
                            Text("확인")
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun ArrivedDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Checking In...",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(color = MainColor)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainColor,
                        contentColor = Color.White
                    )
                ) {
                    Text("Close")
                }
            }
        }
    }
}
