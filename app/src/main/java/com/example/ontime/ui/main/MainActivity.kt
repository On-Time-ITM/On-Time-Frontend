package com.example.ontime.ui.main

import AppBar
import CustomButton
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ontime.R
import com.example.ontime.ui.theme.MainColor
import com.example.ontime.ui.theme.OnTimeTheme
import com.example.ontime.ui.theme.body_large
import com.example.ontime.ui.theme.shadow
import com.example.ontime.ui.theme.surfaceContainerLowest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            OnTimeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        MainPage()
                    }
                }
            }
        }
    }
}

@Composable
fun MainPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceContainerLowest)
    ) {
        // App Bar
        AppBar()

        // User Stats Card
        UserStatsCard()

        // Team Section
        TeamSection()
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
                modifier = Modifier
                    .size(56.dp)
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

data class team(
    val title: String,
    val number: Int,
    val date: String,
    val time: String,
    val location: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamSection() {
    val teams = remember {
        mutableStateListOf(
            team("런던즈", 5, "24/10/25", "10:00", "프론티어관"),
            team("Meeting", 123, "2024-11-16", "14:00", "Room A"),
            team("Workshop", 456, "2024-11-17", "10:00", "Room B")
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
                    // 50% 이상 스와이프 시 삭제
                    positionalThreshold = { it * 0.7f }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = false,
                    backgroundContent = {
                        DismissBackground()
                    }
                ) {
                    TeamCard(team.title, team.number, team.date, team.time, team.location)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                CustomButton(
                    text = "Add a New Team",
                    onClick = { /* Handle new team creation */ },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            item {
                Button(onClick = { Log.d("ITM", teams.joinToString { it.toString() }) }) {
                    Text("로그 출력") // 버튼에 텍스트 추가
                }
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MainColor
            )
//            Spacer(modifier = Modifier.width(8.dp))
//            Text(
//                text = "삭제",
//                style = TextStyle(
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Bold
//                ),
//                color = Color.White
//            )
        }
    }
}


@Composable
private fun TeamCard(title: String, number: Int, date: String, time: String, location: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    title,
                    fontSize = 20.sp,
                    color = shadow,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "${number}명",
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
                    date,
                    fontSize = body_large,
                    color = shadow
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    time,
                    fontSize = body_large,
                    color = shadow
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    location,
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OnTimeTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding)
            ) {
                MainPage()
            }
        }
    }
}