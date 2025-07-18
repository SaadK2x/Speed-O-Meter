package gps.navigation.speedmeter.settings

import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds.admobInterstitialNav
import gps.navigation.speedmeter.ads.SpeedMeterShowAds.mediationBackPressedSimpleHoneyBeeMapNavigation
import gps.navigation.speedmeter.databinding.ActivityUnitSelectionBinding
import gps.navigation.speedmeter.sharedprefrences.SharedPreferenceHelperClass

class UnitSelection : AppCompatActivity() {
    val binding: ActivityUnitSelectionBinding by lazy {
        ActivityUnitSelectionBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        squareXGPSBannerAdsSmall()

        binding.btnKmh.setOnClickListener {
            val sp = SharedPreferenceHelperClass(this)
            val color = sp.getString("AppColor", "#0DCF31")

            binding.btnKmh.setCardBackgroundColor(Color.parseColor(color))
            binding.btnMph.setCardBackgroundColor(getColor(R.color.fadeColor))
            binding.btnKnot.setCardBackgroundColor(getColor(R.color.fadeColor))

            sp.putString("Unit", "KMH")
        }
        binding.btnMph.setOnClickListener {
            val sp = SharedPreferenceHelperClass(this)
            val color = sp.getString("AppColor", "#0DCF31")
            binding.btnMph.setCardBackgroundColor(Color.parseColor(color))
            binding.btnKmh.setCardBackgroundColor(getColor(R.color.fadeColor))
            binding.btnKnot.setCardBackgroundColor(getColor(R.color.fadeColor))

            sp.putString("Unit", "MPH")
        }
        binding.btnKnot.setOnClickListener {
            val sp = SharedPreferenceHelperClass(this)
            val color = sp.getString("AppColor", "#0DCF31")
            binding.btnKnot.setCardBackgroundColor(Color.parseColor(color))
            binding.btnMph.setCardBackgroundColor(getColor(R.color.fadeColor))
            binding.btnKmh.setCardBackgroundColor(getColor(R.color.fadeColor))

            sp.putString("Unit", "KNOT")
        }
        binding.btnDone.setOnClickListener {
            onBackPressed()
        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        mediationBackPressedSimpleHoneyBeeMapNavigation(this,admobInterstitialNav)
    }

    private fun squareXGPSBannerAdsSmall() {
        val adContainer = findViewById<LinearLayout>(R.id.adContainer)
        val smallAd = findViewById<LinearLayout>(R.id.smallAd)
        SpeedMeterLoadAds.loadAdMobIntroMediumBannerAd(
            this, adContainer, smallAd
        )
    }

    override fun onResume() {
        super.onResume()
        settingTextView()
    }


    fun settingTextView() {
        val sp = SharedPreferenceHelperClass(this)
        val color = sp.getString("AppColor", "#0DCF31")
        binding.titleUnits.text = getString(R.string.speed_in)
        binding.titleTxt.text = getString(R.string.speed_units)
        binding.doneTV.text = getString(R.string.done)
        binding.btnDone.setCardBackgroundColor(Color.parseColor(color))


        when (sp.getString("Unit", "KMH")) {
            "KMH" -> {
                binding.btnKmh.setCardBackgroundColor(Color.parseColor(color))
                binding.btnMph.setCardBackgroundColor(getColor(R.color.fadeColor))
                binding.btnKnot.setCardBackgroundColor(getColor(R.color.fadeColor))
            }

            "MPH" -> {
                binding.btnMph.setCardBackgroundColor(Color.parseColor(color))
                binding.btnKmh.setCardBackgroundColor(getColor(R.color.fadeColor))
                binding.btnKnot.setCardBackgroundColor(getColor(R.color.fadeColor))
            }

            "KNOT" -> {
                binding.btnKnot.setCardBackgroundColor(Color.parseColor(color))
                binding.btnMph.setCardBackgroundColor(getColor(R.color.fadeColor))
                binding.btnKmh.setCardBackgroundColor(getColor(R.color.fadeColor))
            }

        }
    }
}