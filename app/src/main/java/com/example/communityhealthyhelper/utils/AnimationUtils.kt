package com.example.communityhealthyhelper.utils

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry

/**
 * Utility object that provides various animation presets for use throughout the app
 */
object AnimationUtils {
    
    // Duration constants
    const val SHORT_ANIMATION_DURATION = 300
    const val MEDIUM_ANIMATION_DURATION = 500
    const val LONG_ANIMATION_DURATION = 800
    
    /**
     * Navigation transition animations for the app
     * These define how screens animate when navigating between them
     */
    val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(
                durationMillis = MEDIUM_ANIMATION_DURATION,
                easing = EaseInOut
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = MEDIUM_ANIMATION_DURATION,
                easing = EaseIn
            )
        )
    }
    
    val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth / 4 },
            animationSpec = tween(
                durationMillis = MEDIUM_ANIMATION_DURATION,
                easing = EaseInOut
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = MEDIUM_ANIMATION_DURATION,
                easing = EaseOut
            )
        )
    }
    
    val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth / 4 },
            animationSpec = tween(
                durationMillis = MEDIUM_ANIMATION_DURATION,
                easing = EaseInOut
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = MEDIUM_ANIMATION_DURATION,
                easing = EaseIn
            )
        )
    }
    
    val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(
                durationMillis = MEDIUM_ANIMATION_DURATION,
                easing = EaseInOut
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = MEDIUM_ANIMATION_DURATION,
                easing = EaseOut
            )
        )
    }
    
    /**
     * Button click animation that provides tactile feedback
     */
    val buttonClickAnimation = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    /**
     * Pulse animation for the SOS button
     */
    val pulseAnimation = keyframes {
        durationMillis = 2000
        1.0f at 0
        1.2f at 500
        1.0f at 1000
        1.2f at 1500
        1.0f at 2000
    }
    
    /**
     * Card appearance animation
     */
    val cardEnterAnimation = ContentTransform(
        initialContentExit = fadeOut(animationSpec = tween(150, easing = LinearEasing)),
        targetContentEnter = expandIn(animationSpec = tween(300, easing = EaseInOut)) + 
                fadeIn(animationSpec = tween(300, easing = EaseInOut)),
        sizeTransform = SizeTransform(clip = false)
    )
    
    /**
     * List item animations
     */
    val listItemEnterAnimation = fadeIn(animationSpec = tween(300)) + 
            expandVertically(animationSpec = tween(300))
    
    val listItemExitAnimation = fadeOut(animationSpec = tween(300)) + 
            shrinkVertically(animationSpec = tween(300))
            
    /**
     * Dialog animations
     */
    val dialogEnterAnimation = scaleIn(
        initialScale = 0.8f,
        animationSpec = tween(300, easing = EaseInOut)
    ) + fadeIn(animationSpec = tween(300))
    
    val dialogExitAnimation = scaleOut(
        targetScale = 0.8f,
        animationSpec = tween(300, easing = EaseInOut)
    ) + fadeOut(animationSpec = tween(300))
    
    /**
     * BMI result animation
     */
    val bmiResultEnterAnimation = expandIn(
        expandFrom = androidx.compose.ui.Alignment.Center,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    ) + fadeIn(
        animationSpec = tween(500)
    )
}
