package com.example.ontime.ui.location

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ontime.data.model.nominatim.SearchResult
import com.example.ontime.ui.component.AppBar
import com.example.ontime.ui.component.CustomButton
import com.example.ontime.ui.theme.MainColor
import com.example.ontime.ui.theme.body_large
import com.example.ontime.ui.theme.surfaceContainerLowest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun SearchBar(
    searchText: String,
    searchResults: List<SearchResult>,
    onSearchChange: (String) -> Unit,
    onSearch: () -> Unit,
    onResultSelect: (SearchResult) -> Unit
) {
    Column {  // Column으로 감싸서 검색창과 결과를 수직으로 배치
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    top = 10.dp,
                    start = 18.dp,
                    end = 18.dp,
                    bottom = 10.dp
                )
                .clip(
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (searchResults.isEmpty()) 20.dp else 0.dp,
                        bottomEnd = if (searchResults.isEmpty()) 20.dp else 0.dp
                    )
                )
                .fillMaxWidth()
                .background(color = Color(0x99E8E8E8))
                .padding(horizontal = 15.dp)
                .height(40.dp)
        ) {
            BasicTextField(
                value = searchText,
                onValueChange = {
                    onSearchChange(it)
                    if (it.length >= 2) {  // 2글자 이상일 때만 검색
                        onSearch()
                    }
                },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = body_large,
                    color = Color.Black
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .align(Alignment.CenterVertically),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchText.isEmpty()) {
                            Text(
                                "Search for a location",
                                color = Color(0xFFBCBCBC),
                                fontSize = body_large
                            )
                        }
                        innerTextField()
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { onSearch() }
                ),
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onSearch,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFFBCBCBC)
                )
            }
        }

        // 검색 결과 표시
        if (searchResults.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                color = Color.White,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            ) {
                Column {
                    searchResults.forEach { result ->
                        Text(
                            text = result.displayName,
                            modifier = Modifier
                                .clickable {
                                    onResultSelect(result)
                                }
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                        )
                        if (result != searchResults.last()) {
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocationSelectionScreen(
    viewModel: LocationSelectionViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState
    val cameraPositionState = rememberCameraPositionState()

    // 카메라 위치 이벤트 처리
    LaunchedEffect(Unit) {
        viewModel.cameraPositionEvent.collect { newLocation ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(newLocation, 15f)
                )
            )
        }
    }

    // 현재 위치가 업데이트되면 카메라 이동
    LaunchedEffect(uiState.currentLocation) {
        uiState.currentLocation?.let { location ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.values.reduce { acc, next -> acc && next }
        viewModel.updatePermissionStatus(isGranted)
        if (isGranted) {
            viewModel.getCurrentLocation()  // 권한이 승인되면 현재 위치를 가져옴
        }
    }

    LaunchedEffect(Unit) {
        if (!viewModel.checkLocationPermission()) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            viewModel.updatePermissionStatus(true)
            viewModel.getCurrentLocation()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceContainerLowest)
    ) {
        AppBar()

        SearchBar(
            searchText = uiState.searchText,
            searchResults = uiState.searchResults,
            onSearchChange = viewModel::onSearchChange,
            onSearch = { viewModel.searchLocations() },
            onResultSelect = { result -> viewModel.selectSearchResult(result) }
        )

        Box(
            modifier = Modifier
                .weight(1f) // SearchBar와 Button 사이의 공간을 모두 차지
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            if (uiState.isPermissionGranted) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,  // 미리 선언한 cameraPositionState 사용

                    properties = MapProperties(isMyLocationEnabled = true),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = true
                    ),
                    onMapClick = viewModel::updateSelectedLocation
                ) {
                    // 선택된 위치에 마커 표시
                    uiState.selectedLocation?.let { location ->
                        Marker(
                            state = MarkerState(position = location),
                            title = "Selected Location"
                        )
                    }
                }
            } else {
                // 권한이 없을 때의 UI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Location permission is required to use the map",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    ) {
                        Text("Grant Permission")
                    }
                }
            }

            // 로딩 인디케이터 (선택적)
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp),
                    color = MainColor
                )
            }
        }

        CustomButton(
            text = "Confirm",
            onClick = { viewModel.confirmLocation() },
            enabled = uiState.selectedLocation != null,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 10.dp)
                .padding(top = 20.dp, bottom = 60.dp)
        )
    }

    // Error handling
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error message (using Toast or Snackbar)
            viewModel.clearError()
        }
    }
}












