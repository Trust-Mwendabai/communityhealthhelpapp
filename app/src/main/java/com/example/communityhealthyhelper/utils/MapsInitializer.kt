package com.example.communityhealthyhelper.utils

import android.content.Context
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

/**
 * Utility class to handle Google Maps initialization and availability checking
 */
object MapsInitializer {
    private const val TAG = "MapsInitializer"
    
    /**
     * Check if Google Play Services is available and up to date
     * @return true if Google Play Services is available and up to date
     */
    fun isGooglePlayServicesAvailable(context: Context): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
        
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Google Play Services not available (status=$resultCode)")
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                Log.d(TAG, "Google Play Services error is resolvable by the user")
            }
            return false
        }
        
        Log.d(TAG, "Google Play Services is available and up to date")
        return true
    }
}
