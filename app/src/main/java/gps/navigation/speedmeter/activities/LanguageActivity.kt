package gps.navigation.speedmeter.activities

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.adapters.LanguageAdapter
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds
import gps.navigation.speedmeter.databinding.ActivityLanguageBinding
import gps.navigation.speedmeter.sharedprefrences.SharedPreferenceHelperClass
import gps.navigation.speedmeter.utils.Constants
import gps.navigation.speedmeter.utils.OnLanguageClick

class LanguageActivity : AppCompatActivity(), OnLanguageClick {
    val binding: ActivityLanguageBinding by lazy {
        ActivityLanguageBinding.inflate(layoutInflater)
    }
    var mAdapter: LanguageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        squareXGPSBannerAdsSmall()

        binding.backBtn.setOnClickListener {
            onBackPressed()
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
        super.onBackPressed()
    }

    override fun onItemClick(code: String, name: String, position: Int) {
        Constants.setLocale(this, code)
        val sp = SharedPreferenceHelperClass(this)
        sp.putString("language", code)
        sp.putString("languageName", name)
        sp.putInt("languagePos", position)
        for (i in 0..<Constants.arrayLang.size) {
            if (position == i) {
                Log.d("TAG_POS", "onItemClick: i=$i and position=$position  true")
                Constants.arrayLang[i].isChecked = true
            } else {
                Constants.arrayLang[i].isChecked = false
            }
        }
        mAdapter!!.notifyDataSetChanged()

        this.recreate()
    }

    override fun onResume() {
        super.onResume()
        val sp = SharedPreferenceHelperClass(this)
        val position = sp.getInt("languagePos")
        for (i in 0..<Constants.arrayLang.size) {
            if (position == i) {
                Log.d("TAG_POS", "onItemClick: i=$i and position=$position  true")
                Constants.arrayLang[i].isChecked = true
            } else {
                Constants.arrayLang[i].isChecked = false
            }
        }

        mAdapter = LanguageAdapter(Constants.arrayLang, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = mAdapter
    }
}