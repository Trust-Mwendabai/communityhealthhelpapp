package com.example.communityhealthyhelper.screens.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.widget.Toast
import com.example.communityhealthyhelper.components.PasswordTextField
import com.example.communityhealthyhelper.components.StandardButton
import com.example.communityhealthyhelper.components.StandardTextField
import com.example.communityhealthyhelper.viewmodels.AuthState
import com.example.communityhealthyhelper.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegistrationSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    
    // Handle authentication state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Error -> {
                snackbarHostState.showSnackbar((authState as AuthState.Error).message)
                authViewModel.resetAuthState()
            }
            is AuthState.Success -> {
                // Show success toast
                Toast.makeText(
                    context, 
                    "Registration successful! Redirecting...", 
                    Toast.LENGTH_SHORT
                ).show()
                
                // Short delay to show the toast before navigation
                delay(500)
                
                // Navigate to the next screen
                onRegistrationSuccess()
                
                // Reset auth state
                authViewModel.resetAuthState()
            }
            else -> {}
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Community Health Helper",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                StandardTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        emailError = ""
                    },
                    label = "Email",
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    isError = emailError.isNotEmpty(),
                    errorMessage = emailError
                )
                
                PasswordTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        passwordError = ""
                    },
                    label = "Password",
                    imeAction = ImeAction.Next,
                    isError = passwordError.isNotEmpty(),
                    errorMessage = passwordError
                )
                
                PasswordTextField(
                    value = confirmPassword,
                    onValueChange = { 
                        confirmPassword = it
                        confirmPasswordError = ""
                    },
                    label = "Confirm Password",
                    isError = confirmPasswordError.isNotEmpty(),
                    errorMessage = confirmPasswordError
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                StandardButton(
                    onClick = {
                        var isValid = true
                        
                        if (email.isEmpty()) {
                            emailError = "Email cannot be empty"
                            isValid = false
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            emailError = "Please enter a valid email address"
                            isValid = false
                        }
                        
                        if (password.isEmpty()) {
                            passwordError = "Password cannot be empty"
                            isValid = false
                        } else if (password.length < 6) {
                            passwordError = "Password must be at least 6 characters"
                            isValid = false
                        }
                        
                        if (confirmPassword.isEmpty()) {
                            confirmPasswordError = "Please confirm your password"
                            isValid = false
                        } else if (password != confirmPassword) {
                            confirmPasswordError = "Passwords do not match"
                            isValid = false
                        }
                        
                        if (isValid) {
                            authViewModel.register(email, password)
                        }
                    },
                    text = "Register",
                    isLoading = authState is AuthState.Loading
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Already have an account? Sign in",
                        fontSize = 16.sp
                    )
                }
            }
            
            // Show overlay loading indicator when in loading state
            if (authState is AuthState.Loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
