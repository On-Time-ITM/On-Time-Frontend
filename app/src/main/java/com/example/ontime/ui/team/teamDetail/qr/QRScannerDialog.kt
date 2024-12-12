package com.example.ontime.ui.team.teamDetail.qr

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ontime.ui.theme.MainColor
import com.example.ontime.ui.theme.shadow
import com.example.ontime.ui.theme.surfaceContainerLowest
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch

@Composable
fun QRScannerDialog(
    onDismiss: () -> Unit,
    onQrCodeScanned: suspend (String) -> Unit  // suspend 함수로 변경
) {
    val scope = rememberCoroutineScope()  // 코루틴 스코프 추가

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
    ) { result ->
        result.contents?.let { qrCode ->
            scope.launch {  // 코루틴 스코프 내에서 실행
                try {
                    onQrCodeScanned(qrCode)
                } catch (e: Exception) {


                }
            }
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceContainerLowest)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "QR Code Check-in",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = shadow
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Scan the QR code to check in",
                    fontSize = 14.sp,
                    color = shadow,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val options = ScanOptions().apply {
                            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                            setPrompt("Align QR code within the frame")
                            setBeepEnabled(true)
                            setBarcodeImageEnabled(true)
                            setOrientationLocked(true)
                        }
                        scannerLauncher.launch(options)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainColor,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open Scanner")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Cancel",
                        color = MainColor
                    )
                }
            }
        }
    }
}