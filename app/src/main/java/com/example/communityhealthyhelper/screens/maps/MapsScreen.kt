package com.example.communityhealthyhelper.screens.maps

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.communityhealthyhelper.screens.maps.PlaceType.Companion.entries
import com.example.communityhealthyhelper.ui.theme.HealthGreen
import com.example.communityhealthyhelper.utils.MapsInitializer
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await

enum class PlaceType(val label: String, val icon: ImageVector) {
    HOSPITAL("Hospitals", Icons.Default.LocalHospital),
    PHARMACY("Pharmacies", Icons.Default.LocalPharmacy),
    FITNESS("Fitness Centers", Icons.Default.SportsGymnastics);
    
    companion object {
        val entries get() = values().toList()
    }
}

data class PlaceInfo(
    val id: String,
    val name: String,
    val position: LatLng,
    val address: String,
    val type: PlaceType
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasGooglePlayServices by remember { mutableStateOf(false) }
    var mapError by remember { mutableStateOf<String?>(null) }
    var selectedPlaceType by remember { mutableStateOf(PlaceType.HOSPITAL) }
    var selectedPlace by remember { mutableStateOf<PlaceInfo?>(null) }
    
    // Animation states (used for UI effects when map is loading)
    
    // Mock places - in a real app, these would come from Google Places API
    val mockPlaces = remember {
        mutableMapOf(
            PlaceType.HOSPITAL to listOf(
                PlaceInfo(
                    id = "h1",
                    name = "Community General Hospital",
                    position = LatLng(0.0, 0.0),  // Will be updated relative to user location
                    address = "123 Health Avenue",
                    type = PlaceType.HOSPITAL
                ),
                PlaceInfo(
                    id = "h2",
                    name = "Family Health Center",
                    position = LatLng(0.0, 0.0),  // Will be updated relative to user location
                    address = "456 Wellness Street",
                    type = PlaceType.HOSPITAL
                )
            ),
            PlaceType.PHARMACY to listOf(
                PlaceInfo(
                    id = "p1",
                    name = "Community Pharmacy",
                    position = LatLng(0.0, 0.0),  // Will be updated relative to user location
                    address = "789 Medicine Road",
                    type = PlaceType.PHARMACY
                ),
                PlaceInfo(
                    id = "p2",
                    name = "Health First Pharmacy",
                    position = LatLng(0.0, 0.0),  // Will be updated relative to user location
                    address = "321 Remedy Lane",
                    type = PlaceType.PHARMACY
                )
            ),
            PlaceType.FITNESS to listOf(
                PlaceInfo(
                    id = "f1",
                    name = "Community Fitness Center",
                    position = LatLng(0.0, 0.0),  // Will be updated relative to user location
                    address = "555 Wellness Way",
                    type = PlaceType.FITNESS
                ),
                PlaceInfo(
                    id = "f2",
                    name = "Health Zone Gym",
                    position = LatLng(0.0, 0.0),  // Will be updated relative to user location
                    address = "777 Exercise Avenue",
                    type = PlaceType.FITNESS
                )
            )
        )
    }
    
    // Check if Google Play Services are available
    LaunchedEffect(Unit) {
        hasGooglePlayServices = MapsInitializer.isGooglePlayServicesAvailable(context)
        if (!hasGooglePlayServices) {
            mapError = "Google Play Services are not available on this device."
            isLoading = false
        } else {
            // Check location permission
            val hasLocationPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            
            if (hasLocationPermission) {
                locationPermissionGranted = true
                try {
                    // Get current location
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    val locationResult = fusedLocationClient.lastLocation.await()
                    
                    locationResult?.let { location ->
                        currentLocation = LatLng(location.latitude, location.longitude)
                        
                        // Update mock place locations based on current location
                        mockPlaces.values.flatten().forEach { place ->
                            val offsetLat = (Math.random() - 0.5) * 0.01  // Creates a random offset
                            val offsetLng = (Math.random() - 0.5) * 0.01
                            
                            // Using copy instead of reflection to update the position
                            val updatedPlace = place.copy(
                                position = LatLng(
                                    location.latitude + offsetLat,
                                    location.longitude + offsetLng
                                )
                            )
                            // Update the mockPlaces map with the updated place
                            val places = mockPlaces[place.type] ?: emptyList()
                            val updatedPlaces = places.map { if (it.id == place.id) updatedPlace else it }
                            mockPlaces[place.type] = updatedPlaces
                        }
                    } ?: run {
                        mapError = "Could not get your current location."
                    }
                } catch (e: Exception) {
                    mapError = "Error getting location: ${e.message}"
                } finally {
                    isLoading = false
                }
            } else {
                isLoading = false
            }
        }
    }
    
    // Request location permission
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locationPermissionGranted = true
            // Restart the activity to get location
            // In a real app, you would use a viewmodel to reload the data
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Services Map") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            if (locationPermissionGranted && currentLocation != null) {
                FloatingActionButton(
                    onClick = { /* Re-center map on current location */ },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "My Location"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                // Show loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading map...")
                    }
                }
            } else if (mapError != null) {
                // Show error state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Error Loading Map",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = mapError ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (!locationPermissionGranted) {
                // Show permission request UI
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    FiltersRow(
                        selectedType = selectedPlaceType,
                        onTypeSelected = { selectedPlaceType = it }
                    )
                    
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Location Permission Required",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "To find health services near you, please allow location access.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FilledTonalButton(
                            onClick = {
                                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        ) {
                            Text("Grant Permission")
                        }
                    }
                }
            } else {
                // Show map UI
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    FiltersRow(
                        selectedType = selectedPlaceType,
                        onTypeSelected = { selectedPlaceType = it }
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        // Google Map
                        if (currentLocation != null) {
                            val cameraPositionState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(currentLocation!!, 15f)
                            }
                            
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                onMapClick = { selectedPlace = null }
                            ) {
                                // Add user's current location marker
                                Marker(
                                    state = MarkerState(position = currentLocation!!),
                                    title = "You are here",
                                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                                )
                                
                                // Add health service markers
                                mockPlaces[selectedPlaceType]?.forEach { place ->
                                    Marker(
                                        state = MarkerState(position = place.position),
                                        title = place.name,
                                        snippet = place.address,
                                        icon = when (place.type) {
                                            PlaceType.HOSPITAL -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                                            PlaceType.PHARMACY -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                                            PlaceType.FITNESS -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                                        },
                                        onClick = {
                                            selectedPlace = place
                                            true
                                        }
                                    )
                                }
                            }
                            
                            // Show place details card when a place is selected
                            this@Column.AnimatedVisibility(
                                visible = selectedPlace != null,
                                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 16.dp)
                            ) {
                                selectedPlace?.let { place ->
                                    PlaceInfoCard(place = place)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            // Clean up any resources if needed
        }
    }
}

