package com.example.communityhealthyhelper.screens.sos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.communityhealthyhelper.utils.AnimationUtils
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.communityhealthyhelper.components.StandardButton
import com.google.android.gms.location.LocationServices

data class EmergencyContact(
    val id: String,
    val name: String,
    val phoneNumber: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    
    var emergencyContacts by remember {
        mutableStateOf(
            listOf(
                EmergencyContact("1", "Emergency Services", "911")
            )
        )
    }
    
    var showAddContactDialog by remember { mutableStateOf(false) }
    var showEditContactDialog by remember { mutableStateOf(false) }
    var contactBeingEdited by remember { mutableStateOf<EmergencyContact?>(null) }
    var emergencyMessage by remember {
        mutableStateOf("This is an emergency. I need help at my current location.")
    }
    var showMessageDialog by remember { mutableStateOf(false) }
    
    // Location permission
    var locationPermissionGranted by remember { mutableStateOf(false) }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        locationPermissionGranted = isGranted
    }
    
    // SMS permission
    var smsPermissionGranted by remember { mutableStateOf(false) }
    val requestSmsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        smsPermissionGranted = isGranted
    }
    
    // Check permissions
    LaunchedEffect(Unit) {
        val hasLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        val hasSmsPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
        
        locationPermissionGranted = hasLocationPermission
        smsPermissionGranted = hasSmsPermission
        
        if (!hasLocationPermission) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        
        if (!hasSmsPermission) {
            requestSmsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency SOS") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    titleContentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddContactDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Contact")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SOS Emergency",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "In case of emergency, tap the SOS button below to alert your emergency contacts.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // SOS Button
            SOSButton(
                onActivate = {
                    if (emergencyContacts.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Please add at least one emergency contact",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (!smsPermissionGranted) {
                        Toast.makeText(
                            context,
                            "SMS permission required to send emergency messages",
                            Toast.LENGTH_SHORT
                        ).show()
                        requestSmsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                    } else {
                        // Get location if permission granted
                        var locationText = ""
                        if (locationPermissionGranted) {
                            try {
                                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                    if (location != null) {
                                        locationText = "My location: https://maps.google.com/?q=${location.latitude},${location.longitude}"
                                        
                                        // Send SMS to all emergency contacts
                                        for (contact in emergencyContacts) {
                                            val fullMessage = "${emergencyMessage}\n${locationText}"
                                            
                                            // For demonstration, we'll use implicit intents instead of actually sending SMS
                                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = Uri.parse("smsto:${contact.phoneNumber}")
                                                putExtra("sms_body", fullMessage)
                                            }
                                            
                                            try {
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    context,
                                                    "Unable to send message to ${contact.name}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    } else {
                                        // Fallback if location is not available
                                        sendEmergencyMessages(context, emergencyContacts, emergencyMessage)
                                    }
                                }
                            } catch (e: Exception) {
                                // Handle error or fallback
                                sendEmergencyMessages(context, emergencyContacts, emergencyMessage)
                            }
                        } else {
                            // Location permission not granted, send message without location
                            sendEmergencyMessages(context, emergencyContacts, emergencyMessage)
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Emergency Message",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = { showMessageDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Message")
                }
            }
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = emergencyMessage,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Emergency Contacts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (emergencyContacts.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "No emergency contacts added yet. Add your first contact by tapping the + button.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                emergencyContacts.forEach { contact ->
                    ContactCard(
                        contact = contact,
                        onEdit = {
                            contactBeingEdited = contact
                            showEditContactDialog = true
                        },
                        onDelete = {
                            emergencyContacts = emergencyContacts.filter { it.id != contact.id }
                        },
                        onCall = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${contact.phoneNumber}")
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
    
    // Add Contact Dialog
    if (showAddContactDialog) {
        ContactDialog(
            onDismiss = { showAddContactDialog = false },
            onSave = { name, phoneNumber ->
                val newContact = EmergencyContact(
                    id = System.currentTimeMillis().toString(),
                    name = name,
                    phoneNumber = phoneNumber
                )
                emergencyContacts = emergencyContacts + newContact
                showAddContactDialog = false
            },
            title = "Add Emergency Contact"
        )
    }
    
    // Edit Contact Dialog
    if (showEditContactDialog && contactBeingEdited != null) {
        ContactDialog(
            onDismiss = {
                showEditContactDialog = false
                contactBeingEdited = null
            },
            onSave = { name, phoneNumber ->
                val updatedContact = contactBeingEdited!!.copy(
                    name = name,
                    phoneNumber = phoneNumber
                )
                emergencyContacts = emergencyContacts.map {
                    if (it.id == updatedContact.id) updatedContact else it
                }
                showEditContactDialog = false
                contactBeingEdited = null
            },
            title = "Edit Emergency Contact",
            initialName = contactBeingEdited!!.name,
            initialPhoneNumber = contactBeingEdited!!.phoneNumber
        )
    }
    
    // Edit Message Dialog
    if (showMessageDialog) {
        MessageDialog(
            message = emergencyMessage,
            onDismiss = { showMessageDialog = false },
            onSave = {
                emergencyMessage = it
                showMessageDialog = false
            }
        )
    }
}

private fun sendEmergencyMessages(
    context: android.content.Context,
    contacts: List<EmergencyContact>,
    message: String
) {
    for (contact in contacts) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:${contact.phoneNumber}")
            putExtra("sms_body", message)
        }
        
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Unable to send message to ${contact.name}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@Composable
fun SOSButton(onActivate: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    
    // Pulsating animation for SOS button
    val infiniteTransition = rememberInfiniteTransition(label = "SOSPulse")
    val pulseSize by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAnimation"
    )
    
    val glowOpacity by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAnimation"
    )
    
    // Size animation for press feedback
    val size by animateFloatAsState(
        targetValue = if (isPressed) 220f else 200f * pulseSize,
        animationSpec = tween(durationMillis = 300),
        label = "SOSButtonSize"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            onClick = {
                showConfirmDialog = true
            },
            modifier = Modifier.size(size.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.error,
            border = BorderStroke(8.dp, MaterialTheme.colorScheme.errorContainer)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SOS",
                    color = MaterialTheme.colorScheme.onError,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Tap to activate emergency alert",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
    
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm SOS Alert") },
            text = { Text("Are you sure you want to send an emergency alert to all your emergency contacts?") },
            confirmButton = {
                Button(
                    onClick = {
                        onActivate()
                        showConfirmDialog = false
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Send Alert")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ContactCard(
    contact: EmergencyContact,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = contact.phoneNumber,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                IconButton(onClick = onCall) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDelete,
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Remove")
                }
            }
        }
    }
}

@Composable
fun ContactDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, phoneNumber: String) -> Unit,
    title: String,
    initialName: String = "",
    initialPhoneNumber: String = ""
) {
    var name by remember { mutableStateOf(initialName) }
    var phoneNumber by remember { mutableStateOf(initialPhoneNumber) }
    
    var nameError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        nameError = ""
                    },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError.isNotEmpty(),
                    supportingText = if (nameError.isNotEmpty()) {
                        { Text(text = nameError) }
                    } else null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { 
                        phoneNumber = it
                        phoneError = ""
                    },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = phoneError.isNotEmpty(),
                    supportingText = if (phoneError.isNotEmpty()) {
                        { Text(text = phoneError) }
                    } else null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var isValid = true
                    
                    if (name.isBlank()) {
                        nameError = "Name cannot be empty"
                        isValid = false
                    }
                    
                    if (phoneNumber.isBlank()) {
                        phoneError = "Phone number cannot be empty"
                        isValid = false
                    } else if (!isValidPhoneNumber(phoneNumber)) {
                        phoneError = "Please enter a valid phone number"
                        isValid = false
                    }
                    
                    if (isValid) {
                        onSave(name, phoneNumber)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MessageDialog(
    message: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var editedMessage by remember { mutableStateOf(message) }
    var messageError by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Emergency Message") },
        text = {
            Column {
                OutlinedTextField(
                    value = editedMessage,
                    onValueChange = { 
                        editedMessage = it
                        messageError = ""
                    },
                    label = { Text("Message") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    isError = messageError.isNotEmpty(),
                    supportingText = if (messageError.isNotEmpty()) {
                        { Text(text = messageError) }
                    } else null
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    
                    Text(
                        text = "Your current location will be included automatically if location permission is granted.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (editedMessage.isBlank()) {
                        messageError = "Message cannot be empty"
                    } else {
                        onSave(editedMessage)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun isValidPhoneNumber(phone: String): Boolean {
    // Basic validation - can be enhanced for specific regions
    return phone.isNotBlank() && phone.all { it.isDigit() || it == '+' || it == '-' || it == '(' || it == ')' || it == ' ' }
}
