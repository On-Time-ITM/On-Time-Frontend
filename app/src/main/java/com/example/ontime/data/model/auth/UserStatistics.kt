package com.example.ontime.data.model.auth

data class UserStatistics(
    val totalMeetings: Int = 0,
    val totalArrivedMeetings: Int = 0,
    val totalLateMeetings: Int = 0,
    val lateRate: Double = 0.0
)