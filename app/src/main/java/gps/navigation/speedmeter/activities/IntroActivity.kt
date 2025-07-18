package gps.navigation.speedmeter.activities

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.adapters.ViewPagerAdapter
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds
import gps.navigation.speedmeter.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    val binding: ActivityIntroBinding by lazy {
        ActivityIntroBinding.inflate(layoutInflater)
    }
    private lateinit var viewPager: ViewPager
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var layouts: IntArray
    private lateinit var dotsIndicator: WormDotsIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mHoneyBeeMapNavigationBannerAdsSmall()

        binding.btnBack.visibility = LinearLayout.GONE
        binding.btnNext.setBackgroundResource(R.drawable.next_second)
        dotsIndicator = findViewById(R.id.spring_dots_indicator)
        viewPager = findViewById(R.id.view_pager)
        layouts = intArrayOf(
            R.layout.slide1,
            R.layout.slide2,
            R.layout.slide3
        )
        viewPagerAdapter = ViewPagerAdapter(layouts)
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener)
        viewPager.adapter = viewPagerAdapter
        dotsIndicator.setViewPager(viewPager)
        binding.btnNext.setOnClickListener {
            val current = getItem(1)
            if (current < layouts.size) {
                // move to the next screen
                viewPager.currentItem = current
            } else {
                launchHomeScreen()
            }
        }
        binding.btnBack.setOnClickListener {
            // checking for the first page
            // if it's not the first page, move to the previous screen
            val current = getItem(-1)
            if (current >= 0) {
                viewPager.currentItem = current
            }
        }

    }

    private val viewPagerPageChangeListener = object : ViewPager.OnPageChangeListener {

        override fun onPageSelected(position: Int) {
            //    addBottomDots(position)
            when (position) {
                0 -> {
                    binding.btnNext.text = "Next"
                    binding.btnNext.setBackgroundResource(R.drawable.next_second)
                    binding.btnBack.visibility = LinearLayout.GONE
                }

                1 -> {
                    binding.btnNext.text = "Next"
                    binding.btnNext.setBackgroundResource(R.drawable.next_shape)
                    binding.btnBack.visibility = LinearLayout.VISIBLE
                }

                2 -> {
                    binding.btnNext.text = "Start"
                    binding.btnNext.setBackgroundResource(R.drawable.next_shape)
                    binding.btnBack.visibility = LinearLayout.VISIBLE
                }


            }

        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}

        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    private fun launchHomeScreen() {
        val intent = Intent(this@IntroActivity, MainActivity::class.java)
        startActivity(intent)

    }

    private fun mHoneyBeeMapNavigationBannerAdsSmall() {
        val adContainer = findViewById<LinearLayout>(R.id.adContainer)
        val smallAd = findViewById<LinearLayout>(R.id.smallAd)

        SpeedMeterLoadAds.loadAdMobIntroMediumBannerAd(
            this, adContainer, smallAd
        )

    }

    private fun getItem(i: Int): Int {
        return viewPager.currentItem + i
    }

    override fun onBackPressed() {

    }
}