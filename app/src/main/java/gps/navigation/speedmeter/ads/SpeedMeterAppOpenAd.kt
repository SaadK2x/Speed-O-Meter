package gps.navigation.speedmeter.ads

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.activities.SplashActivity
import gps.navigation.speedmeter.utils.MyApp
import java.util.Date

class SpeedMeterAppOpenAd(private val myApplication: MyApp) : LifecycleObserver,
    Application.ActivityLifecycleCallbacks {


    private val TAG = "StartAdLogs:"
    private var startOpenAd: AppOpenAd? = null
    private var callBackLoading: AppOpenAd.AppOpenAdLoadCallback? = null
    private var runningActivity: Activity? = null
    private var adLoadingTime: Long = 0

    companion object {
        private var isDisplayingAd = false
    }

    init {
        myApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun showHoneyBeeMapNavigationStartAppOpenAd() {
        val dialog = Dialog(runningActivity!!)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.loading_dialog)
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.getWindow()!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        if (!isDisplayingAd && isStartAdAvailable) {
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        dialog.dismiss()
                        startOpenAd = null
                        isDisplayingAd = false
                        loadHoneyBeeMapNavigationStartAppOpenAd()

                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    }

                    override fun onAdShowedFullScreenContent() {
                        isDisplayingAd = true
                    }
                }
            dialog.show()
            Handler(Looper.getMainLooper()).postDelayed({

                startOpenAd!!.show(runningActivity!!)
                Log.d(TAG, "showStartAppOpenAd: Showing App open ad")
                startOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
            }, 1000)
        } else {
            Log.d(TAG, "Can not show ad.")
            loadHoneyBeeMapNavigationStartAppOpenAd()

        }
    }

    fun loadHoneyBeeMapNavigationStartAppOpenAd() {
        if (isStartAdAvailable) {
            Log.d(TAG, "Ad is available App Open Ad ")
        } else {
            Log.d(TAG, "Ad requesting App Open Ad ")
            callBackLoading = object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(p0: AppOpenAd) {
                    super.onAdLoaded(p0)
                    Log.d(TAG, "onAppOpenAdLoaded")
                    this@SpeedMeterAppOpenAd.startOpenAd = p0
                    adLoadingTime = Date().time
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    Log.d(TAG, "onAppOpenAdFailedToLoad : Error : $p0")
                }
            }
            val purchaseHelper =
                SpeedMeterBillingHelper(
                    myApplication
                )
            if (purchaseHelper.shouldShowAds()) {
                try {
                    AppOpenAd.load(
                        myApplication,
                        SpeedMeterLoadAds.app_open_ad_id_admob,
                        AdRequest.Builder().build(),
                        callBackLoading!!
                    )
                } catch (e: Exception) {
                }
            }
            Log.d(TAG, "loadStartAppOpenAd: Purchased")
        }
    }

    private fun callForLoadingTime(hours: Long): Boolean {
        val difference = Date().time - adLoadingTime
        val numMilliSecondsPerHour: Long = 3600000
        return difference < numMilliSecondsPerHour * hours
    }

    val isStartAdAvailable: Boolean
        get() = startOpenAd != null && callForLoadingTime(4)

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
    }

    override fun onActivityStarted(activity: Activity) {
        runningActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        runningActivity = activity
    }

    override fun onActivityDestroyed(activity: Activity) {
        runningActivity = null
    }

    fun clearAllOpenAd() {
        Log.d(TAG, "clearAllOpenAd: ")
        startOpenAd = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        Log.d(TAG, "OnLifecycleEvent onStart")
        if (SpeedMeterLoadAds.canShowAppOpen && runningActivity !is SplashActivity) {
            Log.d(TAG, "onStart: Showing App open Resume AD")
            showHoneyBeeMapNavigationStartAppOpenAd()
        } else {
            loadHoneyBeeMapNavigationStartAppOpenAd()
        }
    }

}