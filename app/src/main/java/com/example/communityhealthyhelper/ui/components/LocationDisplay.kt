package com.example.communityhealthyhelper.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.communityhealthyhelper.utils.PermissionManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Simple location display component that doesn't require Google Maps
 * This can be used for testing without a billing account
 */
@Composable
fun LocationDisplay(
    onLocationFound: (Location) -> Unit = {}
) {
    val context = LocalContext.current
    val permissionManager = remember { PermissionManager(context) }
    val coroutineScope = rememberCoroutineScope()
    
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var locationPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                val cancellationTokenSource = CancellationTokenSource()
                val location = fusedLocationClient.getCurrentLocation(
                    100, // PRIORITY_HIGH_ACCURACY
                    cancellationTokenSource.token
                ).await()
                
                location?.let {
                    currentLocation = it
                    onLocationFound(it)
                }
            } catch (e: Exception) {
                // Handle location exception
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Current Location",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(8.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (currentLocation != null) {
                    Text(
                        text = "Latitude: ${currentLocation?.latitude}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "Longitude: ${currentLocation?.longitude}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "Accuracy: ${currentLocation?.accuracy} meters",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else if (!locationPermissionGranted) {
                    Text(
                        text = "Location permission not granted",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = {
                            // Request permission through your permission manager
                            // This will need activity context, so it should be handled 
                            // at a higher level in a real app
                        }
                    ) {
                        Text("Grant Permission")
                    }
                } else {
                    Text(
                        text = "Getting location...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                if (locationPermissionGranted) {
                    coroutineScope.launch {
                        try {
                            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                            val cancellationTokenSource = CancellationTokenSource()
                            val location = fusedLocationClient.getCurrentLocation(
                                100, // PRIORITY_HIGH_ACCURACY
                                cancellationTokenSource.token
                            ).await()
                            
                            location?.let {
                                currentLocation = it
                                onLocationFound(it)
                            }
                        } catch (e: Exception) {
                            // Handle location exception
                        }
                    }
                }
            },
            enabled = locationPermissionGranted
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh"
            )
            Text(text = "Refresh Location")
        }
    }
}
