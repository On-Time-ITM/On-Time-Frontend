package com.example.ontime.ui.team


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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ontime.R
import com.example.ontime.data.model.account.AccountData
import com.example.ontime.ui.component.AppBar
import com.example.ontime.ui.component.CustomButton
import com.example.ontime.ui.theme.ButtonText
import com.example.ontime.ui.theme.MainColor
import com.example.ontime.ui.theme.body_medium
import com.example.ontime.ui.theme.shadow
import com.example.ontime.ui.theme.surfaceContainerLowest


@Composable
fun TeamFormationScreen(
    onCalendarClick: () -> Unit,
    onSetLocationClick: () -> Unit,
    onFriendSelectionClick: () -> Unit,
    viewModel: TeamFormationViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var showTitleDialog by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }
    val formState by viewModel.formState.collectAsState()

//    // 계좌 정보 상태
//    var bankName by remember { mutableStateOf("") }
//    var accountNumber by remember { mutableStateOf("") }
//    var accountHolder by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceContainerLowest)
    ) {

        Button(onClick = { viewModel.getValue() }) {

        }
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
//                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* TODO: Navigate to logo creation screen */ }
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ontime_logo),
                        contentDescription = "Team Logo",
                        modifier = Modifier
                            .size(60.dp)  // 로고 크기 조절
                            .border(  // 로고 주변에 원형 테두리 추가
                                width = 1.dp,
                                color = Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(24.dp)
                            )
                    )
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
                text = if (title.isBlank()) "Enter the schedule title" else title,
                icon = Icons.Filled.Edit,
                onClick = { showTitleDialog = true }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Friend selection
            InputRow(
                text = if (formState.members.isEmpty()) "Select friends to join the schedule"
                else "${formState.members.size} friends selected",
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


            // 계좌 정보 입력 Row 추가
            InputRow(
                text = formState.bankAccount?.let { "${it.bankName} - ${it.accountNumber}" }
                    ?: "Enter bank account information",
                icon = R.drawable.bank,
                onClick = { showAccountDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))


            // Date and Time Row
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                        text = formState.date.ifEmpty { "2024/10/25" },
                        icon = Icons.Filled.DateRange
                    )
                    DateTimeInput(
                        modifier = Modifier.weight(1f),
                        text = formState.time.ifEmpty { "10:00" },
                        icon = Icons.Filled.DateRange
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
            currentTitle = title,
            onTitleChange = { title = it },
            onConfirm = {
                showTitleDialog = false
                viewModel.updateTitle(title)
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
    }
}


@Composable
private fun TitleInputDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    currentTitle: String,
    onTitleChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            title = {
                Column {
                    Text(
                        "Enter Schedule Title",
                        fontSize = 20.sp,
                        color = shadow,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                }
            },
            text = {
                TextField(
                    value = currentTitle,
                    onValueChange = onTitleChange,
                    placeholder = { Text("Enter title", color = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0x1FE9E9E9),
                        focusedContainerColor = Color(0x1FE9E9E9),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .width(100.dp)
                            .border(1.dp, MainColor, RoundedCornerShape(10.dp))
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MainColor
                            ),
                            modifier = Modifier.fillMaxSize(), // Box 크기에 맞춤
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Cancel", fontSize = body_medium)
                        }
                    }

                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainColor,
                            contentColor = ButtonText
                        ),
                        modifier = Modifier
                            .height(40.dp)
                            .width(100.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Confirm", fontSize = body_medium)
                    }
                }
            },
            containerColor = surfaceContainerLowest,
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
private fun AccountInputDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    currentAccount: AccountData?,
    onConfirm: (AccountData) -> Unit
) {
    if (showDialog) {
        var bankName by remember { mutableStateOf(currentAccount?.bankName ?: "") }
        var accountNumber by remember { mutableStateOf(currentAccount?.accountNumber ?: "") }
        var accountHolder by remember { mutableStateOf(currentAccount?.accountHolder ?: "") }

        AlertDialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            title = {
                Column {
                    Text(
                        "Enter Bank Account Information",
                        fontSize = 20.sp,
                        color = shadow,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        placeholder = { Text("Bank name", color = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0x1FE9E9E9),
                            focusedContainerColor = Color(0x1FE9E9E9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextField(
                        value = accountNumber,
                        onValueChange = { accountNumber = it },
                        placeholder = { Text("Account number", color = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0x1FE9E9E9),
                            focusedContainerColor = Color(0x1FE9E9E9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextField(
                        value = accountHolder,
                        onValueChange = { accountHolder = it },
                        placeholder = { Text("Account holder", color = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0x1FE9E9E9),
                            focusedContainerColor = Color(0x1FE9E9E9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MainColor
                        ),
                        modifier = Modifier
                            .height(40.dp)
                            .width(100.dp)
                            .border(1.dp, MainColor, RoundedCornerShape(10.dp))
                    ) {
                        Text("Cancel", fontSize = body_medium)
                    }

                    Button(
                        onClick = {
                            onConfirm(
                                AccountData(
                                    bankName = bankName,
                                    accountNumber = accountNumber,
                                    accountHolder = accountHolder
                                )
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainColor,
                            contentColor = ButtonText
                        ),
                        modifier = Modifier
                            .height(40.dp)
                            .width(100.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Confirm", fontSize = body_medium)
                    }
                }
            },
            containerColor = surfaceContainerLowest,
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
private fun InputRow(
    text: String,
    icon: Any,
    onClick: () -> Unit
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
            Text(
                text = text,
                color = shadow,
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun DateTimeInput(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector
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