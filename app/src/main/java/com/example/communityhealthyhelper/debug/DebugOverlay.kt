package com.example.communityhealthyhelper.debug

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Simple debug overlay for testing on real devices
 */
@Composable
fun DebugOverlay(
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    if (!enabled) {
        content()
        return
    }
    
    var showDebugInfo by remember { mutableStateOf(false) }
    
    Box {
        content()
        
        Box(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd)
        ) {
            // Debug toggle button
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { showDebugInfo = !showDebugInfo },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Debug",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        
        // Debug info dialog
        if (showDebugInfo) {
            AlertDialog(
                onDismissRequest = { showDebugInfo = false },
                title = { Text("Debug Information") },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("App Version: 1.0 (Debug)")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Android Version: ${android.os.Build.VERSION.RELEASE}")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showDebugInfo = false }
                    ) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
