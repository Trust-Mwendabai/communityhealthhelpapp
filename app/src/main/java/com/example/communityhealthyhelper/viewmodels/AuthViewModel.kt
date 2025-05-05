package com.example.communityhealthyhelper.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityhealthyhelper.models.UserProfile
import com.example.communityhealthyhelper.services.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firebaseService = FirebaseService()
    
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    
    init {
        // Load user profile if user is already logged in
        _currentUser.value?.let { user ->
            fetchUserProfile(user.uid)
        }
    }
    
    private fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            try {
                val result = firebaseService.getUserProfile(userId)
                result.onSuccess { profile ->
                    _userProfile.value = profile
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                auth.signInWithEmailAndPassword(email, password).await()
                val user = auth.currentUser
                _currentUser.value = user
                
                // Fetch user profile after login
                user?.let { fetchUserProfile(it.uid) }
                
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }
    
    fun register(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                
                // Log registration attempt for debugging
                Log.d("AuthViewModel", "Attempting to register with email: $email")
                
                // Use coroutines for Firebase operations instead of callbacks
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user
                _currentUser.value = user
                
                // Create initial user profile in Firestore
                if (user != null) {
                    try {
                        // Add timeout to profile creation to prevent UI hanging
                        withTimeoutOrNull(10000L) { // 10 seconds timeout
                            val result = firebaseService.createInitialUserProfile(user)
                            result.onSuccess { profile ->
                                _userProfile.value = profile
                                Log.d("AuthViewModel", "User profile created successfully")
                            }
                            result.onFailure { error ->
                                Log.e("AuthViewModel", "Failed to create user profile: ${error.message}")
                                // Continue with success even if profile creation fails
                            }
                        }
                        
                        // Regardless of profile creation outcome, mark registration as successful
                        // since the Firebase Auth account was created
                        withContext(Dispatchers.Main) {
                            _authState.value = AuthState.Success
                            Log.d("AuthViewModel", "Registration marked as successful")
                        }
                    } catch (e: Exception) {
                        Log.e("AuthViewModel", "Exception during profile creation: ${e.message}")
                        // Still mark as successful since auth account was created
                        _authState.value = AuthState.Success
                    }
                } else {
                    Log.e("AuthViewModel", "User is null after successful authentication")
                    _authState.value = AuthState.Error("Registration completed but user data is missing")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Registration failed: ${e.message}")
                val errorMessage = when (e) {
                    is FirebaseAuthWeakPasswordException -> "Password is too weak. It must be at least 6 characters."
                    is FirebaseAuthInvalidCredentialsException -> "Invalid email format."
                    is FirebaseAuthUserCollisionException -> "An account already exists with this email."
                    else -> "Registration failed: ${e.message}"
                }
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }
    
    fun signOut() {
        auth.signOut()
        _currentUser.value = null
        _userProfile.value = null
        _authState.value = AuthState.Idle
    }
    
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
