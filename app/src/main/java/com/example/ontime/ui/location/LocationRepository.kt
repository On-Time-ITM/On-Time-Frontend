package com.example.ontime.ui.location

import com.example.ontime.data.api.NominatimApi
import com.example.ontime.data.model.nominatim.NominatimResult
import com.example.ontime.data.model.nominatim.SearchResult
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val nominatimApi: NominatimApi
) {
    suspend fun searchLocations(query: String): List<SearchResult> {
        return nominatimApi.searchLocation(query)
            .map { result: NominatimResult -> result.toSearchResult() }  // 확장 함수 사용
    }

    suspend fun reverseGeocode(latLng: LatLng): String {
        return nominatimApi.reverseGeocode(
            latitude = latLng.latitude,
            longitude = latLng.longitude
        ).displayName
    }
}