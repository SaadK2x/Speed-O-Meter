plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "gps.navigation.speedmeter"
    compileSdk = 35

    defaultConfig {
        applicationId = "gps.speedometer.gpsspeedometer.odometer.speedtracker"
        minSdk = 24
        targetSdk = 35
        versionCode = 9
        versionName = "1.0.9"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    bundle {
        language {
            enableSplit = false
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField(
                "String",
                "admob_ad_id",
                "\"ca-app-pub-4016151566711692~3601854534\""
            )
            buildConfigField(
                "String",
                "admob_banner_id",
                "\"ca-app-pub-4016151566711692/9384394759\""
            )
            buildConfigField(
                "String",
                "medium_banner",
                "\"ca-app-pub-4016151566711692/6905752253\""
            )
            buildConfigField(
                "String",
                "admob_interstitial_id",
                "\"ca-app-pub-4016151566711692/9966176235\""
            )
            buildConfigField(
                "String",
                "nav_interstitial",
                "\"ca-app-pub-4016151566711692/5825353309\""
            )
            buildConfigField(
                "String",
                "app_open_ad_id_admob",
                "\"ca-app-pub-4016151566711692/3066398418\""
            )
            buildConfigField(
                "String",
                "app_open_ad_id_admob_splash",
                "\"ca-app-pub-3940256099942544/9257395921\""
            )
            buildConfigField(
                "String",
                "admob_interstitial_splash",
                "\"ca-app-pub-4016151566711692/8071313083\""
            )
            buildConfigField(
                "String",
                "tokenApi",
                "\"Bearer 3eed4dddd5e027fb9adce197cd7e87ccc04c9bf3cfae04b7fa6e01addf93c750\""
            )
            buildConfigField(
                "String",
                "imagesToken",
                "\"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJmcm9udGVuZCJ9.xCEH7D24icc1R2sQlJ3870aCNMZgqvQ9hucvBGyzcpM\""
            )
        }

        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "admob_ad_id",
                "\"ca-app-pub-3940256099942544~3347511713\""
            )

            buildConfigField(
                "String",
                "admob_banner_id",
                "\"ca-app-pub-3940256099942544/6300978111\""
            )
            buildConfigField(
                "String",
                "medium_banner",
                "\"ca-app-pub-3940256099942544/6300978111\""
            )
            buildConfigField(
                "String",
                "admob_interstitial_id",
                "\"ca-app-pub-3940256099942544/1033173712\""
            )
            buildConfigField(
                "String",
                "nav_interstitial",
                "\"ca-app-pub-3940256099942544/1033173712\""
            )

            buildConfigField(
                "String",
                "app_open_ad_id_admob",
                "\"ca-app-pub-3940256099942544/9257395921\""
            )
            buildConfigField(
                "String",
                "app_open_ad_id_admob_splash",
                "\"ca-app-pub-3940256099942544/9257395921\""
            )
            buildConfigField(
                "String",
                "admob_interstitial_splash",
                "\"ca-app-pub-3940256099942544/1033173712\""
            )

            buildConfigField(
                "String",
                "tokenApi",
                "\"Bearer 3eed4dddd5e027fb9adce197cd7e87ccc04c9bf3cfae04b7fa6e01addf93c750\""
            )
            buildConfigField(
                "String",
                "imagesToken",
                "\"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJmcm9udGVuZCJ9.xCEH7D24icc1R2sQlJ3870aCNMZgqvQ9hucvBGyzcpM\""
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
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.activity:activity:1.10.1")
    implementation("androidx.lifecycle:lifecycle-process:2.9.1")
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.github.anastr:speedviewlib:1.6.1")
    implementation("androidx.fragment:fragment-ktx:1.8.8")
    implementation("androidx.activity:activity-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")
    implementation("androidx.multidex:multidex:2.0.1")

    implementation("androidx.room:room-runtime:2.7.2")
    kapt("androidx.room:room-compiler:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")
    implementation("com.github.hydrated:SwipeRevealLayout:1.4.0")
    //lottie
    implementation("com.airbnb.android:lottie:6.6.7")
    //Mapbox
    implementation("com.mapbox.maps:android:11.9.0")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-turf:7.3.1")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-geojson:7.3.1")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-services:7.3.1")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-core:7.3.1")

    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")
    //googleBilling
    implementation("com.android.billingclient:billing:7.1.1")
    //ads
    implementation("com.google.android.gms:play-services-ads:24.4.0")
    //Update App
    implementation("com.google.android.play:app-update-ktx:2.1.0")
    //firebase
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("com.google.firebase:firebase-messaging:24.1.2")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation ("com.google.firebase:firebase-config-ktx")
    implementation("io.github.farimarwat:grizzly:2.4.2")
    //Meta Bidding
    implementation("com.google.ads.mediation:facebook:6.20.0.0")

    //dot
    implementation("com.tbuonomo:dotsindicator:5.0")
}