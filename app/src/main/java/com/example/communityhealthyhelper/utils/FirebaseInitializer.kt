package com.example.communityhealthyhelper.utils

import android.content.Context
import android.util.Log
import com.example.communityhealthyhelper.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

/**
 * Utility class to handle Firebase initialization with debug options for testing
 */
object FirebaseInitializer {
    
    private const val TAG = "FirebaseInitializer"
    
    /**
     * Initialize Firebase with appropriate logging settings for testing
     */
    fun initialize(context: Context) {
        try {
            // Check if Firebase is already initialized
            if (FirebaseApp.getApps(context).isEmpty()) {
                // Get application ID from google-services.json using reflection
                val applicationId = context.packageName
                Log.d(TAG, "Initializing Firebase for package: $applicationId")
                
                // Try to initialize with explicit options if needed
                try {
                    // First try default initialization
                    FirebaseApp.initializeApp(context)
                } catch (e: Exception) {
                    Log.e(TAG, "Default initialization failed: ${e.message}, trying manual initialization")
                    
                    // If that fails, try to manually initialize Firebase (fallback)
                    val options = FirebaseOptions.Builder()
                        .setProjectId("communityhealthhelper-18804")
                        .setApplicationId("1:281675431898:android:b680108e33ab05f6508ae3")
                        .setApiKey("AIzaSyAzmXVhDmkWrxNxX2gka33llP2BxtggD_c")
                        .build()
                    
                    FirebaseApp.initializeApp(context, options)
                }
            } else {
                Log.d(TAG, "Firebase already initialized")
            }
            
            // Enable Firestore logging for debug builds
            if (BuildConfig.FIREBASE_LOGGING_ENABLED) {
                enableFirestoreLogging()
            }
            
            Log.d(TAG, "Firebase successfully initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase: ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * Enable verbose Firestore logging for testing
     */
    private fun enableFirestoreLogging() {
        FirebaseFirestore.setLoggingEnabled(true)
        
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        
        firestore.firestoreSettings = settings
        
        Log.d(TAG, "Firebase logging enabled for testing")
    }
}
