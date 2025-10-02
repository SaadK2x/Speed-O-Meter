package gps.navigation.speedmeter.fragments

import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.databinding.FragmentGaugeBinding
import gps.navigation.speedmeter.sharedprefrences.SharedPreferenceHelperClass
import gps.navigation.speedmeter.utils.Constants
import gps.navigation.speedmeter.views.CustomSpeedometerView
import androidx.core.content.ContextCompat


class GaugeFragment : Fragment() {
    private val binding: FragmentGaugeBinding by lazy {
        FragmentGaugeBinding.inflate(layoutInflater)
    }
    var sharedPrefrences: SharedPreferenceHelperClass? = null

    var isResume = true
    var isStop = true
    
    // Track receiver registration state
    private var isSpeedUpdateReceiverRegistered = false
    private var isStartUpdateReceiverRegistered = false
    private var isPauseUpdateReceiverRegistered = false
    private var isResetUpdateReceiverRegistered = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedPrefrences = SharedPreferenceHelperClass(requireContext())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity?.registerReceiver(
                startUpdateReceiver,
                IntentFilter("ACTION_START_UPDATE"),
                Context.RECEIVER_EXPORTED
            )
            activity?.registerReceiver(
                pauseUpdateReceiver,
                IntentFilter("ACTION_PAUSE_UPDATE"),
                Context.RECEIVER_EXPORTED
            )
            activity?.registerReceiver(
                resetUpdateReceiver,
                IntentFilter("ACTION_RESET_UPDATE"),
                Context.RECEIVER_EXPORTED
            )
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            activity?.registerReceiver(
                startUpdateReceiver,
                IntentFilter("ACTION_START_UPDATE"),
                Context.RECEIVER_NOT_EXPORTED
            )
            activity?.registerReceiver(
                pauseUpdateReceiver,
                IntentFilter("ACTION_PAUSE_UPDATE"),
                Context.RECEIVER_NOT_EXPORTED
            )
            activity?.registerReceiver(
                resetUpdateReceiver,
                IntentFilter("ACTION_RESET_UPDATE"),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            activity?.registerReceiver(startUpdateReceiver, IntentFilter("ACTION_START_UPDATE"))
            activity?.registerReceiver(pauseUpdateReceiver, IntentFilter("ACTION_PAUSE_UPDATE"))
            activity?.registerReceiver(resetUpdateReceiver, IntentFilter("ACTION_RESET_UPDATE"))
        }
        
        // Mark receivers as registered
        isStartUpdateReceiverRegistered = true
        isPauseUpdateReceiverRegistered = true
        isResetUpdateReceiverRegistered = true
        Constants.startStop.observe(requireActivity()){
            valuesStart_Stop(it)
        }

        Constants.moveForward.observe(requireActivity()) {
            if (it != "No") {
                binding.playTV.text = context?.getString(R.string.start) ?: "Start"
                binding.playProgress.visibility = View.GONE
            }
        }

        // Initialize custom speedometer view
      //  initializeCustomSpeedometer()

