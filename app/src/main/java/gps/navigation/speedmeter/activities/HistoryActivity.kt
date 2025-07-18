package gps.navigation.speedmeter.activities

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.adapters.HistoryOuterAdapter
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds.admobInterstitialNav
import gps.navigation.speedmeter.ads.SpeedMeterShowAds.mediationBackPressedSimpleHoneyBeeMapNavigation
import gps.navigation.speedmeter.database.DatabaseBuilder
import gps.navigation.speedmeter.database.DatabaseHelperImpl
import gps.navigation.speedmeter.database.HistoryDatabase
import gps.navigation.speedmeter.database.HistoryWithRoutePoints
import gps.navigation.speedmeter.databinding.ActivityHistoryBinding
import gps.navigation.speedmeter.utils.HistoryDisplayItem
import gps.navigation.speedmeter.utils.OnItemDelete
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity(), OnItemDelete {

    var historyDatabase: HistoryDatabase? = null
    var databaseHelper: DatabaseHelperImpl? = null
    var mAdapter: HistoryOuterAdapter? = null
    var list: ArrayList<HistoryWithRoutePoints>? = null
    private val binding: ActivityHistoryBinding by lazy {
        ActivityHistoryBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        squareXGPSBannerAdsSmall()
        binding.clearTV.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val list = databaseHelper!!.getDeleteAll()
                withContext(Dispatchers.Main) {
                    if (list > 0) {
                        Toast.makeText(this@HistoryActivity, "Data Cleared", Toast.LENGTH_SHORT)
                            .show()
                        mAdapter = HistoryOuterAdapter(ArrayList(), this@HistoryActivity)
                        binding.list.layoutManager = LinearLayoutManager(this@HistoryActivity)
                        binding.list.adapter = mAdapter
                    }
                }
            }
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
        SpeedMeterLoadAds.loadBanner(
            adContainer, smallAd, this
        )
    }

    override fun onItemClick(id: Int, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val lit = databaseHelper!!.getDeleteSpecific(id)
            withContext(Dispatchers.Main) {
                if (lit > 0) {
                    mAdapter?.notifyItemRemoved(position)
                    list?.removeAt(position)
                    mAdapter?.notifyDataSetChanged()
                    Toast.makeText(this@HistoryActivity, "Data Deleted", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        historyDatabase = DatabaseBuilder.getInstance(this)
        databaseHelper = DatabaseHelperImpl(historyDatabase!!)
        CoroutineScope(Dispatchers.IO).launch {
            list = databaseHelper!!.getHistoryWithRoutePoints() as ArrayList<HistoryWithRoutePoints>
            val groupedByDate: Map<String, List<HistoryWithRoutePoints>> =
                list!!.groupBy { it.history.date }
            val displayList = mutableListOf<HistoryDisplayItem>()
            groupedByDate.forEach { (date, items) ->
                displayList.add(HistoryDisplayItem(date, items))
            }
            Log.d("TAG_LL", "onCreate: $groupedByDate")
            withContext(Dispatchers.Main) {
                mAdapter = HistoryOuterAdapter(displayList, this@HistoryActivity)
                binding.list.layoutManager = LinearLayoutManager(this@HistoryActivity)
                binding.list.adapter = mAdapter
            }
        }
    }
}