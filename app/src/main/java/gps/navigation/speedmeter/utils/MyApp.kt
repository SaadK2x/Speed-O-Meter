package gps.navigation.speedmeter.utils

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import gps.navigation.speedmeter.ads.SpeedMeterAdsModel
import gps.navigation.speedmeter.ads.SpeedMeterAppOpenAd
import gps.navigation.speedmeter.ads.SpeedMeterAppOpenSplashAd
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MyApp : Application() {

    companion object {
        var gpsArea: MyApp? = null
        var isFirstTime = true
        var honeyBeeGPSOpenAdManager: SpeedMeterAppOpenAd? = null
        var sStreetViewTruckCTAppOpenSplashAdManager: SpeedMeterAppOpenSplashAd? = null

        // Store categories from Firebase

        @JvmStatic
        fun getStr(id: Int): String {
            return gpsArea!!.getString(id)
        }


    }


    override fun onCreate() {
        super.onCreate()

        gpsArea = this
        val firebaseCrashlytics = FirebaseCrashlytics.getInstance()

        // Initialize and start GrizzlyMonitor with default settings
        /*  GrizzlyMonitorBuilder(this)
              .withTicker(500L) // Set ticker interval (1-500ms)
              .withThreshold(4500L) // Set ANR threshold (1000-4500ms)
              .withTitle("App Error") // Set custom crash dialog title
              .withMessage("An error occurred. Please restart.") // Set custom crash dialog message
              .withFirebaseCrashLytics(firebaseCrashlytics) // Integrate with Firebase Crashlytics
              .build()
              .start()*/



        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
        }
        sStreetViewTruckCTAppOpenSplashAdManager =
            SpeedMeterAppOpenSplashAd(this)
        honeyBeeGPSOpenAdManager = SpeedMeterAppOpenAd(this)
        // insertADSToFirebase()
        CoroutineScope(Dispatchers.IO).launch {
            getDataFromFirebase()
        }


    }


    private fun getDataFromFirebase() {
        Log.i("getDataFromFirebase: ", " getDataFromFirebase ")
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("SpeedOMeter")
        databaseReference.child("ADS_IDS").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val model = dataSnapshot.getValue<SpeedMeterAdsModel>(
                    SpeedMeterAdsModel::class.java
                )
                Log.i("getDataFromFirebase: ", " model ")
                if (model != null) {
                    Log.i("getDataFromFirebase: ", " model != null")
                    SpeedMeterLoadAds.haveGotSnapshot = true
                    SpeedMeterLoadAds.appid_admob_inApp = model.appid_admob_inApp
                    SpeedMeterLoadAds.banner_admob_inApp = model.banner_admob_inApp
                    SpeedMeterLoadAds.interstitial_admob_inApp = model.interstitial_admob_inApp
                    SpeedMeterLoadAds.native_admob_inApp = model.native_admob_inApp
                    SpeedMeterLoadAds.app_open_ad_id_admob = model.app_open_admob_inApp
                    SpeedMeterLoadAds.app_open_splash_ad_id_admob = model.app_open_splash_ad_id_admob

                    Log.i("getDataFromFirebase: ", " banner_admob_inApp = ${model.banner_admob_inApp}")

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.i("getDataFromFirebase", databaseError.message)
            }
        })
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}