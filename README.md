# Community Health Helper App

A comprehensive mobile application aimed at improving health and safety within communities by providing easy access to health tools and emergency assistance.

## Features

### 1. User Authentication
- Secure registration and login system using Firebase Authentication
- User account management

### 2. BMI Calculator
- Calculate Body Mass Index by entering height and weight
- Get BMI classification and health recommendations
- Visual representation of BMI categories

### 3. Health Services Locator
- Find nearby health facilities using Google Maps integration
- Filter locations by type: hospitals, pharmacies, and fitness centers
- Get directions to selected health facilities

### 4. Emergency SOS System
- Send emergency alerts to saved contacts
- Include current location in emergency messages
- Customizable emergency message
- Quick access to emergency services

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox (2020.3.1) or newer
- Kotlin 1.5.0 or newer
- Google Maps API Key
- Firebase Account

### Firebase Setup
1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Register your app with package name `com.example.communityhealthyhelper`
3. Download the `google-services.json` file and replace the placeholder file in the app directory
4. Enable Firebase Authentication in the Firebase Console
   - Go to Authentication > Sign-in method
   - Enable Email/Password authentication

### Google Maps Setup
1. Create a Google Cloud Platform project
2. Enable Maps SDK for Android
3. Generate an API key
4. Replace the API key in your project:
   - Create a file at `app/src/main/res/values/maps_api.xml`
   - Add your key:
   ```xml
   <?xml version="1.0" encoding="utf-8"?>
   <resources>
       <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">YOUR_API_KEY</string>
   </resources>
   ```

## Building and Running the Project
1. Open the project in Android Studio
2. Sync Gradle files
3. Build the project
4. Run on an emulator or physical device

## Note for Developers
The current implementation uses mock data for health services locations. In a production environment, you should integrate with the Google Places API to get real-world data of health facilities.

## License
This project is intended for educational purposes only.
