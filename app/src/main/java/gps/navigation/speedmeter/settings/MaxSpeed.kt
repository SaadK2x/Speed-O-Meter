package gps.navigation.speedmeter.settings

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds.admobInterstitialNav
import gps.navigation.speedmeter.ads.SpeedMeterShowAds.mediationBackPressedSimpleHoneyBeeMapNavigation
import gps.navigation.speedmeter.databinding.ActivityMaxSpeedBinding
import gps.navigation.speedmeter.sharedprefrences.SharedPreferenceHelperClass

class MaxSpeed : AppCompatActivity() {
    val binding: ActivityMaxSpeedBinding by lazy {
        ActivityMaxSpeedBinding.inflate(layoutInflater)
    }
    var modeSpeed = "Driving"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnDone.setOnClickListener {

            val sp = SharedPreferenceHelperClass(this)
            sp.putString("modeSpeed", modeSpeed)
            sp.putInt("speedLimit", binding.speedLimit.text.toString().toInt())
            onBackPressed()
        }


        binding.btnDriving.setOnClickListener {
            val sp = SharedPreferenceHelperClass(this)
            val color = sp.getString("AppColor", "#0DCF31")
            modeSpeed = "Driving"

            binding.speedLimit.setText(binding.limitDriving.text.toString())
            binding.btnDriving.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
            binding.btnCycling.backgroundTintList =
                ColorStateList.valueOf(getColor(R.color.fadeBlue))
            binding.btnWalking.backgroundTintList =
                ColorStateList.valueOf(getColor(R.color.fadeBlue))
            sp.putString("modeSpeed", modeSpeed)
            sp.putInt("speedLimit", binding.speedLimit.text.toString().toInt())

        }
        binding.btnWalking.setOnClickListener {
            val sp = SharedPreferenceHelperClass(this)
            val color = sp.getString("AppColor", "#0DCF31")
            modeSpeed = "Walking"
            binding.speedLimit.setText(binding.limitWalking.text.toString())
            binding.btnWalking.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
            binding.btnDriving.backgroundTintList =
                ColorStateList.valueOf(getColor(R.color.fadeBlue))
            binding.btnCycling.backgroundTintList =
                ColorStateList.valueOf(getColor(R.color.fadeBlue))
            sp.putString("modeSpeed", modeSpeed)
            sp.putInt("speedLimit", binding.speedLimit.text.toString().toInt())
        }
        binding.btnCycling.setOnClickListener {
            val sp = SharedPreferenceHelperClass(this)
            val color = sp.getString("AppColor", "#0DCF31")
            modeSpeed = "Cycling"
            binding.speedLimit.setText(binding.limitCycling.text.toString())
            binding.btnCycling.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
            binding.btnDriving.backgroundTintList =
                ColorStateList.valueOf(getColor(R.color.fadeBlue))
            binding.btnWalking.backgroundTintList =
                ColorStateList.valueOf(getColor(R.color.fadeBlue))
            sp.putString("modeSpeed", modeSpeed)
            sp.putInt("speedLimit", binding.speedLimit.text.toString().toInt())

        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        mediationBackPressedSimpleHoneyBeeMapNavigation(this, admobInterstitialNav)
    }

    override fun onResume() {
        super.onResume()
        settingTextView()


    }

    fun settingTextView() {
        val sp = SharedPreferenceHelperClass(this)
        binding.titleTxt.text = getString(R.string.speed_limit)
        binding.doneTV.text = getString(R.string.done)
        binding.titleCustom.text = getString(R.string.warning_when_over)
        binding.descCustom.text = getString(R.string.tap_on_digit_to_edit)
        binding.titleDriving.text = getString(R.string.driving)
        binding.titleCycling.text = getString(R.string.cycling)
        binding.titleWalking.text = getString(R.string.walking)
        val color = sp.getString("AppColor", "#0DCF31")
        val mode = sp.getString("modeSpeed", modeSpeed)
        var limit = sp.getInt("speedLimit")
        if (limit == 0) {
            limit = 100
        }

        binding.speedLimit.setText(limit.toString())
        binding.btnDone.setCardBackgroundColor(Color.parseColor(color))


        when (mode) {
            "Driving" -> {
                binding.btnDriving.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(color))
            }

            "Walking" -> {
                binding.btnWalking.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(color))
            }

            "Cycling" -> {
                binding.btnCycling.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(color))
            }

        }
    }
}