package com.example.ontime.ui.calendar

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
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
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    fun updateDate(date: LocalDate) {
        _selectedDate.value = date
    }

}