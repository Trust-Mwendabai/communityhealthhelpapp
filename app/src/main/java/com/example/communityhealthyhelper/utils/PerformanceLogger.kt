package com.example.communityhealthyhelper.utils

import android.os.Build
import android.util.Log
import android.view.Choreographer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.concurrent.TimeUnit

/**
 * Utility class to log and monitor UI performance during testing
 */
object PerformanceLogger {
    private const val TAG = "PerformanceLogger"
    
    // Threshold for considering a frame drop (in ms)
    private const val FRAME_TIME_THRESHOLD_MS = 16L // ~60 FPS
    
    // Frame callback for monitoring frame timing
    private val frameCallback = object : Choreographer.FrameCallback {
        private var lastFrameTimeNanos = 0L
        
        override fun doFrame(frameTimeNanos: Long) {
            if (lastFrameTimeNanos > 0) {
                val durationMs = TimeUnit.NANOSECONDS.toMillis(frameTimeNanos - lastFrameTimeNanos)
                if (durationMs > FRAME_TIME_THRESHOLD_MS) {
                    Log.w(TAG, "Frame drop detected: $durationMs ms (threshold: $FRAME_TIME_THRESHOLD_MS ms)")
                }
            }
            lastFrameTimeNanos = frameTimeNanos
            Choreographer.getInstance().postFrameCallback(this)
        }
    }
    
    /**
     * Start monitoring frame performance
     */
    fun startMonitoring() {
        // Avoid duplicate callbacks
        Choreographer.getInstance().removeFrameCallback(frameCallback)
        Choreographer.getInstance().postFrameCallback(frameCallback)
        Log.d(TAG, "Frame performance monitoring started")
    }
    
    /**
     * Stop monitoring frame performance
     */
    fun stopMonitoring() {
        Choreographer.getInstance().removeFrameCallback(frameCallback)
        Log.d(TAG, "Frame performance monitoring stopped")
    }
    
    /**
     * Log device information for testing
     */
    fun logDeviceInfo() {
        Log.d(TAG, "Device Info: ${Build.MANUFACTURER} ${Build.MODEL}")
        Log.d(TAG, "Android Version: ${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE})")
    }
    
    /**
     * Log UI events for debugging animation issues
     */
    fun logUIEvent(name: String, durationMs: Long? = null) {
        if (durationMs != null) {
            Log.d(TAG, "UI Event: $name - Duration: $durationMs ms")
        } else {
            Log.d(TAG, "UI Event: $name")
        }
    }
}

/**
 * Composable to monitor frame performance in Compose screens
 */
@Composable
fun MonitorFramePerformance(enabled: Boolean = true) {
    if (enabled) {
        val context = LocalContext.current
        
        DisposableEffect(Unit) {
            PerformanceLogger.logDeviceInfo()
            PerformanceLogger.startMonitoring()
            onDispose {
                PerformanceLogger.stopMonitoring()
            }
        }
    }
}
