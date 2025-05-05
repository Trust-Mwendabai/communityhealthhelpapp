package com.example.communityhealthyhelper.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.communityhealthyhelper.utils.AnimationUtils
import com.example.communityhealthyhelper.screens.auth.LoginScreen
import com.example.communityhealthyhelper.screens.auth.RegisterScreen
import com.example.communityhealthyhelper.screens.bmi.BMICalculatorScreen
import com.example.communityhealthyhelper.screens.home.HomeScreen
import com.example.communityhealthyhelper.screens.maps.MapsScreen
import com.example.communityhealthyhelper.screens.sos.SOSScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object BMI : Screen("bmi")
    object Maps : Screen("maps")
    object SOS : Screen("sos")
}

@Composable
fun AppNavigation(navController: NavHostController, startDestination: String = Screen.Login.route) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            route = Screen.Login.route,
            enterTransition = AnimationUtils.enterTransition,
            exitTransition = AnimationUtils.exitTransition,
            popEnterTransition = AnimationUtils.popEnterTransition,
            popExitTransition = AnimationUtils.popExitTransition
        ) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToHome = { navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }}
            )
        }
        
        composable(
            route = Screen.Register.route,
            enterTransition = AnimationUtils.enterTransition,
            exitTransition = AnimationUtils.exitTransition,
            popEnterTransition = AnimationUtils.popEnterTransition,
            popExitTransition = AnimationUtils.popExitTransition
        ) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegistrationSuccess = { navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }}
            )
        }
        
        composable(
            route = Screen.Home.route,
            enterTransition = AnimationUtils.enterTransition,
            exitTransition = AnimationUtils.exitTransition,
            popEnterTransition = AnimationUtils.popEnterTransition,
            popExitTransition = AnimationUtils.popExitTransition
        ) {
            HomeScreen(
                onNavigateToBMI = { navController.navigate(Screen.BMI.route) },
                onNavigateToMaps = { navController.navigate(Screen.Maps.route) },
                onNavigateToSOS = { navController.navigate(Screen.SOS.route) },
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(
            route = Screen.BMI.route,
            enterTransition = AnimationUtils.enterTransition,
            exitTransition = AnimationUtils.exitTransition,
            popEnterTransition = AnimationUtils.popEnterTransition,
            popExitTransition = AnimationUtils.popExitTransition
        ) {
            BMICalculatorScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.Maps.route,
            enterTransition = AnimationUtils.enterTransition,
            exitTransition = AnimationUtils.exitTransition,
            popEnterTransition = AnimationUtils.popEnterTransition,
            popExitTransition = AnimationUtils.popExitTransition
        ) {
            MapsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.SOS.route,
            enterTransition = AnimationUtils.enterTransition,
            exitTransition = AnimationUtils.exitTransition,
            popEnterTransition = AnimationUtils.popEnterTransition,
            popExitTransition = AnimationUtils.popExitTransition
        ) {
            SOSScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
