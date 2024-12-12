package com.example.ontime.data.model.response

data class ParticipantArrivalInfo(
    val meetingId: String,
    val participantId: String,
    val arrivalTime: String?, // arrivalTime이 null일 수 있으므로 nullable로 설정
    val participantArrivalStatus: String,
    val late: Boolean
)
