package com.example.communityhealthyhelper

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.communityhealthyhelper.debug.DebugOverlay
import com.example.communityhealthyhelper.navigation.AppNavigation
import com.example.communityhealthyhelper.navigation.Screen
import com.example.communityhealthyhelper.ui.theme.CommunityhealthyhelperTheme
import com.example.communityhealthyhelper.utils.MonitorFramePerformance
import com.example.communityhealthyhelper.utils.PermissionManager
import com.example.communityhealthyhelper.utils.PerformanceLogger

class MainActivity : ComponentActivity() {
    private val tag = "MainActivity"
    private lateinit var permissionManager: PermissionManager
    
    // Debug mode flag for testing - set to true during development
    private val isDebugMode = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize permission manager for testing
        permissionManager = PermissionManager(this)
        
        // Log permission status for testing
        val permissions = permissionManager.checkAllPermissions()
        Log.d(tag, "Initial permission status: $permissions")
        
        // Initialize performance logging
        PerformanceLogger.logDeviceInfo()
        
        setContent {
            CommunityhealthyhelperTheme {
                // Monitor frame performance in debug builds
                MonitorFramePerformance(enabled = isDebugMode)
                
                // Wrap the app with the debug overlay for testing on real devices
                DebugOverlay(enabled = isDebugMode) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        CommunityHealthApp()
                    }
                }
            }
        }
    }
}

@Composable
fun CommunityHealthApp() {
    val navController = rememberNavController()
    
    // Use a default value to determine the initial screen
    // We'll simply start with the Login screen as default
    // In a production app, you'd want to check shared preferences or local storage here
    
    AppNavigation(
        navController = navController,
        startDestination = Screen.Login.route
    )
}