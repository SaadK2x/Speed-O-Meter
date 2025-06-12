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

            buildConfigField(
                "String",
                "admob_ad_id",
                "\"ca-app-pub-3940256099942544~3347511713\""
            )
            buildConfigField(
                "String",
                "admob_banner_id",
                "\"ca-app-pub-4016151566711692/7097421699\""
            )
            buildConfigField(
                "String",
                "medium_admob_banner_id",
                "\"ca-app-pub-4016151566711692/1060147526\""
            )
            buildConfigField(
                "String",
                "admob_interstitial_id",
                "\"ca-app-pub-4016151566711692/3910155618\""
            )
            buildConfigField(
                "String",
                "admob_interstitial_id_main",
                "\"ca-app-pub-4016151566711692/9700368454\""
            )
            buildConfigField(
                "String",
                "admob_medium_banner_intro_id",
                "\"ca-app-pub-4016151566711692/3123102455\""
            )
            buildConfigField(
                "String",
                "app_open_ad_id_admob",
                "\"ca-app-pub-4016151566711692/4668367770\""
            )
            buildConfigField(
                "String",
                "app_open_ad_id_admob_splash",
                "\"ca-app-pub-4016151566711692/3125054537\""
            )
            buildConfigField(
                "String",
                "native_small",
                "\"ca-app-pub-4016151566711692/4635744035\""
            )
            buildConfigField(
                "String",
                "rewarded",
                "\"ca-app-pub-4016151566711692/5726753489\""
            )
            buildConfigField(
                "String",
                "video_interstitial",
                "\"ca-app-pub-4016151566711692/3419799126\""
            )
            buildConfigField(
                "String",
                "intro_admob_native_id",
                "\"ca-app-pub-4016151566711692/7670570091\""
            )
            buildConfigField(
                "String",
                "facebook_banner",
                "\"1140362210925883_1161406428821461\""
            )
            buildConfigField(
                "String",
                "facebook_native",
                "\"1140362210925883_1161406768821427\""
            )
            buildConfigField(
                "String",
                "facebook_interstitial",
                "\"1140362210925883_1161406615488109\""
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
                "medium_admob_banner_id",
                "\"ca-app-pub-3940256099942544/6300978111\""
            )
            buildConfigField(
                "String",
                "admob_interstitial_id",
                "\"ca-app-pub-3940256099942544/1033173712\""
            )
            buildConfigField(
                "String",
                "admob_interstitial_id_main",
                "\"ca-app-pub-3940256099942544/1033173712\""
            )
            buildConfigField(
                "String",
                "video_interstitial",
                "\"ca-app-pub-3940256099942544/1033173712\""
            )
            buildConfigField(
                "String",
                "admob_medium_banner_intro_id",
                "\"ca-app-pub-3940256099942544/6300978111\""
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
                "native_small",
                "\"ca-app-pub-3940256099942544/2247696110\""
            )
            buildConfigField(
                "String",
                "rewarded",
                "\"ca-app-pub-3940256099942544/5224354917\""
            )
            buildConfigField(
                "String",
                "intro_admob_native_id",
                "\"ca-app-pub-3940256099942544/2247696110\""
            )
            buildConfigField(
                "String",
                "facebook_banner",
                "\"1140362210925883_1161406428821461\""
            )
            buildConfigField(
                "String",
                "facebook_native",
                "\"1140362210925883_1161406768821427\""
            )
            buildConfigField(
                "String",
                "facebook_interstitial",
                "\"1140362210925883_1161406615488109\""
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

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.10.1")
    implementation("androidx.lifecycle:lifecycle-process:2.9.1")
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
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
    //googlebilling
    implementation("com.android.billingclient:billing:7.1.1")
    //ads
    implementation("com.google.android.gms:play-services-ads:24.2.0")
    //Update App
    implementation("com.google.android.play:app-update-ktx:2.1.0")
    //firebase
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("com.google.firebase:firebase-messaging:24.1.0")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("io.github.farimarwat:grizzly:2.3")
}