package com.example.ontime.ui.main

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ontime.R
import com.example.ontime.ui.auth.logout.LogoutViewModel
import com.example.ontime.ui.component.AppBar
import com.example.ontime.ui.component.CustomButton
import com.example.ontime.ui.theme.MainColor
import com.example.ontime.ui.theme.body_large
import com.example.ontime.ui.theme.shadow
import com.example.ontime.ui.theme.surfaceContainerLowest

@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onAddTeamClick: () -> Unit,
    onTeamClick: (Team) -> Unit,
    onFriendClick: () -> Unit,
    viewModel: LogoutViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceContainerLowest)
    ) {
        AppBar()

// 로그용
        Button(
            onClick = {
                val userId = viewModel.getCurrentUserId()
                Log.d("ITM", "Current User ID: $userId")

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Check Current User ID")
        }

        UserStatsCard()
        TeamSection(
            onTeamClick = onTeamClick,
            onAddTeamClick = onAddTeamClick
        )
        CustomButton(
            text = "Logout",
            onClick = onLogout,
            isLoading = viewModel.isLoading,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        CustomButton(
            text = "Friends",
            onClick = onFriendClick,
            isLoading = viewModel.isLoading,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun UserStatsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0x66000000)),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_icon),
                contentDescription = "profile",
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "임새연",
                    fontSize = 18.sp,
                    color = shadow,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.size(5.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "임새연",
                        fontSize = body_large,
                        color = shadow,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        "님의 평균 지각률은 ",
                        fontSize = body_large,
                        color = shadow
                    )
                    Text(
                        "100%",
                        fontSize = body_large,
                        color = shadow,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamSection(
    onTeamClick: (Team) -> Unit,
    onAddTeamClick: () -> Unit
) {
    val teams = remember {
        mutableStateListOf(
            Team("런던즈", 5, "24/10/25", "10:00", "프론티어관"),
            Team("Meeting", 123, "2024-11-16", "14:00", "Room A"),
            Team("Workshop", 456, "2024-11-17", "10:00", "Room B")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            "Team",
            fontSize = body_large,
            color = shadow,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(
                items = teams,
                key = { team ->
                    "${team.title}_${team.date}_${team.time}"
                }
            ) { team ->
                var show by remember { mutableStateOf(true) }
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { dismissValue ->
                        when (dismissValue) {
                            SwipeToDismissBoxValue.EndToStart -> {
                                show = false
                                teams.remove(team)
                                true
                            }

                            else -> false
                        }
                    },
                    positionalThreshold = { it * 0.7f }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = false,
                    backgroundContent = {
                        DismissBackground()
                    }
                ) {
                    TeamCard(
                        team = team,
                        onClick = { onTeamClick(team) }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                CustomButton(
                    text = "Add a New Team",
                    onClick = onAddTeamClick,
                    modifier = Modifier.padding(top = 8.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamCard(
    team: Team,
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
                    team.title,
                    fontSize = 20.sp,
                    color = shadow,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "${team.number}명",
                    fontSize = body_large,
                    color = shadow
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    team.date,
                    fontSize = body_large,
                    color = shadow
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    team.time,
                    fontSize = body_large,
                    color = shadow
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    team.location,
                    fontSize = body_large,
                    color = shadow
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "More",
                    tint = shadow
                )
            }
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//private fun MainScreenPreview() {
//    OnTimeTheme {
//        MainScreen(
//            onLogout = {},
//            onAddTeamClick = {},
//            onTeamClick = {},
//            viewModel = LogoutViewModel()
//        )
//    }
//}