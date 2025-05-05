package com.example.communityhealthyhelper

import android.app.Application
import com.example.communityhealthyhelper.utils.FirebaseInitializer

/**
 * Custom Application class for initialization of app-wide components
 */
class CommunityHealthApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase with logging
        FirebaseInitializer.initialize(this)
    }
}
