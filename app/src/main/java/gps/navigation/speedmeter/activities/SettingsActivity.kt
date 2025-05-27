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
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.adapters.ThemeAdapter
import gps.navigation.speedmeter.databinding.ActivitySettingsBinding
import gps.navigation.speedmeter.sharedprefrences.SharedPreferenceHelperClass
import gps.navigation.speedmeter.utils.Constants


class SettingsActivity : AppCompatActivity() {
    private val binding: ActivitySettingsBinding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }
    private lateinit var bottomSheet: BottomSheetBehavior<ConstraintLayout>

    var modeSpeed = "Driving"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        bottomSheet = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        settingSwitches()
        onClickEvents()
        binding.colorsRv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = ThemeAdapter(Constants.listTheme, object : ThemeAdapter.ColorCallback {
            override fun onColorChanger(color: String) {
                settingTextView(color)
            }
        })
        binding.colorsRv.adapter = adapter
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.btnRateUs.setOnClickListener {
            rateUs()
        }

        binding.btnPrivacyPolicy.setOnClickListener {
            val myIntent: Intent = Intent.parseUri(
                "https://speedometer69.blogspot.com/2025/05/speedometer-pp.html",
                Intent.URI_INTENT_SCHEME
            )
            startActivity(myIntent)
        }
        binding.btnLanguage.setOnClickListener {
            startActivity(Intent(this, LanguageActivity::class.java))
        }


        binding.btnShareApp.setOnClickListener {
            shareApp(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
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
        binding.speedLimit.setTextColor(Color.parseColor(color))
        binding.btnDone.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
        binding.speedIcon.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN)
        binding.speedWarning.setTextColor(Color.parseColor(color))
        val sp = SharedPreferenceHelperClass(this)
        binding.switchKeepScreenOn.isChecked = sp.getBoolean("KeepScreenOn", false)
        binding.switchVibration.isChecked = sp.getBoolean("Vibration", false)
        binding.switchNotificationBar.isChecked = sp.getBoolean("Notification", true)
        binding.switchRouteTracking.isChecked = sp.getBoolean("RouteTracking", true)
        binding.switchSoundEffects.isChecked = sp.getBoolean("SoundEffects", false)

        when (sp.getString("Unit", "KMH")) {
            "KMH" -> {
                binding.btnKmh.setBackgroundResource(R.drawable.selected_shape)
                binding.btnMph.setBackgroundResource(R.drawable.unselected_shape)
                binding.btnKnot.setBackgroundResource(R.drawable.unselected_shape)
                binding.btnKmh.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))

                binding.txtKmh.setTextColor(Color.parseColor("#000000"))
                binding.txtMph.setTextColor(Color.parseColor("#FFFFFF"))
                binding.txtKnot.setTextColor(Color.parseColor("#FFFFFF"))
            }

            "MPH" -> {
                binding.btnMph.setBackgroundResource(R.drawable.selected_shape)
                binding.btnKmh.setBackgroundResource(R.drawable.unselected_shape)
                binding.btnKnot.setBackgroundResource(R.drawable.unselected_shape)
                binding.btnMph.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))

                binding.txtMph.setTextColor(Color.parseColor("#000000"))
                binding.txtKmh.setTextColor(Color.parseColor("#FFFFFF"))
                binding.txtKnot.setTextColor(Color.parseColor("#FFFFFF"))
            }

            "KNOT" -> {
                binding.btnKnot.setBackgroundResource(R.drawable.selected_shape)
                binding.btnMph.setBackgroundResource(R.drawable.unselected_shape)
                binding.btnKmh.setBackgroundResource(R.drawable.unselected_shape)
                binding.btnKnot.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))

                binding.txtKnot.setTextColor(Color.parseColor("#000000"))
                binding.txtMph.setTextColor(Color.parseColor("#FFFFFF"))
                binding.txtKmh.setTextColor(Color.parseColor("#FFFFFF"))
            }
        }
        if (binding.switchKeepScreenOn.isChecked) changingSwitch(
            Color.parseColor(color),
            Color.parseColor(color),
            binding.switchKeepScreenOn
        )
        if (binding.switchVibration.isChecked) changingSwitch(
            Color.parseColor(color),
            Color.parseColor(color),
            binding.switchVibration
        )
        if (binding.switchRouteTracking.isChecked) changingSwitch(
            Color.parseColor(color),
            Color.parseColor(color),
            binding.switchRouteTracking
        )

        if (binding.switchNotificationBar.isChecked) changingSwitch(
            Color.parseColor(color),
            Color.parseColor(color),
            binding.switchNotificationBar
        )
        if (binding.switchSoundEffects.isChecked) changingSwitch(
            Color.parseColor(color),
            Color.parseColor(color),
            binding.switchSoundEffects
        )

        val mode = sp.getString("modeSpeed", modeSpeed)
        var limit = sp.getInt("speedLimit")
        if (limit == 0) {
            limit = 100
        }

        binding.speedWarning.text = limit.toString() + "Km/h"
        Log.d("TAG_LIMIT", "settingTextView: Mode $mode limit $limit")
        when (mode) {
            "Driving" -> {
                binding.speedLimit.setText(limit.toString())
                binding.btnDriving.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(color))
                binding.titleDriving.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))
                binding.drivingUnit.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))
                binding.limitDriving.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))
                binding.limitDriving.text = binding.speedLimit.text.toString()
            }

            "Walking" -> {
                binding.speedLimit.setText(limit.toString())
                binding.btnWalking.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(color))
                binding.titleWalking.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))
                binding.unitWalking.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))
                binding.limitWalking.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))
                binding.limitWalking.text = binding.speedLimit.text.toString()
            }

            "Cycling" -> {
                binding.speedLimit.setText(limit.toString())
                binding.btnCycling.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(color))
                binding.titleCycling.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))
                binding.unitCycling.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))
                binding.limitCycling.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))
                binding.limitCycling.text = binding.speedLimit.text.toString()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val sp = SharedPreferenceHelperClass(this)
        changingLanguage()
        settingTextView(sp.getString("AppColor", "#FBC100"))
        binding.languageName.text = sp.getString("languageName", "English")
    }

    fun changingLanguage() {
        binding.titleTxt.text = getString(R.string.settings)
        binding.titleUnits.text = getString(R.string.speed_in)
        binding.titleThemes.text = getString(R.string.select_theme)
        binding.notifiTV.text = getString(R.string.notification_bar)
        binding.vibrationTV.text = getString(R.string.vibration)
        binding.keepScreenOnTV.text = getString(R.string.keep_screen_on)
        binding.soundEffectTV.text = getString(R.string.sound_effects)
        binding.routeTrackingTV.text = getString(R.string.route_tracking_on_map)
        binding.speedWarningTV.text = getString(R.string.speed_warning)
        binding.historyTV.text = getString(R.string.history)
        binding.languageTV.text = getString(R.string.language)
        binding.rateUsTV.text = getString(R.string.rate_us)
        binding.shareAppTV.text = getString(R.string.shape_app)
        binding.ppTV.text = getString(R.string.privacy_policy)
        binding.titleLimit.text = getString(R.string.warning_when_over)
        binding.titleDriving.text = getString(R.string.driving)
        binding.titleCycling.text = getString(R.string.cycling)
        binding.titleWalking.text = getString(R.string.walking)
        binding.btnDone.text = getString(R.string.done)
    }

    fun settingSwitches() {
        binding.switchSoundEffects.setOnCheckedChangeListener { p0, isChecked ->
            if (isChecked) {
                val sp = SharedPreferenceHelperClass(this)
                changingSwitch(
                    Color.parseColor(sp.getString("AppColor", "#FBC100")),
                    Color.parseColor(sp.getString("AppColor", "#FBC100")),
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
                    Color.parseColor(sp.getString("AppColor", "#FBC100")),
                    Color.parseColor(sp.getString("AppColor", "#FBC100")),
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
                    Color.parseColor(sp.getString("AppColor", "#FBC100")),
                    Color.parseColor(sp.getString("AppColor", "#FBC100")),
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
                    Color.parseColor(sp.getString("AppColor", "#FBC100")),
                    Color.parseColor(sp.getString("AppColor", "#FBC100")),
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
                    Color.parseColor(sp.getString("AppColor", "#FBC100")),
                    Color.parseColor(sp.getString("AppColor", "#FBC100")),
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


        binding.btnDone.setOnClickListener {
            bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            val sp = SharedPreferenceHelperClass(this)
            sp.putString("modeSpeed", modeSpeed)
            sp.putInt("speedLimit", binding.speedLimit.text.toString().toInt())
            binding.speedWarning.text = binding.speedLimit.text.toString() + "Km/h"
        }

        binding.btnKmh.setOnClickListener {
            val sp = SharedPreferenceHelperClass(this)
            val color = sp.getString("AppColor", "#FBC100")

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
            val color = sp.getString("AppColor", "#FBC100")
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
            val color = sp.getString("AppColor", "#FBC100")
            binding.btnKnot.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
            binding.btnMph.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#303030"))
            binding.btnKmh.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#303030"))

            binding.txtKnot.setTextColor(Color.parseColor("#000000"))
            binding.txtMph.setTextColor(Color.parseColor("#FFFFFF"))
            binding.txtKmh.setTextColor(Color.parseColor("#FFFFFF"))
            sp.putString("Unit", "KNOT")
        }
        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        binding.btnSpeedWarning.setOnClickListener {
            bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.btnDriving.setOnClickListener {
            val sp = SharedPreferenceHelperClass(this)
            val color = sp.getString("AppColor", "#FBC100")
            modeSpeed = "Driving"

            binding.speedLimit.setText(binding.limitDriving.text.toString())
            binding.btnDriving.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
            binding.titleDriving.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))
            binding.drivingUnit.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))
            binding.limitDriving.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))

            binding.btnWalking.backgroundTintList = ColorStateList.valueOf(0)
            binding.titleWalking.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.unitWalking.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.limitWalking.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.limitWalking.text = "10"

            binding.btnCycling.backgroundTintList = ColorStateList.valueOf(0)
            binding.titleCycling.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.unitCycling.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.limitCycling.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.limitCycling.text = "25"
        }
        binding.btnWalking.setOnClickListener {
            val sp = SharedPreferenceHelperClass(this)
            val color = sp.getString("AppColor", "#FBC100")
            modeSpeed = "Walking"
            binding.speedLimit.setText(binding.limitWalking.text.toString())
            binding.btnWalking.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
            binding.titleWalking.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))
            binding.unitWalking.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))
            binding.limitWalking.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))

            binding.btnDriving.backgroundTintList = ColorStateList.valueOf(0)
            binding.titleDriving.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.drivingUnit.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.limitDriving.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.limitDriving.text = "100"

            binding.btnCycling.backgroundTintList = ColorStateList.valueOf(0)
            binding.titleCycling.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.unitCycling.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.limitCycling.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.limitCycling.text = "25"
        }
        binding.btnCycling.setOnClickListener {
            val sp = SharedPreferenceHelperClass(this)
            val color = sp.getString("AppColor", "#FBC100")
            modeSpeed = "Cycling"
            binding.speedLimit.setText(binding.limitCycling.text.toString())
            binding.btnCycling.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
            binding.titleCycling.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))
            binding.unitCycling.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))
            binding.limitCycling.setTextColor(ColorStateList.valueOf(Color.parseColor("#111424")))

            binding.btnDriving.backgroundTintList = ColorStateList.valueOf(0)
            binding.titleDriving.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.drivingUnit.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.limitDriving.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.limitDriving.text = "100"

            binding.btnWalking.backgroundTintList = ColorStateList.valueOf(0)
            binding.titleWalking.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.unitWalking.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.limitWalking.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
            binding.limitWalking.text = "10"
        }
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
                } else if (p1 > 4 && p1 <= 5) {
                    emoji.setImageResource(R.drawable.rate_5)
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                }, 1000)
            }

        }

        dialog.show()
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