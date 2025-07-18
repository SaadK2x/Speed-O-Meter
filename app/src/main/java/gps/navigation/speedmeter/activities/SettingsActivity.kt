package gps.navigation.speedmeter.activities

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import gps.navigation.speedmeter.BuildConfig
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds.admobInterstitialNav
import gps.navigation.speedmeter.ads.SpeedMeterShowAds.directAdsSpecificModulesSquareXNavigation
import gps.navigation.speedmeter.ads.SpeedMeterShowAds.mediationBackPressedSimpleHoneyBeeMapNavigation
import gps.navigation.speedmeter.databinding.ActivitySettingsBinding
import gps.navigation.speedmeter.settings.MaxSpeed
import gps.navigation.speedmeter.settings.ThemeSelection
import gps.navigation.speedmeter.settings.UnitSelection
import gps.navigation.speedmeter.sharedprefrences.SharedPreferenceHelperClass
import gps.navigation.speedmeter.utils.Constants


class SettingsActivity : AppCompatActivity() {
    private val binding: ActivitySettingsBinding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    var modeSpeed = "Driving"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        squareXGPSBannerAdsSmall()
        settingSwitches()
        onClickEvents()
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.speedUnitLayout.setOnClickListener {
            val intent = Intent(this, UnitSelection::class.java)
            directAdsSpecificModulesSquareXNavigation(this,
                SpeedMeterLoadAds.admobInterstitialNav,intent)
        }
        binding.themesLayout.setOnClickListener {
            val intent = Intent(this, ThemeSelection::class.java)
            directAdsSpecificModulesSquareXNavigation(this,
                SpeedMeterLoadAds.admobInterstitialNav,intent)
        }
        binding.maxSpeedLayout.setOnClickListener {
            val intent = Intent(this, MaxSpeed::class.java)
            directAdsSpecificModulesSquareXNavigation(this,
                SpeedMeterLoadAds.admobInterstitialNav,intent)
        }

