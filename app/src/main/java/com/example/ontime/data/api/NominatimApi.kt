package com.example.ontime.data.api

import com.example.ontime.data.model.nominatim.NominatimResult
import com.example.ontime.data.model.nominatim.NominatimReverseResult
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApi {
    @GET("search")
    suspend fun searchLocation(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5
    ): List<NominatimResult>

    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("format") format: String = "json",
        @Query("zoom") zoom: Int = 18,
        @Query("addressdetails") addressDetails: Int = 1
    ): NominatimReverseResult
}