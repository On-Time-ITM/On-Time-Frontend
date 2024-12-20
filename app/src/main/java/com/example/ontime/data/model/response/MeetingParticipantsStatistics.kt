package com.example.ontime.data.model.response

data class MeetingParticipantsStatistics(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val statistics: StatisticsInfo
)

data class StatisticsInfo(
    val totalMeetings: Int,
    val totalArrivedMeetings: Int,
    val totalLateMeetings: Int,
    val lateRate: Double
)
