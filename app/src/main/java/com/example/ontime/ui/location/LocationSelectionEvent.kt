package com.example.ontime.ui.location

import com.google.android.gms.maps.model.LatLng

sealed class LocationSelectionEvent {
    data class LocationConfirmed(
        val address: String,
        val latLng: LatLng
    ) : LocationSelectionEvent()
}
