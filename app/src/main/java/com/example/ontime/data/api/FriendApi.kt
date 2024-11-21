package com.example.ontime.data.api

import com.example.ontime.data.model.request.AddFriendRequest
import com.example.ontime.data.model.response.FriendResponse
import com.example.ontime.data.model.response.FriendshipRequestListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FriendApi {
    @GET("/api/v1/friendship/{userId}")
    suspend fun getFriends(@Path("userId") userId: String): Response<List<FriendResponse>>

    @POST("/api/v1/friendship")
    suspend fun addFriend(@Body request: AddFriendRequest): Response<Unit>

    @GET("/api/v1/friendship/request/{userId}")
    suspend fun getFriendshipRequestList(@Path("userId") userId: String): Response<List<FriendshipRequestListResponse>>

}