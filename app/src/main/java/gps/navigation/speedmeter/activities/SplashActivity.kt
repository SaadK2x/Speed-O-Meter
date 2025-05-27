package gps.navigation.speedmeter.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import gps.navigation.speedmeter.databinding.ActivitySplashBinding
import gps.navigation.speedmeter.sharedprefrences.SharedPreferenceHelperClass
import gps.navigation.speedmeter.utils.Constants

class SplashActivity : AppCompatActivity() {
    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }
    var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        doWork(6000)
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

                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
        }
        timer?.start()
    }
}