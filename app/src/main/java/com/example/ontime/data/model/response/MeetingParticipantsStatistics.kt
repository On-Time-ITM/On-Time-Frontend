package com.example.ontime.data.model.response

data class MeetingParticipantsStatistics(
    val userId: String,
    val statisticsInfo: StatisticsInfo
)

data class StatisticsInfo(
    val totalMeetings: Int,
    val totalArrivedMeetings: Int,
    val totalLateMeetings: Int,
    val lateRate: Double
)
