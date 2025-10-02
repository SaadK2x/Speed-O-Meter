package gps.navigation.speedmeter.activities

import android.animation.ValueAnimator
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import gps.navigation.speedmeter.BuildConfig
import gps.navigation.speedmeter.ads.SpeedMeterBillingHelper
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds.adShowAfter
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds.next_ads_time
import gps.navigation.speedmeter.ads.SpeedMeterPurchaseSubs
import gps.navigation.speedmeter.ads.SpeedMeterShowAds
import gps.navigation.speedmeter.databinding.ActivitySplashBinding
import gps.navigation.speedmeter.sharedprefrences.SharedPreferenceHelperClass
import gps.navigation.speedmeter.utils.Constants
import gps.navigation.speedmeter.utils.Constants.IsAppOnTimer
import gps.navigation.speedmeter.utils.Constants.backpressadcontrol
import gps.navigation.speedmeter.utils.Constants.willIntersShow
import kotlin.random.Random

class SplashActivity : AppCompatActivity() {
    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }
    var timer: CountDownTimer? = null
    private var purchaseHelper1: SpeedMeterBillingHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        SpeedMeterPurchaseSubs(this)
        purchaseHelper1 = SpeedMeterBillingHelper(this)

        val userKey = settingKeys(generateRandomNumber())
        Log.d("SplashKey", "onCreate: $userKey")
        Constants.keyMapbox = userKey
        remoteConfigValues()
        loadSplash()


    }

    fun generateRandomNumber(): Int {
        return Random.nextInt(1, 6) // Generates a random number from 1 to 5 (inclusive)
    }

    fun settingKeys(pos: Int): String {
        return when (pos) {
            1 -> {
                BuildConfig.mapbox_key_1
            }

            2 -> {
                BuildConfig.mapbox_key_2
            }

            3 -> {
                BuildConfig.mapbox_key_3
            }

            4 -> {
                BuildConfig.mapbox_key_4
            }

            5 -> {
                BuildConfig.mapbox_key_5
            }



            else -> {
                BuildConfig.mapbox_key_3
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val sp = SharedPreferenceHelperClass(this)
        Constants.setLocale(this, sp.getString("language", "en"))
    }

    private fun doWork(seconds: Long) {
        timer = object : CountDownTimer(seconds, 80) {
            override fun onTick(millisUntilFinished: Long) {
                val sec = (millisUntilFinished / 80).toString().toInt()
                val finalsec = -(sec - 100)
                Log.i(ContentValues.TAG, "secs left$finalsec")
                binding.progressBar.progress = finalsec
            }

            override fun onFinish() {
                binding.progressBar.visibility = View.INVISIBLE
                binding.textView1.visibility = View.INVISIBLE
                moveForward()
            }
        }
        timer?.start()
    }

    fun loadSplash() {
        try {
            MobileAds.initialize(this)
        } catch (e: Exception) {
        }
        if (purchaseHelper1!!.shouldShowAds()) {
            SpeedMeterLoadAds.loadSplashAd(this, object : SpeedMeterLoadAds.OnVideoLoad {
                override fun onLoaded() {
                    timer?.cancel()
                    timer = null
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.textView1.visibility = View.INVISIBLE
                    SpeedMeterShowAds.showingSplashAd(this@SplashActivity) {
                        moveForward()
                    }

                }

                override fun onFailed() {
                    timer?.cancel()
                    timer = null
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.textView1.visibility = View.INVISIBLE
                    moveForward()
                }

            })
            doWork(8000)
        } else {
            moveForward()
        }

    }

    private fun moveForward() {
        val sp = SharedPreferenceHelperClass(this)
        val data = sp.getBoolean("firstTime", true)
        if (data) {
            sp.putBoolean("firstTime", false)
            val intent = Intent(this@SplashActivity, IntroActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this@SplashActivity, PremiumActivity::class.java)
            startActivity(intent)
        }

    }

    private fun remoteConfigValues() {

        val remoteConfig = FirebaseRemoteConfig.getInstance()

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(30)
            .build()

        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this@SplashActivity) { task ->
                if (task.isSuccessful) {
                    adShowAfter = remoteConfig.getString("intertitialCounter").toInt()
                    Log.d("TAG_LL", "remoteConfigValues: ${remoteConfig.getString("premium")}")
                    IsAppOnTimer = remoteConfig.getBoolean("IsAppOnTimer")
                    willIntersShow = remoteConfig.getBoolean("willIntersShow")
                    backpressadcontrol = remoteConfig.getBoolean("backpressadcontrol")
                    next_ads_time = remoteConfig.getString("next_ads_time").toLong()

                } else {
                    Log.d("remoteConfig", "Failed to fetch values")
                }
            }
    }
}