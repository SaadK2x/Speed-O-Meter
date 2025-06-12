package gps.navigation.speedmeter.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.databinding.ActivityConsentBinding

class ConsentActivity : AppCompatActivity() {
    val binding: ActivityConsentBinding by lazy {
        ActivityConsentBinding.inflate(layoutInflater)
    }
    companion object {
        lateinit var consentInformation: ConsentInformation
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //.setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)

        /*   val debugSettings = ConsentDebugSettings.Builder(this)
                 .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                 .addTestDeviceHashedId("BC91F25D8077F8763EB65CFA36D610B2")
                 .build()*/

        val params = ConsentRequestParameters
            .Builder()
            /* .setConsentDebugSettings(debugSettings)*/
            .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        //consentInformation.reset()
        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                //success
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(this) { loadAndShowError ->
                    if (loadAndShowError != null) {
                        Log.d("consentInformation", "success: ${loadAndShowError.message}")
                        moveToNext()
                    }

                    if (consentInformation.canRequestAds()) {
                        Log.d("consentInformation", "canRequestAds")
                        moveToNext()
                    } else {
                        moveToNext()
                    }
                }
            },
            {
                //failure
                Log.d("consentInformation", "failure: ${it.message}")
                moveToNext()
            }
        )

    }

    private fun moveToNext() {
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
    }
}