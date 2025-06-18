package gps.navigation.speedmeter.ads;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gps.navigation.speedmeter.BuildConfig;
import gps.navigation.speedmeter.R;
import gps.navigation.speedmeter.utils.MyApp;

public class SpeedMeterLoadAds {
    public static String appid_admob_inApp = MyApp.Companion.getStr(R.string.admob_ad_id);
    public static String interstitial_admob_inApp = BuildConfig.admob_interstitial_id;
    public static String banner_admob_inApp = BuildConfig.admob_banner_id;
    public static String app_open_ad_id_admob = BuildConfig.app_open_ad_id_admob;
    public static String app_open_splash_ad_id_admob = BuildConfig.app_open_ad_id_admob_splash;
    public static String admob_interstitial_splash = BuildConfig.admob_interstitial_splash;


    public static boolean shouldShowAdmob = true;

    public static boolean canShowAppOpen = true;
    public static boolean haveGotSnapshot = false;
    public static long next_ads_time = 12000;

    public static boolean shouldGoForAds = true;

    public static InterstitialAd admobInterstitialAdHoneyBee;
    public static InterstitialAd admobInterstitialAdMain;
    public static InterstitialAd videoInterstitial;
    public static InterstitialAd splashInterstitial;
    public static boolean canReLoadedAdMob = false;

    public static int adClickCounter = 0;
    public static int adShowAfter = 5;
    public static int user_count = 0;
    public static RewardedAd rewardedAd;
    private static final Handler myHandler = new Handler();
    private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SpeedOMeter");

    public static void setHandlerForAd() {
        shouldGoForAds = false;
        Log.d("ConstantAdsLoadAds", "shouldGoForAds onTimeStart: " + shouldGoForAds);
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                shouldGoForAds = true;
                Log.d("ConstantAdsLoadAds", "shouldGoForAds onTimeComplete: " + shouldGoForAds);
            }
        }, next_ads_time);
    }

    private static void loadHoneyBeeMapNavigationAdMobBanner(final LinearLayout adContainer, final View mView, final Context context, TextView tv) {
        SpeedMeterBillingHelper billingHelper = new SpeedMeterBillingHelper(context);
        if (billingHelper.shouldShowAds()) {

            if (shouldShowAdmob) {
                AdView mAdView = new AdView(context);
                mAdView.setAdUnitId(banner_admob_inApp);
                mAdView.setAdSize(getAdSize(((Activity) context)));
                mAdView.setAdListener(new com.google.android.gms.ads.AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        tv.setVisibility(View.GONE);
                        Log.d("ConstantAdsLoadAds", "Bannen onAdLoaded: ");
                        try {
                            adContainer.removeAllViews();
                            adContainer.addView(mAdView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.d("ConstantAdsLoadAds", "Bannen onAdFailedToLoad: " + loadAdError.toString());
                        mAdView.destroy();
                        mView.setVisibility(View.GONE);
                    }

                });
                try {
                    mAdView.loadAd(new AdRequest.Builder().build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            mView.setVisibility(View.GONE);
        }
    }

    public static void loadHoneyBeeMapNavigationAdMobBannerSpecific(final LinearLayout adContainer, final View mView, final Context context) {
        SpeedMeterBillingHelper billingHelper = new SpeedMeterBillingHelper(context);

        if (billingHelper.shouldShowAds()) {
            AdView mAdView = new AdView(context);
            mAdView.setAdUnitId(banner_admob_inApp);
            mAdView.setAdSize(getAdSize(((Activity) context)));
            mAdView.setAdListener(new com.google.android.gms.ads.AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.d("ConstantAdsLoadAds", "Bannen onAdLoaded: ");
                    try {
                        adContainer.removeAllViews();
                        adContainer.addView(mAdView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.d("ConstantAdsLoadAds", "Bannen onAdFailedToLoad: " + loadAdError.toString());
                    mAdView.destroy();
                }

            });
            try {
                mAdView.loadAd(new AdRequest.Builder().build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mView.setVisibility(View.GONE);
        }
    }

    public static AdSize getAdSize(Activity context) {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }


    public static void loadBanner(final LinearLayout adContainer, final View mView, final Context context) {

        TextView tv = adContainer.findViewById(R.id.adTV);
        SpeedMeterBillingHelper billingHelper = new SpeedMeterBillingHelper(context);
        if (billingHelper.shouldShowAds()) {
            SpeedMeterLoadAds.loadHoneyBeeMapNavigationAdMobBanner(adContainer, mView, context, tv);
            //loadGlobeMapBannerMax(adContainer, mView,context);
        } else {
            mView.setVisibility(View.GONE);
        }


    }

    //1
    public static void loadVideoAdForPrompt(final Context context, OnVideoLoad videoCallback) {
        SpeedMeterBillingHelper billingHelper = new SpeedMeterBillingHelper(context);
        if (billingHelper.shouldShowAds()) {
            //admobeload
            if (shouldShowAdmob) {
                if (videoInterstitial == null) {
                    canReLoadedAdMob = false;
                    InterstitialAd.load(context, SpeedMeterLoadAds.interstitial_admob_inApp, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            super.onAdLoaded(interstitialAd);
                            Log.d("ConstantAdsLoadAds", "Admob loaded");
                            videoInterstitial = interstitialAd;
                            videoCallback.onLoaded();
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            Log.d("ConstantAdsLoadAds", "Admob Faild: " + loadAdError.toString());
                            videoInterstitial = null;
                            videoCallback.onFailed();
                        }
                    });
                } else {
                    Log.d("ConstantAdsLoadAds", "admobe AlReady loaded");
                    videoCallback.onLoaded();
                }
            } else {
                videoCallback.onFailed();
            }

        }
    }

    public interface OnVideoLoad {
        void onLoaded();

        void onFailed();
    }

    //1
    public static void loadSplashAd(final Context context, OnVideoLoad videoCallback) {
        SpeedMeterBillingHelper billingHelper = new SpeedMeterBillingHelper(context);
        if (billingHelper.shouldShowAds()) {
            if (splashInterstitial == null) {
                InterstitialAd.load(context, SpeedMeterLoadAds.admob_interstitial_splash, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        Log.d("ConstantAdsLoadAds", "Admob loaded");
                        splashInterstitial = interstitialAd;
                        videoCallback.onLoaded();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.d("ConstantAdsLoadAds", "Admob Faild: " + loadAdError.toString());
                        splashInterstitial = null;
                        videoCallback.onFailed();
                    }
                });
            } else {
                Log.d("ConstantAdsLoadAds", "admobe AlReady loaded");
                videoCallback.onLoaded();
            }

        } else {
            videoCallback.onFailed();
        }
    }


    public static void preReLoadAdsLiveEarth(final Context context) {


        SpeedMeterBillingHelper billingHelper = new SpeedMeterBillingHelper(context);
        if (billingHelper.shouldShowAds()) {


            //admobeload
            if (admobInterstitialAdHoneyBee != null) {
                Log.d("ConstantAdsLoadAds", "admobe ReAlReady loaded");
            } else {
                Log.d("ConstantAdsLoadAds", "canReLoadedAdMob " + canReLoadedAdMob);

                InterstitialAd.load(context, SpeedMeterLoadAds.interstitial_admob_inApp, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        Log.d("ConstantAdsLoadAds", "Admob Reloaded");
                        canReLoadedAdMob = true;
                        admobInterstitialAdHoneyBee = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.d("ConstantAdsLoadAds", "Admob ReFaild: " + loadAdError.toString());
                        canReLoadedAdMob = true;
                        admobInterstitialAdHoneyBee = null;
                    }
                });

            }
        }
    }

}