        binding.languageLayout.setOnClickListener {
            val intent=(Intent(this, LanguageActivity::class.java))
            directAdsSpecificModulesSquareXNavigation(this,
                SpeedMeterLoadAds.admobInterstitialNav,intent)
        }
        binding.historyLayout.setOnClickListener {
            val intent=(Intent(this, HistoryActivity::class.java))
            directAdsSpecificModulesSquareXNavigation(this,
                SpeedMeterLoadAds.admobInterstitialNav,intent)
        }
        binding.btnShareApp.setOnClickListener {
            shareApp(this)
        }
    }

    private fun squareXGPSBannerAdsSmall() {
        val adContainer = findViewById<LinearLayout>(R.id.adContainer)
        val smallAd = findViewById<LinearLayout>(R.id.smallAd)
        SpeedMeterLoadAds.loadBanner(
            adContainer, smallAd, this
        )
    }

    override fun onBackPressed() {
        mediationBackPressedSimpleHoneyBeeMapNavigation(this,admobInterstitialNav)
    }

    fun shareApp(mContext: Context) {
        try {
            val shareAppIntent = Intent(Intent.ACTION_SEND)
            shareAppIntent.type = "text/plain"
            val shareSub = "Check out this application on play store!"
            val shareBody = mContext.getString(R.string.share_app_link)
            shareAppIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub)
            shareAppIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            mContext.startActivity(Intent.createChooser(shareAppIntent, "Share App using..."))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun changingSwitch(parseColor: Int, trackColor: Int, switch: SwitchCompat) {
        switch.thumbTintList = ColorStateList.valueOf(parseColor)
        switch.trackTintList = ColorStateList.valueOf(trackColor)
    }

    fun settingTextView(color: String) {

        binding.unitIcon.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN)
        binding.themeIcon.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN)
        binding.historyIcon.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN)
        binding.maxIcon.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN)
        binding.languageIcon.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN)

        binding.unitsInner.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
        binding.themeInner.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
        binding.historyInner.setStrokeColor(Color.parseColor(color))
        binding.languageInner.setStrokeColor(Color.parseColor(color))
        binding.maxInner.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))

        val sp = SharedPreferenceHelperClass(this)
        binding.switchKeepScreenOn.isChecked = sp.getBoolean("KeepScreenOn", false)
        binding.switchVibration.isChecked = sp.getBoolean("Vibration", false)
        binding.switchNotificationBar.isChecked = sp.getBoolean("Notification", true)
        binding.switchRouteTracking.isChecked = sp.getBoolean("RouteTracking", true)
        binding.switchSoundEffects.isChecked = sp.getBoolean("SoundEffects", false)

        when (sp.getString("Unit", "KMH")) {
            "KMH" -> {
                binding.selectedUnits.text = "Km/h"
            }

            "MPH" -> {
                binding.selectedUnits.text = "Mph"
            }

            "KNOT" -> {
                binding.selectedUnits.text = "Knot"
            }
        }
        if (binding.switchKeepScreenOn.isChecked) changingSwitch(
            Color.parseColor(color),
            Color.parseColor(color),
            binding.switchKeepScreenOn
        ) else {
            changingSwitch(
                Color.parseColor("#CACACA"),
                Color.parseColor("#777982"),
                binding.switchKeepScreenOn
            )
        }
        if (binding.switchVibration.isChecked) changingSwitch(
            Color.parseColor(color),
            Color.parseColor(color),
            binding.switchVibration
        ) else {
            changingSwitch(
                Color.parseColor("#CACACA"),
                Color.parseColor("#777982"),
                binding.switchVibration
            )
        }
        if (binding.switchRouteTracking.isChecked) changingSwitch(
            Color.parseColor(color),
            Color.parseColor(color),
            binding.switchRouteTracking
        ) else {
            changingSwitch(
                Color.parseColor("#CACACA"),
                Color.parseColor("#777982"),
                binding.switchRouteTracking
            )
        }

        if (binding.switchNotificationBar.isChecked) changingSwitch(
            Color.parseColor(color),
            Color.parseColor(color),
            binding.switchNotificationBar
        ) else {
            changingSwitch(
                Color.parseColor("#CACACA"),
                Color.parseColor("#777982"),
                binding.switchNotificationBar
            )
        }
        if (binding.switchSoundEffects.isChecked) changingSwitch(
            Color.parseColor(color),
            Color.parseColor(color),
            binding.switchSoundEffects
        ) else {
            changingSwitch(
                Color.parseColor("#CACACA"),
                Color.parseColor("#777982"),
                binding.switchSoundEffects
            )
        }

        val mode = sp.getString("modeSpeed", modeSpeed)
        var limit = sp.getInt("speedLimit")
        if (limit == 0) {
            limit = 100
        }

        binding.selectedSpeed.text = limit.toString() + "Km/h"
        Log.d("TAG_LIMIT", "settingTextView: Mode $mode limit $limit")
    }

    override fun onResume() {
        super.onResume()
        val sp = SharedPreferenceHelperClass(this)
        Constants.setLocale(this, sp.getString("language", "en"))
        changingLanguage()
        settingTextView(sp.getString("AppColor", "#0DCF31"))
        binding.selectedLanguage.text = sp.getString("languageName", "English")
    }

    fun changingLanguage() {
        binding.titleTxt.text = getString(R.string.settings)
        binding.notifiTV.text = getString(R.string.notification_bar)
        binding.vibrationTV.text = getString(R.string.vibration)
        binding.keepScreenOnTV.text = getString(R.string.keep_screen_on)
        binding.soundEffectTV.text = getString(R.string.sound_effects)
        binding.routeTrackingTV.text = getString(R.string.route_tracking_on_map)
        binding.shareAppTV.text = getString(R.string.shape_app)

    }

    fun settingSwitches() {
        binding.switchSoundEffects.setOnCheckedChangeListener { p0, isChecked ->
            if (isChecked) {
                val sp = SharedPreferenceHelperClass(this)
                changingSwitch(
                    Color.parseColor(sp.getString("AppColor", "#0DCF31")),
                    Color.parseColor(sp.getString("AppColor", "#0DCF31")),
                    binding.switchSoundEffects
                )
                sp.putBoolean("SoundEffects", true)
            } else {
                val sp = SharedPreferenceHelperClass(this)
                changingSwitch(
                    Color.parseColor("#CACACA"),
                    Color.parseColor("#777982"),
                    binding.switchSoundEffects
                )
                sp.putBoolean("SoundEffects", false)
            }

        }
        binding.switchKeepScreenOn.setOnCheckedChangeListener { p0, isChecked ->
            if (isChecked) {
                val sp = SharedPreferenceHelperClass(this)
                changingSwitch(
                    Color.parseColor(sp.getString("AppColor", "#0DCF31")),
                    Color.parseColor(sp.getString("AppColor", "#0DCF31")),
                    binding.switchKeepScreenOn
                )
                sp.putBoolean("KeepScreenOn", true)
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                val sp = SharedPreferenceHelperClass(this)
                changingSwitch(
                    Color.parseColor("#CACACA"),
                    Color.parseColor("#777982"),
                    binding.switchKeepScreenOn
                )
                sp.putBoolean("KeepScreenOn", false)
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }

        }
        binding.switchRouteTracking.setOnCheckedChangeListener { p0, isChecked ->
            if (isChecked) {
                val sp = SharedPreferenceHelperClass(this)
                changingSwitch(
                    Color.parseColor(sp.getString("AppColor", "#0DCF31")),
                    Color.parseColor(sp.getString("AppColor", "#0DCF31")),
                    binding.switchRouteTracking
                )
                sp.putBoolean("RouteTracking", true)
            } else {
                val sp = SharedPreferenceHelperClass(this)
                changingSwitch(
                    Color.parseColor("#CACACA"),
                    Color.parseColor("#777982"),
                    binding.switchRouteTracking
                )
                sp.putBoolean("RouteTracking", false)
            }

        }
        binding.switchVibration.setOnCheckedChangeListener { p0, isChecked ->
            if (isChecked) {
                val sp = SharedPreferenceHelperClass(this)
                changingSwitch(
                    Color.parseColor(sp.getString("AppColor", "#0DCF31")),
                    Color.parseColor(sp.getString("AppColor", "#0DCF31")),
                    binding.switchVibration
                )
                sp.putBoolean("Vibration", true)
            } else {
                val sp = SharedPreferenceHelperClass(this)
                changingSwitch(
                    Color.parseColor("#CACACA"),
                    Color.parseColor("#777982"),
                    binding.switchVibration
                )
                sp.putBoolean("Vibration", false)
            }

        }
        binding.switchNotificationBar.setOnCheckedChangeListener { p0, isChecked ->
            if (isChecked) {
                val sp = SharedPreferenceHelperClass(this)
                changingSwitch(
                    Color.parseColor(sp.getString("AppColor", "#0DCF31")),
                    Color.parseColor(sp.getString("AppColor", "#0DCF31")),
                    binding.switchNotificationBar
                )
                sp.putBoolean("Notification", true)
            } else {
                val sp = SharedPreferenceHelperClass(this)
                changingSwitch(
                    Color.parseColor("#CACACA"),
                    Color.parseColor("#777982"),
                    binding.switchNotificationBar
                )
                sp.putBoolean("Notification", false)
            }

        }
    }

    fun onClickEvents() {


        /*  binding.btnDone.setOnClickListener {
              bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
              val sp = SharedPreferenceHelperClass(this)
              sp.putString("modeSpeed", modeSpeed)
              sp.putInt("speedLimit", binding.speedLimit.text.toString().toInt())
              binding.speedWarning.text = binding.speedLimit.text.toString() + "Km/h"
          }

          binding.btnKmh.setOnClickListener {
              val sp = SharedPreferenceHelperClass(this)
              val color = sp.getString("AppColor", "#0DCF31")

              binding.btnKmh.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
              binding.btnMph.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#303030"))
              binding.btnKnot.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#303030"))

              binding.txtKmh.setTextColor(Color.parseColor("#000000"))
              binding.txtMph.setTextColor(Color.parseColor("#FFFFFF"))
              binding.txtKnot.setTextColor(Color.parseColor("#FFFFFF"))
              sp.putString("Unit", "KMH")
          }
          binding.btnMph.setOnClickListener {
              val sp = SharedPreferenceHelperClass(this)
              val color = sp.getString("AppColor", "#0DCF31")
              binding.btnMph.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
              binding.btnKmh.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#303030"))
              binding.btnKnot.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#303030"))

              binding.txtMph.setTextColor(Color.parseColor("#000000"))
              binding.txtKmh.setTextColor(Color.parseColor("#FFFFFF"))
              binding.txtKnot.setTextColor(Color.parseColor("#FFFFFF"))
              sp.putString("Unit", "MPH")
          }
          binding.btnKnot.setOnClickListener {
              val sp = SharedPreferenceHelperClass(this)
              val color = sp.getString("AppColor", "#0DCF31")
              binding.btnKnot.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
              binding.btnMph.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#303030"))
              binding.btnKmh.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#303030"))

              binding.txtKnot.setTextColor(Color.parseColor("#000000"))
              binding.txtMph.setTextColor(Color.parseColor("#FFFFFF"))
              binding.txtKmh.setTextColor(Color.parseColor("#FFFFFF"))
              sp.putString("Unit", "KNOT")
          }*/


    }

    fun rateUs() {
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        dialog.setContentView(R.layout.rateus_dialouge)
        dialog.setCancelable(true)

        val emoji = dialog.findViewById<ImageView>(R.id.emojiIcon)
        val ratingBar = dialog.findViewById<RatingBar>(R.id.ratingBar)

        ratingBar.onRatingBarChangeListener = object : RatingBar.OnRatingBarChangeListener {
            override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {
                if (p1 <= 1) {
                    emoji.setImageResource(R.drawable.rate_1)
                } else if (p1 > 1 && p1 <= 2) {
                    emoji.setImageResource(R.drawable.rate_2)
                } else if (p1 > 2 && p1 <= 3) {
                    emoji.setImageResource(R.drawable.rate_3)
                } else if (p1 > 3 && p1 <= 4) {
                    emoji.setImageResource(R.drawable.rate_4)
                    moveForward()
                } else if (p1 > 4 && p1 <= 5) {
                    emoji.setImageResource(R.drawable.rate_5)
                    moveForward()
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                }, 1000)
            }

        }

        dialog.show()
    }

    fun moveForward() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data =
            Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            val webIntent = Intent(Intent.ACTION_VIEW)
            webIntent.data =
                Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
            startActivity(webIntent)
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkCourseLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}