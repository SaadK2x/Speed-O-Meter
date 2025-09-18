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
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.databinding.FragmentDigitalBinding
import gps.navigation.speedmeter.sharedprefrences.SharedPreferenceHelperClass
import gps.navigation.speedmeter.utils.Constants
import gps.navigation.speedmeter.utils.Constants.countAnimKMH
import gps.navigation.speedmeter.utils.Constants.countAnimKNOTS
import gps.navigation.speedmeter.utils.Constants.countAnimMPH
import gps.navigation.speedmeter.utils.Constants.viewScaling

class DigitalFragment : Fragment() {
    private val binding: FragmentDigitalBinding by lazy {
        FragmentDigitalBinding.inflate(layoutInflater)
    }

    var sharedPrefrences: SharedPreferenceHelperClass? = null

    var isResume = true
    var isStop = true
    var startKMH = 0
    var startMPH = 0f
    var startKNOTS = 0f
    private var isSpeedReceiverRegistered = false
    private var isStartReceiverRegistered = false
    private var isPauseReceiverRegistered = false
    private var isResetReceiverRegistered = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
        isStartReceiverRegistered = true
        isPauseReceiverRegistered = true
        isResetReceiverRegistered = true
        Constants.moveForward.observe(requireActivity()) {
            if (it != "No") {
                binding.playTV.text = context?.getString(R.string.start) ?: "Start"
                binding.playProgress.visibility = View.GONE
            }
        }
        Constants.startStop.observe(requireActivity()){
            valuesStart_Stop(it)
        }
        return binding.root
    }


    private val speedUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val speed = intent?.getFloatExtra("speed", 0.0f)
            val time = intent?.getStringExtra("time")
            val distance = intent?.getStringExtra("distance")
            val avgSpeed = intent?.getStringExtra("avgSpeed")
            Log.d("TAG_SPeed", "onReceive: Running $speed")
            if (time != "")
                binding.time.text = time
            when (sharedPrefrences?.getString("Unit", "KMH")) {
                "KMH" -> {
                    binding.distance.text = distance
                    binding.avgSpeed.text = avgSpeed
                    countAnimKMH(startKMH, speed!!.toInt(), text = binding.speedTV)

                    startKMH = speed.toInt()
                }

                "MPH" -> {
                    binding.distance.text =
                        Constants.kmTomi(distance!!.toFloat()).toInt().toString()
                    binding.avgSpeed.text =
                        Constants.kmhToMph(avgSpeed!!.toFloat()).toInt().toString()

                    countAnimMPH(startMPH, speed!!, text = binding.speedTV)

                    startMPH = Constants.kmhToMph(speed)
                }

                "KNOT" -> {
                    binding.distance.text =
                        Constants.kmTonm(distance!!.toFloat()).toInt().toString()
                    binding.avgSpeed.text =
                        Constants.kmhToKnots(avgSpeed!!.toFloat()).toInt().toString()
                    countAnimKNOTS(startKNOTS, speed!!, text = binding.speedTV)

                    startKNOTS = Constants.kmhToKnots(speed)
                }
            }

            val maxSpeed = binding.maxSpeed.text.toString().toFloat().toInt()
            if (maxSpeed < speed!!.toInt()) {
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
            val isDestroying = intent?.getBooleanExtra("isDestroying", false) ?: false
            Log.d("TAG__", "Digital Fragment: isStart $start")
            if (!isDestroying) {

            }
        }
    }

    fun valuesStart_Stop(start:String){
        if (start == "Start") {
            isStop = false
            binding.playTV.text = context?.getString(R.string.stop) ?: "Stop"
            viewScaling(0f, 1f, true, binding.pauseBtn)
            viewScaling(0f, 1f, true, binding.resetBtn)
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
            } else {
                activity?.registerReceiver(
                    speedUpdateReceiver,
                    IntentFilter("ACTION_SPEED_UPDATE")
                )
            }
            isSpeedReceiverRegistered = true
        }
        else if (start == "Stop") {
            isStop = true
            if (isSpeedReceiverRegistered) {
                activity?.unregisterReceiver(speedUpdateReceiver)
                isSpeedReceiverRegistered = false
            }
            binding.playTV.text = context?.getString(R.string.saving) ?: "Saving"
            binding.playProgress.visibility = View.VISIBLE
            viewScaling(1f, 0f, false, binding.pauseBtn)
            viewScaling(1f, 0f, false, binding.resetBtn)
            binding.time.text = "00:00:00"
            binding.distance.text = "0"
            binding.maxSpeed.text = "0"
            binding.avgSpeed.text = "0"
            Handler(Looper.getMainLooper()).postDelayed({
                binding.speedTV.text = "00"
            }, 1500)
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
    }

    fun changeUnit(unit: String) {
        when (unit) {
            "KMH" -> {
                binding.distanceUnit.text = "km"
                binding.avgSpeedUnit.text = "Km/h"
                binding.maxSpeedUnit.text = "Km/h"
                binding.unitTV.text = "Km/h"
            }

            "MPH" -> {
                binding.distanceUnit.text = "mi"
                binding.avgSpeedUnit.text = "Mph"
                binding.maxSpeedUnit.text = "Mph"
                binding.unitTV.text = "Mph"
            }

            "KNOT" -> {
                binding.distanceUnit.text = "nm"
                binding.maxSpeedUnit.text = "Knots"
                binding.avgSpeedUnit.text = "Knots"
                binding.unitTV.text = "Knots"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            if (isPauseReceiverRegistered) {
                activity?.unregisterReceiver(pauseUpdateReceiver)
            }
            if (isSpeedReceiverRegistered) {
                activity?.unregisterReceiver(speedUpdateReceiver)
            }
            if (isStartReceiverRegistered) {
                activity?.unregisterReceiver(startUpdateReceiver)
            }
            if (isResetReceiverRegistered) {
                activity?.unregisterReceiver(resetUpdateReceiver)
            }
        } catch (e: IllegalArgumentException) {
            // Already unregistered
        }
    }
}