package com.example.communityhealthyhelper.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Utility class to manage and monitor permissions for testing
 */
class PermissionManager(private val context: Context) {
    
    private val TAG = "PermissionManager"
    
    // Define all permissions required by the app
    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    val smsPermission = Manifest.permission.SEND_SMS
    
    /**
     * Check if a permission is granted
     */
    fun isPermissionGranted(permission: String): Boolean {
        val granted = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
        
        Log.d(TAG, "Permission check for $permission: $granted")
        return granted
    }
    
    /**
     * Check the status of all critical permissions
     */
    fun checkAllPermissions(): Map<String, Boolean> {
        val permissionStatus = mutableMapOf<String, Boolean>()
        
        permissionStatus["location"] = isPermissionGranted(locationPermission)
        permissionStatus["sms"] = isPermissionGranted(smsPermission)
        
        Log.d(TAG, "Permission status: $permissionStatus")
        return permissionStatus
    }
    
    /**
     * Log permission request outcomes for debugging
     */
    fun logPermissionResult(permission: String, isGranted: Boolean) {
        Log.d(TAG, "Permission request for $permission: ${if (isGranted) "GRANTED" else "DENIED"}")
    }
}

/**
 * Composable function to manage permission requests in Compose screens
 */
@Composable
fun rememberPermissionState(
    permission: String,
    onPermissionResult: (Boolean) -> Unit = {}
): PermissionState {
    val context = LocalContext.current
    val permissionState = remember(permission) {
        PermissionState(
            permission = permission,
            isGranted = ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED,
            onPermissionResult = onPermissionResult
        )
    }
    
    return permissionState
}

/**
 * Permission state holder class
 */
class PermissionState(
    val permission: String,
    isGranted: Boolean,
    private val onPermissionResult: (Boolean) -> Unit
) {
    private val TAG = "PermissionState"
    
    var isGranted = isGranted
        private set
    
    fun requestPermission(activity: FragmentActivity) {
        val launcher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            this.isGranted = isGranted
            onPermissionResult(isGranted)
            Log.d(TAG, "Permission request for $permission: ${if (isGranted) "GRANTED" else "DENIED"}")
        }
        
        launcher.launch(permission)
    }
}