        return binding.root
    }

    /**
     * Initialize the custom speedometer view with meter and needle drawables
     */
    private fun initializeCustomSpeedometer() {
        val customSpeedometer = binding.speedView as CustomSpeedometerView
        
        // Set meter background drawable
        customSpeedometer.setMeterDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.meter_5))
        // Set needle drawable (using test needle for debugging)
        customSpeedometer.setNeedleDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.needle_latest))
        // Set maximum speed (adjust based on your meter design)
        customSpeedometer.setMaxSpeed(160f)
        customSpeedometer.setMinSpeed(0f)
        
        // Set meter scale to fit properly on screen (optional - defaults to 0.9f)
        customSpeedometer.setMeterScale(0.85f)
    }

    private val speedUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val speed = intent?.getFloatExtra("speed", 0.0f)
            val distance = intent?.getStringExtra("distance")
            val time = intent?.getStringExtra("time")
            val avgSpeed = intent?.getStringExtra("avgSpeed")
            when (sharedPrefrences?.getString("Unit", "KMH")) {
                "KMH" -> {
                    binding.distance.text = distance
                    binding.avgSpeed.text = avgSpeed
                    binding.speedTV.text = (speed!!.toInt().toString())
                }

                "MPH" -> {
                    binding.distance.text =
                        Constants.kmTomi(distance!!.toFloat()).toInt().toString()
                    binding.avgSpeed.text =
                        Constants.kmhToMph(avgSpeed!!.toFloat()).toInt().toString()
                    binding.speedTV.text = Constants.kmhToMph(speed!!).toInt().toString()
                }

                "KNOT" -> {
                    binding.distance.text =
                        Constants.kmTonm(distance!!.toFloat()).toInt().toString()
                    binding.avgSpeed.text =
                        Constants.kmhToKnots(avgSpeed!!.toFloat()).toInt().toString()
                    binding.speedTV.text = Constants.kmhToKnots(speed!!).toInt().toString()
                }
            }
            Log.d("TAG_SPEED1", "onCreateView: GaugeFragment $time")
            if (time != "") {
                binding.time.text = time
            }
           // (binding.speedView as CustomSpeedometerView).setSpeed(speed!!)

            binding.speedView.speedTo(speed!!)
            val maxSpeed = binding.maxSpeed.text.toString().toFloat().toInt()
            if (maxSpeed < speed.toInt()) {
                when (sharedPrefrences?.getString("Unit", "KMH")) {
                    "KMH" -> {
                        binding.maxSpeed.text = speed.toString()
                    }

                    "MPH" -> {

                        binding.maxSpeed.text =
                            Constants.kmhToMph(maxSpeed.toFloat()).toInt().toString()

                    }

                    "KNOT" -> {
                        binding.maxSpeed.text =
                            Constants.kmhToKnots(maxSpeed.toFloat()).toInt().toString()

                    }
                }
            }

        }
    }


    private val startUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val start = intent?.getStringExtra("isStart")

        }
    }


    private val pauseUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val start = intent?.getBooleanExtra("isPause", false) ?: false
            isResume = !start
            if (start) {
                if (activity != null) {
                    binding.pauseIcon.setImageResource(R.drawable.play_icon)
                    binding.pauseTxt.text = getString(R.string.resume)
                }
            } else {
                if (activity != null) {
                    binding.pauseIcon.setImageResource(R.drawable.resume_icon)
                    binding.pauseTxt.text = getString(R.string.pause)
                }
            }
        }
    }
    private val resetUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val rotation = ObjectAnimator.ofFloat(binding.resetIcon, View.ROTATION, 0f, 360f)
            rotation.duration = 1000
            rotation.interpolator = AccelerateDecelerateInterpolator()
            rotation.start()
        }
    }


    fun valuesStart_Stop(start:String){
        if (start == "Start") {
            isStop = false
            binding.playTV.text = context?.getString(R.string.stop) ?: "Stop"
            Constants.viewScaling(0f, 1f, true, binding.pauseBtn)
            Constants.viewScaling(0f, 1f, true, binding.resetBtn)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                activity?.registerReceiver(
                    speedUpdateReceiver,
                    IntentFilter("ACTION_SPEED_UPDATE"),
                    Context.RECEIVER_EXPORTED
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity?.registerReceiver(
                    speedUpdateReceiver,
                    IntentFilter("ACTION_SPEED_UPDATE"),
                    Context.RECEIVER_NOT_EXPORTED
                )

            }else {
                activity?.registerReceiver(
                    speedUpdateReceiver,
                    IntentFilter("ACTION_SPEED_UPDATE")
                )
            }
            isSpeedUpdateReceiverRegistered = true

        }
        else if (start == "Stop") {
            isStop = true

            if (isSpeedUpdateReceiverRegistered) {
                try {
                    activity?.unregisterReceiver(speedUpdateReceiver)
                    isSpeedUpdateReceiverRegistered = false
                } catch (e: IllegalArgumentException) {
                    // Receiver was already unregistered
                    isSpeedUpdateReceiverRegistered = false
                }
            }
            binding.playTV.text = context?.getString(R.string.saving) ?: "Saving"
            binding.playProgress.visibility = View.VISIBLE
            Constants.viewScaling(1f, 0f, false, binding.pauseBtn)
            Constants.viewScaling(1f, 0f, false, binding.resetBtn)
            binding.time.text = "00:00:00"
         //   (binding.speedView as CustomSpeedometerView).setSpeed(0f, false)

            binding.speedView.speedTo(0f)
            binding.speedTV.text = "0"
            binding.distance.text = "0"
            binding.maxSpeed.text = "0"
            binding.avgSpeed.text = "0"
        }
    }
    override fun onResume() {
        super.onResume()
        val sp = SharedPreferenceHelperClass(requireContext())
        Constants.setLocale(requireActivity(), sp.getString("language", "en"))
        updatingText()
        settingColors(sp.getString("AppColor", "#0DCF31"))
        changeUnit(sp.getString("Unit", "KMH"))

    }

    fun updatingText() {
        binding.durationTV.text = getString(R.string.duration)
        binding.distanceTV.text = getString(R.string.distance)
        binding.avgSpeedTV.text = getString(R.string.avg_speed)
        binding.maxSpeedTV.text = getString(R.string.max_speed)
        binding.resetTV.text = getString(R.string.reset)
        if (isResume) {
            binding.pauseTxt.text = getString(R.string.pause)
        } else {
            binding.pauseTxt.text = getString(R.string.resume)
        }
        if (isStop) {
            binding.playTV.text = getString(R.string.start)
        } else {
            binding.playTV.text = getString(R.string.stop)
        }
    }

    fun settingColors(color: String) {
        binding.resetIcon.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN)
        binding.playBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
        binding.pauseIcon.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN)

        binding.time.setTextColor(Color.parseColor(color))
        binding.distance.setTextColor(Color.parseColor(color))
        binding.distanceUnit.setTextColor(Color.parseColor(color))
        binding.avgSpeed.setTextColor(Color.parseColor(color))
        binding.avgSpeedUnit.setTextColor(Color.parseColor(color))
        binding.maxSpeed.setTextColor(Color.parseColor(color))
        binding.maxSpeedUnit.setTextColor(Color.parseColor(color))
        binding.speedTV.setTextColor(Color.parseColor(color))
    }

    fun changeUnit(unit: String) {
        when (unit) {
            "KMH" -> {
                binding.distanceUnit.text = "km"
                binding.avgSpeedUnit.text = "Km/h"
                binding.maxSpeedUnit.text = "Km/h"
                binding.speedUnitTV.text = "Km/h"
            }

            "MPH" -> {
                binding.distanceUnit.text = "mi"
                binding.avgSpeedUnit.text = "Mph"
                binding.maxSpeedUnit.text = "Mph"
                binding.speedUnitTV.text = "Mph"
            }

            "KNOT" -> {
                binding.distanceUnit.text = "nm"
                binding.maxSpeedUnit.text = "Knots"
                binding.avgSpeedUnit.text = "Knots"
                binding.speedUnitTV.text = "Knots"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        
        // Safely unregister all receivers
        if (isSpeedUpdateReceiverRegistered) {
            try {
                requireActivity().unregisterReceiver(speedUpdateReceiver)
                isSpeedUpdateReceiverRegistered = false
            } catch (e: IllegalArgumentException) {
                // Already unregistered
                isSpeedUpdateReceiverRegistered = false
            }
        }
        
        if (isStartUpdateReceiverRegistered) {
            try {
                requireActivity().unregisterReceiver(startUpdateReceiver)
                isStartUpdateReceiverRegistered = false
            } catch (e: IllegalArgumentException) {
                // Already unregistered
                isStartUpdateReceiverRegistered = false
            }
        }
        
        if (isPauseUpdateReceiverRegistered) {
            try {
                requireActivity().unregisterReceiver(pauseUpdateReceiver)
                isPauseUpdateReceiverRegistered = false
            } catch (e: IllegalArgumentException) {
                // Already unregistered
                isPauseUpdateReceiverRegistered = false
            }
        }
        
        if (isResetUpdateReceiverRegistered) {
            try {
                requireActivity().unregisterReceiver(resetUpdateReceiver)
                isResetUpdateReceiverRegistered = false
            } catch (e: IllegalArgumentException) {
                // Already unregistered
                isResetUpdateReceiverRegistered = false
            }
        }
    }
}