@Composable
fun FiltersRow(
    selectedType: PlaceType,
    onTypeSelected: (PlaceType) -> Unit
) {
    val types = entries
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Find Health Services Near You",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                types.forEach { type ->
                    val isSelected = type == selectedType
                    val backgroundColor = animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                                     else MaterialTheme.colorScheme.surfaceVariant,
                        animationSpec = tween(300),
                        label = "Background Color"
                    )
                    val contentColor = animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                                     else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(300),
                        label = "Content Color"
                    )
                    val scale = animateFloatAsState(
                        targetValue = if (isSelected) 1.1f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "Scale Animation"
                    )
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTypeSelected(type) }
                            .padding(8.dp)
                            .graphicsLayer { 
                                scaleX = scale.value
                                scaleY = scale.value
                            }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(56.dp)
                                .background(backgroundColor.value, CircleShape)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = type.icon,
                                contentDescription = type.label,
                                tint = contentColor.value,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = type.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary 
                                  else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceInfoCard(place: PlaceInfo) {
    // Animation for card appearance
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icon based on place type
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = place.type.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Column {
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = place.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Distance and details (mock data for UI)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Distance",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "1.2 km",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Column {
                    Text(
                        text = "Rating",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "4.7 ★",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Column {
                    Text(
                        text = "Open",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Yes",
                        style = MaterialTheme.typography.titleMedium,
                        color = HealthGreen
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action buttons with animation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = { /* Call facility */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text("Call")
                }
                
                FilledTonalButton(
                    onClick = { /* Open in maps app */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text("Directions")
                }
            }
        }
    }
}
