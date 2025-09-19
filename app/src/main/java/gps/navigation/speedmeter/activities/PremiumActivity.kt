package gps.navigation.speedmeter.activities

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.ads.SpeedMeterPurchaseSubs
import gps.navigation.speedmeter.databinding.ActivityPremiumBinding
import gps.navigation.speedmeter.utils.Constants

class PremiumActivity : AppCompatActivity() {
    val binding: ActivityPremiumBinding by lazy {
        ActivityPremiumBinding.inflate(layoutInflater)
    }
    var currentCenteredPosition: Int = 0
    private var purchaseHelper: SpeedMeterPurchaseSubs? = null
    private var countDownTimer: CountDownTimer? = null
    var isProgress=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        purchaseHelper = SpeedMeterPurchaseSubs(this)


        binding.tvWeekly.text = Constants.billingWeekly
        binding.tvMonthly.text = Constants.billingMonthly
        binding.tvYearly.text = Constants.billingYearly

        binding.tvFreeTrial.visibility = View.GONE

        val scaleAnimator = ValueAnimator.ofFloat(1.0f, 1.05f).apply {
            duration = 1000 // Duration of the animation
            repeatCount = ValueAnimator.INFINITE // Repeat infinitely
            repeatMode = ValueAnimator.REVERSE // Reverse direction on repeat
            interpolator = AccelerateDecelerateInterpolator() // Smooth transition
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                binding.btnLifeTime.scaleX = animatedValue
                binding.btnLifeTime.scaleY = animatedValue
            }
        }
        scaleAnimator.start()

        // Initialize progress and countdown
        initializeProgressAndCountdown()
        
        binding.crossBtn.setOnClickListener {
            onBackPressed()
        }

        binding.mcMonthly.setOnClickListener {
            currentCenteredPosition = 0
            binding.tvTextChnage.text = "Continue"
            binding.tvFreeTrial.visibility = View.GONE
            binding.mcMonthly.setBackgroundResource(R.drawable.selected_bg)
            binding.imageView10.setImageResource(R.drawable.selected_icn)

            binding.mcYearly.setBackgroundResource(R.drawable.unselected_bg)
            binding.imageView9.setImageResource(R.drawable.unslected_icn)

            binding.mcWeekly.setBackgroundResource(R.drawable.unselected_bg)
            binding.imageView8.setImageResource(R.drawable.unslected_icn)
            binding.tvSubsText.text = getString(R.string.subscription_auto_)
        }
        binding.mcYearly.setOnClickListener {
            currentCenteredPosition = 1
            binding.tvTextChnage.text = "3 Days Free Trial"
            binding.tvFreeTrial.text =
                "Get 3 days free trial!\nThen ${Constants.billingYearly}/year"
            binding.tvFreeTrial.visibility = View.VISIBLE
            binding.mcYearly.setBackgroundResource(R.drawable.selected_bg)
            binding.imageView9.setImageResource(R.drawable.selected_icn)

            binding.mcMonthly.setBackgroundResource(R.drawable.unselected_bg)
            binding.imageView10.setImageResource(R.drawable.unslected_icn)

            binding.mcWeekly.setBackgroundResource(R.drawable.unselected_bg)
            binding.imageView8.setImageResource(R.drawable.unslected_icn)
            binding.tvSubsText.text = getString(R.string.subscription_yearly_)
        }
        binding.mcWeekly.setOnClickListener {
            currentCenteredPosition = 2
            binding.tvTextChnage.text = "Continue"
            binding.tvFreeTrial.visibility = View.GONE

            binding.mcWeekly.setBackgroundResource(R.drawable.selected_bg)
            binding.imageView8.setImageResource(R.drawable.selected_icn)

            binding.mcMonthly.setBackgroundResource(R.drawable.unselected_bg)
            binding.imageView10.setImageResource(R.drawable.unslected_icn)

            binding.mcYearly.setBackgroundResource(R.drawable.unselected_bg)
            binding.imageView9.setImageResource(R.drawable.unslected_icn)
            binding.tvSubsText.text = getString(R.string.subscription_auto_)
        }


        binding.btnLifeTime.setOnClickListener {
            Log.d("values", "onCreate: " + currentCenteredPosition)
            currentCenteredPosition.let { currentCenteredPosition ->
                when (currentCenteredPosition) {
                    0 -> {
                        purchaseHelper!!.purchaseAIStreetViewWeeklyPackage()
                        Log.d("values", "onCreate: " + currentCenteredPosition)
                    }

                    1 -> {

                        purchaseHelper!!.purchaseAIStreetViewMonthlyPackage()
                        Log.d("values", "onCreate: " + currentCenteredPosition)
                    }

                    2 -> {
                        purchaseHelper!!.purchaseAIStreetViewYearlyPackage()
                        Log.d("values", "onCreate: " + currentCenteredPosition)
                    }
                }
            }
        }
    }

    private fun initializeProgressAndCountdown() {
        // Initially hide cross button and show progress
        binding.crossBtn.visibility = View.GONE
        binding.progressContainer.visibility = View.VISIBLE
        
        // Initialize progress bar
        binding.progressBar.max = 100
        binding.progressBar.progress = 0
        
        // Start countdown timer
        countDownTimer = object : CountDownTimer(5000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000) + 1
                val progress = ((5000 - millisUntilFinished) * 100) / 5000
                
                binding.countdownText.text = secondsRemaining.toString()
                binding.progressBar.progress = progress.toInt()
            }

            override fun onFinish() {
                // Hide progress container and show cross button
                binding.progressContainer.visibility = View.GONE
                binding.crossBtn.visibility = View.VISIBLE
                isProgress=false
            }
        }
        countDownTimer?.start()
    }

    override fun onBackPressed() {
        if (!isProgress) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}