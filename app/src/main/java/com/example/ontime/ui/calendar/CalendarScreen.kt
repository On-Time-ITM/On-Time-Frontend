package com.example.ontime.ui.calendar

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ontime.R
import com.example.ontime.ui.theme.MainColor
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarView(
    onDateSelected: (LocalDate) -> Unit,
    selectedDate: LocalDate = LocalDate.now(),
) {
    var currentYearMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    var selected by remember { mutableStateOf(selectedDate) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 년월 표시 및 이전/다음 달 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                currentYearMonth = currentYearMonth.minusMonths(1)
            }) {
                Text("←")
            }

            Text(
                text = "${currentYearMonth.year}년 ${currentYearMonth.monthValue}월",
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(onClick = {
                currentYearMonth = currentYearMonth.plusMonths(1)
            }) {
                Text("→")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 요일 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = if (day == "일") Color.Red else MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 달력 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            // 이전 달의 날짜들로 채우기
            val firstDayOfMonth = currentYearMonth.atDay(1)
            val dayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
            val prevMonthDays = (1..dayOfWeek).map {
                currentYearMonth.minusMonths(1).atDay(
                    currentYearMonth.minusMonths(1).lengthOfMonth() - (dayOfWeek - it)
                )
            }

            items(prevMonthDays) { date ->
                DayCell(
                    date = date,
                    isSelected = false,
                    isCurrentMonth = false,
                    onDateSelected = { },
                    modifier = Modifier.aspectRatio(1f)
                )
            }

            // 현재 달의 날짜들
            val daysInMonth = (1..currentYearMonth.lengthOfMonth()).map {
                currentYearMonth.atDay(it)
            }

            items(daysInMonth) { date ->
                DayCell(
                    date = date,
                    isSelected = date == selected,
                    isCurrentMonth = true,
                    onDateSelected = {
                        selected = date
                        onDateSelected(date)
                    },
                    modifier = Modifier.aspectRatio(1f)
                )
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    isCurrentMonth: Boolean,
    onDateSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(2.dp)
            .background(
                when {
                    isSelected -> MainColor
                    else -> Color.Transparent
                }
            )
            .clickable(enabled = isCurrentMonth) { onDateSelected() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = when {
                !isCurrentMonth -> Color.Gray
                isSelected -> Color.White
                date.dayOfWeek.value == 7 -> Color.Red  // 일요일
                else -> MaterialTheme.colorScheme.onBackground
            }
        )
    }
}
//
//// 사용 예시
//@SuppressLint("DefaultLocale")
//@Composable
//fun CalendarScreen(
//    viewModel: CalendarViewModel,
//    onConfirm: (LocalDate, LocalTime) -> Unit
//) {
////    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
//    val selectedDate by viewModel.selectedDate.collectAsState()
//    val selectedTime by viewModel.selectedTime.collectAsState()
//    val context = LocalContext.current
//
//    Column {
//        CalendarView(
//            onDateSelected = { date ->
//                viewModel.updateDate(date)
////                selectedDate = date
//                // 여기서 선택된 날짜 처리
////                viewModel.confirmDate(selectedDate)
//            },
//            selectedDate = selectedDate
//        )
//        Spacer(modifier = Modifier.weight(1f))
//        // 시간 선택 섹션
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = "약속 시간",
//                style = MaterialTheme.typography.titleMedium
//            )
//
//            // 시간 표시 및 선택 버튼
//            Button(
//                onClick = {
//                    TimePickerDialog(
//                        context,
//                        { _, hour, minute ->
//                            viewModel.updateTime(hour, minute)
//                        },
//                        selectedTime.hour,
//                        selectedTime.minute,
//                        true // 24시간 형식 사용
//                    ).show()
//                },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0x1FE9E9E9),
//                    contentColor = MaterialTheme.colorScheme.onBackground
//                )
//            ) {
//                Text(
//                    text = String.format(
//                        Locale.getDefault(),
//                        "%02d:%02d",
//                        selectedTime.hour,
//                        selectedTime.minute
//                    )
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.weight(1f))
//
//        // 선택된 날짜와 시간 표시
//        Text(
//            text = "선택된 일정: ${
//                selectedDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
//            } ${String.format("%02d:%02d", selectedTime.hour, selectedTime.minute)}",
//            style = MaterialTheme.typography.bodyMedium,
//            modifier = Modifier.padding(horizontal = 16.dp)
//        )
//        CustomButton(
//            text = "Confirm",
//            onClick = { onConfirm(selectedDate, selectedTime) },
//            modifier = Modifier.padding(horizontal = 24.dp, vertical = 40.dp)
//        )
//    }
//
//}

@SuppressLint("DefaultLocale")
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onConfirm: (LocalDate, LocalTime) -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedTime by viewModel.selectedTime.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 선택된 날짜/시간 상단 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MainColor.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedDate.format(
                        DateTimeFormatter.ofPattern(
                            "MM월 dd일 (E)",
                            Locale.KOREAN
                        )
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MainColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        selectedTime.hour,
                        selectedTime.minute
                    ),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MainColor
                )
            }
        }

        // 달력 뷰
        CalendarView(
            onDateSelected = { date ->
                viewModel.updateDate(date)
            },
            selectedDate = selectedDate
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 시간 선택 섹션
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MainColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "약속 시간",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Button(
                    onClick = {
                        TimePickerDialog(
                            context,
                            R.style.CustomTimePickerDialog,
                            { _, hour, minute ->
                                viewModel.updateTime(hour, minute)
                            },
                            selectedTime.hour,
                            selectedTime.minute,
                            true
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            selectedTime.hour,
                            selectedTime.minute
                        ),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 확인 버튼
        Button(
            onClick = { onConfirm(selectedDate, selectedTime) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MainColor,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Confirm",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}