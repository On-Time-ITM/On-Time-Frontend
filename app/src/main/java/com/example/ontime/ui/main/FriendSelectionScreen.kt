package com.example.ontime.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ontime.data.model.response.FriendResponse
import com.example.ontime.ui.component.AppBar
import com.example.ontime.ui.component.CustomButton
import com.example.ontime.ui.friend.friendList.FriendListViewModel
import com.example.ontime.ui.theme.MainColor


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FriendSelectionScreen(
    viewModel: FriendListViewModel,
    modifier: Modifier = Modifier,
) {
    var selectedFriends by remember { mutableStateOf<List<FriendResponse>>(emptyList()) }

    val uiState = viewModel.uiState


    LaunchedEffect(Unit) {
        viewModel.getFriendsList()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppBar()

        // Box를 사용하여 content와 button을 분리
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {

                Text(
                    text = "Friends",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                // Selected Friends Section
                if (uiState.selectedFriends.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
//                        .background(Color(0xFFF5F5F5))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Selected Friends (${uiState.selectedFriends.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.selectedFriends.forEach { friend ->
                                SelectedFriendChip(
                                    friend = friend,
                                    onRemove = {
                                        selectedFriends =
                                            selectedFriends.filter { it.id != friend.id }
                                    }
                                )
                            }
                        }
                    }
                    HorizontalDivider()
                }

                // 친구 목록
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                } else if (uiState.friends.isEmpty()) {
                    EmptyFriendsList()
                } else {
                    LazyColumn {
                        items(uiState.friends) { friend ->
                            SelectableFriendRow(
                                friend = friend,
                                isSelected = uiState.selectedFriends.contains(friend),
                                onSelectionChanged = { isSelected ->
                                    if (isSelected) {
                                        viewModel.addFriend(friend)
                                    } else {
                                        viewModel.removeFriend(friend)
                                    }
                                }
                            )
                            HorizontalDivider(
                                color = Color.LightGray.copy(alpha = 0.5f),
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }
            // 하단 고정 버튼
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp)
            ) {
                CustomButton(
                    text = "Confirm ${if (uiState.selectedFriends.isNotEmpty()) "(${uiState.selectedFriends.size})" else ""}",
                    onClick = {
                        viewModel.confirmFriendSelection()
                    },
                    enabled = uiState.selectedFriends.isNotEmpty()
                )
            }
        }
    }
}


@Composable
private fun SelectedFriendChip(
    friend: FriendResponse,
    onRemove: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier.height(32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = friend.name,
                fontSize = 14.sp,
                modifier = Modifier.padding(end = 4.dp)
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun SelectableFriendRow(
    friend: FriendResponse,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectionChanged(!isSelected) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = friend.name,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = friend.phoneNumber,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        IconButton(
            onClick = { onSelectionChanged(!isSelected) },
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (isSelected) MainColor else Color.LightGray.copy(alpha = 0.2f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = if (isSelected) "Selected" else "Not Selected",
                tint = if (isSelected) Color.White else Color.Gray
            )
        }
    }
}

@Composable
private fun EmptyFriendsList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.Person,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(bottom = 8.dp),
            tint = Color.Gray
        )
        Text(
            text = "No friends yet",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}
