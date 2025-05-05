plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.communityhealthyhelper"
    compileSdk = 35
    
    // Optimize resource processing
    androidResources {
        noCompress.add(".webp")
        noCompress.add(".png")
    }

    defaultConfig {
        applicationId = "com.example.communityhealthyhelper"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Optimize R class generation
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        debug {
            // Add Firebase debug logging
            buildConfigField("boolean", "FIREBASE_LOGGING_ENABLED", "true")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "FIREBASE_LOGGING_ENABLED", "false")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    
    // Core dependencies - use version catalog
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    // Navigation Component
    implementation(libs.androidx.navigation.compose)
    
    // Compose Animation dependencies - explicit versions
    implementation("androidx.compose.animation:animation:1.5.4")
    implementation("androidx.compose.animation:animation-core:1.5.4")
    
    // Firebase dependencies - updated to latest version
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation(libs.firebase.auth) // This is in your version catalog
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-analytics")
    
    // Google Maps dependencies - specified with hardcoded versions
    // as they're not in your version catalog
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.maps.android:maps-compose:2.11.4")
    implementation("com.google.maps.android:maps-compose-utils:2.11.4")
    implementation("com.google.maps.android:maps-compose-widgets:2.11.4")
    
    // Compose dependencies - with specific versions to match your catalog
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation(libs.androidx.runtime.livedata)
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    
    // Material components with matching versions
    implementation("androidx.compose.material:material:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    
    // Credentials
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}