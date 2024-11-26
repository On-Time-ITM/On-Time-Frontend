package com.example.ontime.data.model.nominatim

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

// Data classes for Nominatim responses (using Gson annotations instead of kotlinx.serialization)
data class NominatimResult(
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lon")
    val lon: String
) {
    // NominatimResult를 SearchResult로 변환하는 확장 함수
    fun toSearchResult() = SearchResult(
        displayName = displayName,
        lat = lat.toDouble(),
        lon = lon.toDouble()
    )
}

data class SearchResult(
    val displayName: String,
    val lat: Double,
    val lon: Double
) {
    fun toLatLng() = LatLng(lat, lon)
}

data class NominatimReverseResult(
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("address")
    val address: NominatimAddress? = null
)

data class NominatimAddress(
    @SerializedName("road")
    val road: String? = null,
    @SerializedName("house_number")
    val houseNumber: String? = null,
    @SerializedName("suburb")
    val suburb: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("state")
    val state: String? = null,
    @SerializedName("postcode")
    val postcode: String? = null,
    @SerializedName("country")
    val country: String? = null
)