package com.example.ontime.ui.main

import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.ontime.R
import com.example.ontime.data.model.response.MeetingResponse
import com.example.ontime.ui.component.AppBar
import com.example.ontime.ui.component.CustomButton
import com.example.ontime.ui.component.CustomDialog
import com.example.ontime.ui.theme.MainColor
import com.example.ontime.ui.theme.SubColor
import com.example.ontime.ui.theme.body_large
import com.example.ontime.ui.theme.body_medium
import com.example.ontime.ui.theme.shadow
import com.example.ontime.ui.theme.surfaceContainerLowest
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onAddTeamClick: () -> Unit,
    onTeamClick: (MeetingResponse) -> Unit,
    onFriendClick: () -> Unit,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {

    val uiState = viewModel.uiState
    val userName = viewModel.getUserName()
    val tardinessRate = viewModel.getTardinessRate()
    val context = LocalContext.current

    // Lifecycle key를 사용하여 화면이 재진입될 때마다 리프레시되도록 함
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getMeetingList()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 에러 발생 시 로그 출력
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Log.d("ITM", "Error occurred: $error")
        }
    }

    // 삭제 에러 처리
    LaunchedEffect(uiState.deleteError) {
        uiState.deleteError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            Log.d("ITM", "error occurred: $error")
        }
    }

    // 삭제 성공 메시지 처리
    LaunchedEffect(uiState.showDeleteSuccessMessage) {
        if (uiState.showDeleteSuccessMessage) {
            Toast.makeText(
                context,
                "Team has been successfully deleted",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.clearDeleteSuccessMessage()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceContainerLowest)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppBar()
//            Row {
//                Button(onClick = { Log.d("ITM", "${viewModel.userId}") }) {
//                    Text(text = "userId")
//                }
//                Button(onClick = { viewModel.logout() }) {
//                    Text("Logout")
//                }
//            }
            UserStatsCard(userName = userName, tardinessRate = tardinessRate)

            // Meeting List Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .padding(bottom = 60.dp)
                    .weight(1f)
            ) {
                Text(
                    "Team",
                    fontSize = body_large,
                    color = shadow,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when {
                        uiState.isLoading && uiState.meetingList.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(36.dp),
                                    color = MainColor
                                )
                            }
                        }

                        uiState.meetingList.isEmpty() -> {
                            // 빈 상태 UI
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        "아직 팀이 없습니다",
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        "새로운 팀을 만들어보세요",
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        else -> {
                            TeamListContent(
                                meetings = uiState.meetingList,
                                onTeamClick = onTeamClick,
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                CustomButton(
                    text = "Add a New Team",
                    onClick = onAddTeamClick
                )
            }
        }

        BottomNavigationButtons(
            onFriendClick = onFriendClick,
            onLogout = onLogout,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun BottomNavigationButtons(
    onFriendClick: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NavigationButton(
                text = "Friends",
                icon = R.drawable.profile_icon,
                onClick = onFriendClick,
                color = MainColor,
                modifier = Modifier.weight(1f)
            )

            NavigationButton(
                text = "Logout",
                icon = R.drawable.out,
                onClick = onLogout,
                color = Color.Red.copy(alpha = 0.6f),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NavigationButton(
    text: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, color),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TeamCard(
    meeting: MeetingResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(Color.White),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    meeting.name,
                    fontSize = 20.sp,
                    color = shadow,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    "${meeting.participantCount}명",
//                    fontSize = body_large,
//                    color = shadow
//                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val formattedDateTime = try {
                    val localDateTime = LocalDateTime.parse(
                        meeting.meetingDateTime,
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    )
                    // UTC 시간을 KST로 변환
                    val koreaZoneId = ZoneId.of("Asia/Seoul")
                    val koreaDateTime = localDateTime
                        .atZone(ZoneId.of("UTC"))  // UTC로 명시적 지정
                        .withZoneSameInstant(koreaZoneId)
                        .toLocalDateTime()

                    koreaDateTime.format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm"))
                } catch (e: Exception) {
                    Log.e("ITM", "DateTime formatting failed: ${e.message}", e)
                    meeting.meetingDateTime
                }
                Text(
                    formattedDateTime,
                    fontSize = body_large,
                    color = shadow
                )
                Spacer(modifier = Modifier.width(8.dp))
                val address = meeting.location.address
                val shortenedAddress =
                    address.split(",").firstOrNull() ?: address // 첫 쉼표 전 부분만 가져오기
                Text(
                    shortenedAddress,
                    fontSize = body_large,
                    color = shadow
                )
                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "More",
                    tint = shadow,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun DismissBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = MainColor
        )
    }
}


@Composable
fun UserStatsCard(userName: String, tardinessRate: Float) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MainColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-40).dp)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Welcome,",
                    fontSize = 14.sp,
                    color = Color.White,
                )
                Text(
                    text = userName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Average tardiness rate",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        CustomCircularProgressIndicator(
                            percentage = tardinessRate,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomCircularProgressIndicator(
    percentage: Float,
    modifier: Modifier = Modifier,
    foregroundColor: Color = Color.White,
    backgroundColor: Color = Color.White.copy(alpha = 0.2f)
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
            )

            drawArc(
                color = foregroundColor,
                startAngle = -90f,
                sweepAngle = percentage * 3.6f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = "${percentage.toInt()}%",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = foregroundColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamListContent(
    meetings: List<MeetingResponse>,
    onTeamClick: (MeetingResponse) -> Unit,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var meetingToDelete by remember { mutableStateOf<MeetingResponse?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = meetings,
            key = { it.id }
        ) { meeting ->
            SwipeToDismissBox(
                state = rememberSwipeToDismissBoxState(
                    confirmValueChange = { dismissValue ->
                        when (dismissValue) {
                            SwipeToDismissBoxValue.EndToStart -> {
                                meetingToDelete = meeting
                                showDeleteDialog = true
                                false
                            }

                            else -> false
                        }
                    }
                ),
                enableDismissFromStartToEnd = false,
                backgroundContent = {
                    DismissBackground()
                }
            ) {
                TeamCard(
                    meeting = meeting,
                    onClick = { onTeamClick(meeting) }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
    if (showDeleteDialog && meetingToDelete != null) {
        CustomDialog(
            showDialog = true,
            onDismiss = {
                showDeleteDialog = false
                meetingToDelete = null
            },
            title = "Delete team",
            onConfirm = {
                meetingToDelete?.let { meeting ->
                    viewModel.deleteMeeting(meeting.id)
                }
                showDeleteDialog = false
                meetingToDelete = null
            }
        ) {
            Column(
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(
                    "Are you sure you want to delete this team??",
                    fontSize = body_large,
                    color = shadow
                )
                Text(
                    "This action cannot be undone.",
                    fontSize = body_medium,
                    color = SubColor,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}