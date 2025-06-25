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
import gps.navigation.speedmeter.ads.SpeedMeterBillingHelper
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds
import gps.navigation.speedmeter.ads.SpeedMeterShowAds
import gps.navigation.speedmeter.databinding.ActivitySplashBinding
import gps.navigation.speedmeter.sharedprefrences.SharedPreferenceHelperClass
import gps.navigation.speedmeter.utils.Constants

class SplashActivity : AppCompatActivity() {
    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }
    var timer: CountDownTimer? = null
    private var purchaseHelper1: SpeedMeterBillingHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        purchaseHelper1 = SpeedMeterBillingHelper(this)
        loadSplash()

        val scaleAnimator = ValueAnimator.ofFloat(1.0f, 1.03f).apply {
            duration = 1000 // Duration of the animation
            repeatCount = ValueAnimator.INFINITE // Repeat infinitely
            repeatMode = ValueAnimator.REVERSE // Reverse direction on repeat
            interpolator = AccelerateDecelerateInterpolator() // Smooth transition
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                binding.startBtn.scaleX = animatedValue
                binding.startBtn.scaleY = animatedValue
            }
        }
        scaleAnimator.start()

        binding.startBtn.setOnClickListener {
            SpeedMeterShowAds.showingSplashAd(this@SplashActivity) {
                moveForward()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val sp = SharedPreferenceHelperClass(this)
        Constants.setLocale(this, sp.getString("language", "en"))
    }

    private fun doWork(seconds: Long) {
        timer = object : CountDownTimer(seconds, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val sec = (millisUntilFinished / 100).toString().toInt()
                val finalsec = -(sec - 97)
                Log.i(ContentValues.TAG, "secs left$finalsec")
                binding.progressBar.progress = finalsec
            }

            override fun onFinish() {
                binding.progressBar.visibility = View.INVISIBLE
                binding.textView1.visibility = View.INVISIBLE
                binding.startBtn.visibility = View.INVISIBLE
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
                    binding.startBtn.visibility = View.VISIBLE

                }

                override fun onFailed() {
                    timer?.cancel()
                    timer = null
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.textView1.visibility = View.INVISIBLE
                    binding.startBtn.visibility = View.VISIBLE
                }

            })
            doWork(8000)
        } else {
            moveForward()
        }

    }

    private fun moveForward() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
    }
}