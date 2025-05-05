package com.example.communityhealthyhelper.models

/**
 * User profile data model for storing user information in Firestore
 */
data class UserProfile(
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val height: Float = 0f,  // Height in centimeters
    val weight: Float = 0f,  // Weight in kilograms
    val lastBMI: Float = 0f,
    val emergencyContacts: List<EmergencyContact> = emptyList(),
    val healthConditions: List<String> = emptyList()
)

/**
 * Emergency contact data model
 */
data class EmergencyContact(
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val relationship: String = ""
)
