package gps.navigation.speedmeter.ads


import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import gps.navigation.speedmeter.utils.MyApp
import java.util.Date

class SpeedMeterAppOpenSplashAd(private val myApplication: MyApp) :
    LifecycleObserver,
    Application.ActivityLifecycleCallbacks {
    private val TAG = "StartAdLogs: Splashzzzz"

    private var callBackLoading: AppOpenAd.AppOpenAdLoadCallback? = null
    private var adLoadingTime: Long = 0

    companion object {
        public var startOpenAd: AppOpenAd? = null
        private var isDisplayingAd = false
    }

    init {
        myApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun showStreetViewTruckCTStartAppOpenAd(
        activity: Activity,
        callBack: SplashShowAppOpenCallback
    ) {
        if (!isDisplayingAd && isStartAdAvailable) {
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        startOpenAd = null
                        isDisplayingAd = false
                        /* if (StreetViewTruckCTLoadAds.should_show_app_open) {
                             //loadStreetViewTruckCTStartAppOpenAd()
                         }*/
                        SpeedMeterShowAds.logAnalyticsForClicks(
                            "SplashAdDismissedFullScreenContent",
                            myApplication
                        )
                        callBack.onAdDismissedFullScreenContent()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        callBack.onAdFailedToShowFullScreenContent()
                    }

                    override fun onAdShowedFullScreenContent() {
                        SpeedMeterShowAds.logAnalyticsForClicks(
                            "SplashAdShowedFullScreenContent",
                            myApplication
                        )
                        isDisplayingAd = true
                    }
                }
            startOpenAd!!.show(activity)
            Log.d(TAG, "showStartAppOpenAd: Showing App open ad")
            startOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
        } else {
            Log.d(TAG, "Can not show ad.")
            /*if (StreetViewTruckCTLoadAds.should_show_app_open) {
                //loadStreetViewTruckCTStartAppOpenAd()
            }*/
            callBack.onAdFailedToShowFullScreenContent()
        }

    }

    fun loadStreetViewTruckCTStartAppOpenAd(callBack: SplashCallback) {
        if (isStartAdAvailable) {
            // callBack.onAdCallBack()
            Log.d(TAG, "Ad is available App Open Ad ")
        } else {
            Log.d(TAG, "Ad requesting App Open Ad ")
            callBackLoading = object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(p0: AppOpenAd) {
                    super.onAdLoaded(p0)
                    Log.d(TAG, "onAppOpenAdLoaded")
                    SpeedMeterShowAds.logAnalyticsForClicks(
                        "SplashAdLoaded",
                        myApplication
                    )
                    startOpenAd = p0
                    adLoadingTime = Date().time

                    callBack.onAdLoad()
                    // callBack.onAdCallBack()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    Log.d(TAG, "onAppOpenAdFailedToLoad : Error : $p0")
                    SpeedMeterShowAds.logAnalyticsForClicks(
                        "SplashAdFailedToLoad",
                        myApplication
                    )

                    callBack.onAdFailed()
                    // callBack.onAdCallBack()
                }
            }
            val billingHelper =
                SpeedMeterBillingHelper(
                    myApplication
                )
            if (billingHelper.shouldShowAds()) {
                Log.d(TAG, "loadStartAppOpenAd: Not Purchased")
                try {
                    AppOpenAd.load(
                        myApplication,
                        SpeedMeterLoadAds.app_open_splash_ad_id_admob,
                        AdRequest.Builder().build(),
                        callBackLoading!!
                    )
                } catch (e: Exception) {
                }
            } else {
                // callBack.onAdCallBack()
            }

        }
    }

    interface SplashCallback {
        fun onAdLoad()

        fun onAdFailed()
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

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    fun clearAllOpenAd() {
        Log.d(TAG, "clearAllOpenAd: ")
        startOpenAd = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        Log.d(TAG, "OnLifecycleEvent onStart")
        Log.d(TAG, "onStart: Showing App open Resume AD")
        //showStreetViewTruckCTStartAppOpenAd()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        Log.d(TAG, "OnLifecycleEvent onStop")
    }

    interface SplashShowAppOpenCallback {
        fun onAdDismissedFullScreenContent()
        fun onAdFailedToShowFullScreenContent()
    }

    interface SplashLoadAppOpenCallback {
        fun onAdCallBack()
    }
}