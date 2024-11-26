package com.example.ontime.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ontime.ui.component.CustomButton
import com.example.ontime.ui.theme.MainColor
import java.time.LocalDate
import java.time.YearMonth

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

// 사용 예시
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onConfirm: (LocalDate) -> Unit
) {
//    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val selectedDate by viewModel.selectedDate.collectAsState()

    Column {
        CalendarView(
            onDateSelected = { date ->
                viewModel.updateDate(date)
//                selectedDate = date
                // 여기서 선택된 날짜 처리
//                viewModel.confirmDate(selectedDate)
            },
            selectedDate = selectedDate
        )
        Spacer(modifier = Modifier.weight(1f))
        CustomButton(
            text = "Confirm",
            onClick = { onConfirm(selectedDate) },
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 40.dp)
        )
    }

}