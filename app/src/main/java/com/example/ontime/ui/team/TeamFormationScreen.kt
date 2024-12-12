package com.example.ontime.ui.team


import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ontime.R
import com.example.ontime.data.model.account.AccountData
import com.example.ontime.ui.component.AppBar
import com.example.ontime.ui.component.CustomButton
import com.example.ontime.ui.component.TitleInputDialog
import com.example.ontime.ui.theme.MainColor
import com.example.ontime.ui.theme.shadow
import com.example.ontime.ui.theme.surfaceContainerLowest
import kotlinx.coroutines.delay

@Composable
private fun TeamSuccessScreen(
    teamName: String,
    onNavigateToDetail: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2000) // 2초 후 팀 상세 페이지로 이동
        onNavigateToDetail()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceContainerLowest),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ontime_logo),
                contentDescription = "Success Icon",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "[$teamName]이\n생성되었습니다",
                fontSize = 24.sp,
                color = shadow,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )
        }
    }
}

@Composable
fun SafeBase64Image(
    base64String: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    fallbackResId: Int = R.drawable.ontime_logo
) {
    val bitmap = base64String?.let { base64 ->
        remember(base64) {
            try {
                val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    } else {
        Image(
            painter = painterResource(id = fallbackResId),
            contentDescription = contentDescription,
            modifier = modifier
        )
    }
}

@Composable
fun TeamFormationScreen(
    onCalendarClick: () -> Unit,
    onSetLocationClick: () -> Unit,
    onFriendSelectionClick: () -> Unit,
    onNavigateToTeamDetail: (String) -> Unit,
    viewModel: TeamFormationViewModel = hiltViewModel()
) {
    var showTitleDialog by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }
    val formState by viewModel.formState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var showLogoDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val currentState = uiState) {
            is TeamFormationState.Success -> {
                TeamSuccessScreen(
                    teamName = currentState.meetingTitle,
                    onNavigateToDetail = { onNavigateToTeamDetail(currentState.meetingId) }
                )
            }

            TeamFormationState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MainColor)
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(surfaceContainerLowest)
                ) {

//                    Button(onClick = { viewModel.getValue() }) {
//
//                    }
                    AppBar()

                    Text(
                        "Make a new Team",
                        fontSize = 25.sp,
                        color = shadow,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
                    )
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showLogoDialog = true
                                    }
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFFE0E0E0),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // 이미지 표시 부분을 조건부로 수정
                                if (formState.logoUrl != null) {
                                    SafeBase64Image(
                                        base64String = formState.logoUrl,
                                        contentDescription = "Team Logo",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .border(
                                                width = 1.dp,
                                                color = Color(0xFFE0E0E0),
//                                                shape = RoundedCornerShape(24.dp)
                                            )
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.ontime_logo),
                                        contentDescription = "Team Logo",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .border(
                                                width = 1.dp,
                                                color = Color(0xFFE0E0E0),
                                                shape = RoundedCornerShape(24.dp)
                                            )
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Make your own team logo!",
                                        fontSize = 16.sp,
                                        color = shadow
                                    )
                                    Text(
                                        text = "Click to customize your team's identity",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                        // Title input
                        InputRow(
                            text = if (formState.title.isBlank()) "Enter the schedule title" else formState.title,
                            icon = Icons.Filled.Edit,
                            onClick = { showTitleDialog = true }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Friend selection
                        InputRow(
                            text = if (formState.membersList.isEmpty()) {
                                "Select friends to join the schedule"
                            } else {
                                formState.membersList.joinToString(", ") { it.name }
                            },
                            icon = Icons.Filled.Person,
                            onClick = onFriendSelectionClick
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Set the location
                        InputRow(
                            text = if (formState.location.isEmpty()) "Set the location"
                            else formState.location,
                            icon = Icons.Filled.LocationOn,
                            onClick = onSetLocationClick
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Bank account and Late Fee
                        InputRow(
                            text = "",
                            icon = R.drawable.bank,
                            onClick = { showAccountDialog = true }
                        ) {
                            formState.bankAccount?.let { account ->
                                AccountInfoDisplay(account)
                            } ?: Text(
                                text = "Enter bank account & late fee",
                                color = shadow,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))


                        // Date and Time Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = null,
                                tint = shadow,
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(end = 12.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .clickable(onClick = onCalendarClick),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                DateTimeInput(
                                    modifier = Modifier
                                        .weight(2f),
                                    text = formState.date.ifEmpty { "yyyy/mm/dd" },
                                )
                                DateTimeInput(
                                    modifier = Modifier.weight(1f),
                                    text = formState.time.ifEmpty { "hh:mm" },
                                )
                            }

                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        CustomButton(
                            text = "Make a team",
                            onClick = { viewModel.createTeam() },
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }

                    // title 다이얼로그
                    TitleInputDialog(
                        showDialog = showTitleDialog,
                        onDismiss = { showTitleDialog = false },
                        currentTitle = formState.title,
                        onTitleChange = { viewModel.updateTitle(it) },
                        onConfirm = {
                            showTitleDialog = false
                        }
                    )
                    // 계좌 정보 다이얼로그
                    AccountInputDialog(
                        showDialog = showAccountDialog,
                        onDismiss = { showAccountDialog = false },
                        currentAccount = formState.bankAccount,
                        onConfirm = { account ->
                            viewModel.updateAccount(account)
                        }
                    )
                    // 다이얼로그 추가
                    LogoGenerationDialog(
                        showDialog = showLogoDialog,
                        onDismiss = { showLogoDialog = false },
                        onConfirm = { url -> viewModel.updateLogo(url) },
                        currentLogo = formState.logoUrl,
                        viewModel = viewModel  // viewModel 전달
                    )
                }

            }
        }
    }
}

fun Int.toDecimalFormat(): String {
    return String.format("%,d", this)
}

@Composable
private fun AccountInfoDisplay(
    bankAccount: AccountData
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "${bankAccount.bankName} - ${bankAccount.accountNumber} (${bankAccount.accountHost})",
            color = shadow,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Late Fee",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                text = "${bankAccount.lateFee.toDecimalFormat()}원",
                color = MainColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
private fun InputRow(
    text: String,
    icon: Any,
    onClick: () -> Unit,
    content: @Composable (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        if (icon is ImageVector) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = shadow,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 12.dp)
            )
        } else if (icon is Int) {  // R.drawable 리소스 ID
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = shadow,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 12.dp)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp),
            colors = CardDefaults.cardColors(Color(0x1FE9E9E9))
        ) {
            if (content != null) {
                Box(modifier = Modifier.padding(16.dp)) {
                    content()
                }
            } else {
                Text(
                    text = text,
                    color = shadow,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun DateTimeInput(
    modifier: Modifier = Modifier,
    text: String,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(Color(0x1FE9E9E9))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = text,
                color = shadow,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}