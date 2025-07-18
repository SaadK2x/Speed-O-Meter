package gps.navigation.speedmeter.settings

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.adapters.ThemeAdapter
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds.admobInterstitialNav
import gps.navigation.speedmeter.ads.SpeedMeterShowAds.mediationBackPressedSimpleHoneyBeeMapNavigation
import gps.navigation.speedmeter.databinding.ActivityThemeSelectionBinding
import gps.navigation.speedmeter.sharedprefrences.SharedPreferenceHelperClass
import gps.navigation.speedmeter.utils.Constants

class ThemeSelection : AppCompatActivity() {
    val binding: ActivityThemeSelectionBinding by lazy {
        ActivityThemeSelectionBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        squareXGPSBannerAdsSmall()

        binding.btnDone.setOnClickListener {
            onBackPressed()
        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        mediationBackPressedSimpleHoneyBeeMapNavigation(this, admobInterstitialNav)
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
        val pos = sp.getInt("colorPos")
        for (i in 0..Constants.listTheme.size - 1) {
            if (i == pos) {
                Constants.listTheme[i].isSelected = true
            } else {
                Constants.listTheme[i].isSelected = false
            }
        }
        val color = sp.getString("AppColor", "#0DCF31")
        binding.titleUnits.text = getString(R.string.select_theme)
        binding.titleTxt.text = getString(R.string.themes)
        binding.doneTV.text = getString(R.string.done)
        binding.btnDone.setCardBackgroundColor(Color.parseColor(color))
        binding.colorsRv.layoutManager =
            GridLayoutManager(this, 8, GridLayoutManager.VERTICAL, false)
        val adapter = ThemeAdapter(Constants.listTheme, object : ThemeAdapter.ColorCallback {
            @SuppressLint("UseKtx")
            override fun onColorChanger(color: String) {
                binding.btnDone.setCardBackgroundColor(Color.parseColor(color))
            }
        })
        binding.colorsRv.adapter = adapter
    }
}