package com.example.ontime.data.model.response


data class MeetingParticipantsResponse(
    val meetingId: String,
    val participantLocationInfos: List<ParticipantLocationInfo>
)

data class ParticipantLocationInfo(
    val participantId: String,
    val participantLocation: ParticipantLocation
)

data class ParticipantLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String
)

