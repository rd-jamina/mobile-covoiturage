package com.example.app_covoiturage.presentation.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun TripMapScreen(
    originLat: Double,
    originLng: Double,
    destinationLat: Double,
    destinationLng: Double,
    viewModel: TripMapViewModel = hiltViewModel()
) {
    val origin = GeoPoint(originLat, originLng)
    val destination = GeoPoint(destinationLat, destinationLng)
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(originLat, originLng, destinationLat, destinationLng) {
        viewModel.loadRoute(origin.latitude, origin.longitude, destination.latitude, destination.longitude)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    val midLat = (originLat + destinationLat) / 2
                    val midLng = (originLng + destinationLng) / 2
                    controller.setZoom(9.0)
                    controller.setCenter(GeoPoint(midLat, midLng))

                    val originMarker = Marker(this).apply {
                        position = origin
                        title = "Départ"
                    }
                    val destMarker = Marker(this).apply {
                        position = destination
                        title = "Arrivée"
                    }
                    overlays.add(originMarker)
                    overlays.add(destMarker)
                }
            },
            update = { mapView ->
                mapView.overlays.removeAll { it is Polyline }
                if (state.routePoints.isNotEmpty()) {
                    val polyline = Polyline().apply {
                        setPoints(state.routePoints.map { GeoPoint(it.latitude, it.longitude) })
                        outlinePaint.color = Color(0xFF6200EE).toArgb()
                        outlinePaint.strokeWidth = 8f
                    }
                    mapView.overlays.add(polyline)
                    mapView.invalidate()
                }
            }
        )

        if (state.isLoading) {
            Box(modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)) {
                CircularProgressIndicator()
            }
        }
    }
}