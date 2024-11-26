package com.example.ontime.ui.calendar

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor() : ViewModel() {

    //    var date by mutableStateOf(LocalDate.now())
//        private set
//
//    fun confirmDate(selectedDate: LocalDate) {
//        viewModelScope.launch {
//            try {
//                date = selectedDate
//            } catch (e: Exception) {
//
//            }
//        }
//    }


    // 선택된 날짜 상태
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    // 선택된 시간 상태 추가
    private val _selectedTime = MutableStateFlow(LocalTime.of(12, 0)) // 기본값 12:00
    val selectedTime = _selectedTime.asStateFlow()

    fun updateDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun updateTime(hour: Int, minute: Int) {
        _selectedTime.value = LocalTime.of(hour, minute)
    }
}