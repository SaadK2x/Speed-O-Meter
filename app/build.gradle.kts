plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "gps.navigation.speedmeter"
    compileSdk = 35

    defaultConfig {
        applicationId = "gps.speedometer.digihud.odometer.speedometer"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.10.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("com.github.anastr:speedviewlib:1.6.1")
    implementation("androidx.fragment:fragment-ktx:1.3.0")
    implementation("androidx.activity:activity-ktx:1.3.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0")
    implementation("androidx.multidex:multidex:2.0.1")

    implementation("androidx.room:room-runtime:2.2.4")
    kapt("androidx.room:room-compiler:2.2.4")
    implementation("androidx.room:room-ktx:2.2.5")
    implementation("com.github.hydrated:SwipeRevealLayout:1.4.0")
    //lottie
    implementation("com.airbnb.android:lottie:6.0.0")
    //Mapbox
    implementation("com.mapbox.maps:android:11.9.0")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-turf:7.3.1")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-geojson:7.3.1")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-services:7.3.1")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-core:7.3.1")

    implementation("com.intuit.sdp:sdp-android:1.0.6")
    implementation("com.intuit.ssp:ssp-android:1.0.6")
}