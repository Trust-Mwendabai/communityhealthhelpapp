package com.example.communityhealthyhelper.services

import com.example.communityhealthyhelper.models.EmergencyContact
import com.example.communityhealthyhelper.models.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Service class to handle interactions with Firebase services including Firestore
 */
class FirebaseService {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    private val usersCollection = firestore.collection("users")
    
    /**
     * Get the current authenticated user
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    /**
     * Create or update a user profile in Firestore
     */
    suspend fun saveUserProfile(userProfile: UserProfile): Result<UserProfile> {
        return try {
            usersCollection.document(userProfile.userId)
                .set(userProfile)
                .await()
            
            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get a user profile from Firestore
     */
    suspend fun getUserProfile(userId: String): Result<UserProfile> {
        return try {
            val documentSnapshot = usersCollection.document(userId).get().await()
            
            if (documentSnapshot.exists()) {
                val userProfile = documentSnapshot.toObject(UserProfile::class.java)
                if (userProfile != null) {
                    Result.success(userProfile)
                } else {
                    Result.failure(Exception("Failed to parse user profile"))
                }
            } else {
                Result.failure(Exception("User profile not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Create a user profile after registration
     */
    suspend fun createInitialUserProfile(user: FirebaseUser): Result<UserProfile> {
        val initialProfile = UserProfile(
            userId = user.uid,
            email = user.email ?: "",
            displayName = user.displayName ?: ""
        )
        
        return saveUserProfile(initialProfile)
    }
    
    /**
     * Update the user's BMI data
     */
    suspend fun updateBmiData(userId: String, height: Float, weight: Float, bmi: Float): Result<Unit> {
        return try {
            usersCollection.document(userId)
                .update(
                    mapOf(
                        "height" to height,
                        "weight" to weight,
                        "lastBMI" to bmi
                    )
                )
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Save emergency contacts for a user
     */
    suspend fun saveEmergencyContacts(
        userId: String,
        emergencyContacts: List<EmergencyContact>
    ): Result<Unit> {
        return try {
            usersCollection.document(userId)
                .update("emergencyContacts", emergencyContacts)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get emergency contacts for a user
     */
    suspend fun getEmergencyContacts(userId: String): Result<List<EmergencyContact>> {
        return try {
            val documentSnapshot = usersCollection.document(userId).get().await()
            
            if (documentSnapshot.exists()) {
                val userProfile = documentSnapshot.toObject(UserProfile::class.java)
                if (userProfile != null) {
                    Result.success(userProfile.emergencyContacts)
                } else {
                    Result.failure(Exception("Failed to parse user profile"))
                }
            } else {
                Result.failure(Exception("User profile not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
