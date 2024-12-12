package com.example.ontime.data.api

import com.example.ontime.data.model.request.CreateMeetingRequest
import com.example.ontime.data.model.request.CreateQRRequest
import com.example.ontime.data.model.request.DeleteMeetingRequest
import com.example.ontime.data.model.request.GetParticipantLocations
import com.example.ontime.data.model.request.LocationInfo
import com.example.ontime.data.model.response.CreateQRResponse
import com.example.ontime.data.model.response.MeetingParticipantsResponse
import com.example.ontime.data.model.response.MeetingParticipantsStatistics
import com.example.ontime.data.model.response.MeetingResponse
import com.example.ontime.data.model.response.ParticipantArrivalInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MeetingApi {
    @POST("/api/v1/meeting")
    suspend fun createMeeting(@Body request: CreateMeetingRequest): Response<String>

    @HTTP(method = "DELETE", path = "/api/v1/meeting", hasBody = true)
    suspend fun deleteMeeting(@Body request: DeleteMeetingRequest): Response<String>

    @GET("/api/v1/meeting/{meetingId}")
    suspend fun getMeeting(@Path("meetingId") meetingId: String): Response<MeetingResponse>

    @GET("/api/v1/meeting/list/{userId}")
    suspend fun getMeetingList(@Path("userId") userId: String): Response<List<MeetingResponse>>


    // participants API
    @GET("/api/v1/meeting/{meetingId}/location")
    suspend fun getMeetingLocation(@Path("meetingId") meetingId: String): Response<MeetingParticipantsResponse>

    @GET("/api/v1/meeting/{meetingId}/statistics")
    suspend fun getMeetingStatistics(@Path("meetingId") meetingId: String): Response<List<MeetingParticipantsStatistics>>

    @GET("/api/v1/meeting/{meetingId}/arrival")
    suspend fun getParticipantsArrivalList(@Path("meetingId") meetingId: String): Response<List<ParticipantArrivalInfo>>


    // QR code API
    @POST("/api/v1/qr")
    suspend fun createQRCode(@Body request: CreateQRRequest): Response<CreateQRResponse>

    @GET("/api/v1/qr/{meetingId}")
    suspend fun getGRCode(@Path("meetingId") meetingId: String): Response<CreateQRResponse>


    // arrival API
    @PATCH("api/v1/meeting/{meetingId}/arrival/{participantId}")
    suspend fun registerArrival(
        @Path("meetingId") meetingId: String,
        @Path("participantId") participantId: String,
        @Query("arrivalTime") arrivalTime: String
    ): Response<ParticipantArrivalInfo>

    @GET("api/v1/meeting/{meetingId}/arrival/{participantId}")
    suspend fun getParticipantArrival(
        @Path("meetingId") meetingId: String,
        @Path("participantId") participantId: String
    ): Response<ParticipantArrivalInfo>

    //


    @PATCH("/api/v1/meeting/{meetingId}/location/{participantId}")
    suspend fun updateParticipantLocation(
        @Path("meetingId") meetingId: String,
        @Path("participantId") participantId: String,
        @Body request: LocationInfo
    ): Response<Unit>

    @GET("api/v1/meeting/{meetingId}/location")
    suspend fun getParticipantLocations(
        @Path("meetingId") meetingId: String
    ): Response<GetParticipantLocations>


}