package com.example.ontime.data.model.request

data class GetParticipantLocations(
    val meetingId: String,
    val participantLocationInfos: List<GetParticipantLocationInfo>
)

data class GetParticipantLocationInfo(
    val participantId: String,
    val participantName: String,
    val participantLocation: LocationInfo
)
