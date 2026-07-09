package com.example.app_covoiturage.presentation.map

import android.os.Parcelable
import android.view.MotionEvent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.parcelize.Parcelize
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Parcelize
data class PickedLocation(val name: String, val lat: Double, val lng: Double) : Parcelable

private val MADAGASCAR_BOUNDS = BoundingBox(-11.9, 50.5, -25.6, 43.2)
private val ANTANANARIVO = GeoPoint(-18.8792, 47.5079)

@Composable
fun LocationPickerScreen(
    title: String,
    isOrigin: Boolean,
    onBack: () -> Unit,
    onLocationPicked: (PickedLocation) -> Unit,
    viewModel: LocationPickerViewModel = hiltViewModel()
) {
    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleLarge)
        }

        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    Configuration.getInstance().userAgentValue = ctx.packageName

                    val mapView = MapView(ctx)
                    mapView.setTileSource(TileSourceFactory.MAPNIK)
                    mapView.setMultiTouchControls(true)
                    mapView.minZoomLevel = 6.0
                    mapView.maxZoomLevel = 18.0
                    mapView.setScrollableAreaLimitDouble(MADAGASCAR_BOUNDS)
                    mapView.controller.setZoom(12.0)
                    mapView.controller.setCenter(ANTANANARIVO)

                    mapView.setOnTouchListener { _, event ->
                        if (event.action == MotionEvent.ACTION_UP) {
                            val geoPoint = mapView.projection.fromPixels(
                                event.x.toInt(),
                                event.y.toInt()
                            ) as GeoPoint

                            selectedPoint = geoPoint
                            viewModel.loadPlaceName(geoPoint.latitude, geoPoint.longitude)

                            mapView.overlays.removeAll { it is Marker }
                            val marker = Marker(mapView)
                            marker.position = geoPoint
                            marker.title = if (isOrigin) "Départ" else "Arrivée"
                            mapView.overlays.add(marker)
                            mapView.invalidate()
                        }
                        false
                    }

                    mapViewRef = mapView
                    mapView
                }
            )

            // Affiche le nom trouvé pendant la sélection
            if (selectedPoint != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (state.isLoadingName) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Recherche du lieu...")
                            } else {
                                Text(state.placeName ?: "Lieu sélectionné")
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                selectedPoint?.let {
                    onLocationPicked(
                        PickedLocation(
                            name = state.placeName ?: "Lieu sélectionné",
                            lat = it.latitude,
                            lng = it.longitude
                        )
                    )
                }
            },
            enabled = selectedPoint != null && !state.isLoadingName,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Confirmer cet emplacement")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mapViewRef?.onDetach()
        }
    }
}