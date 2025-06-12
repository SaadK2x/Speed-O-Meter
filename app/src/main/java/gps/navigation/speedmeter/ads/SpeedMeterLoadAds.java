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
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import gps.navigation.speedmeter.BuildConfig;
import gps.navigation.speedmeter.R;
import gps.navigation.speedmeter.utils.MyApp;

public class SpeedMeterLoadAds {
    public static String appid_admob_inApp = MyApp.Companion.getStr(R.string.admob_ad_id);
    public static String interstitial_admob_inApp = BuildConfig.admob_interstitial_id;
    public static String interstitial_facebook = BuildConfig.facebook_interstitial;
    public static String interstitial_admob_inApp_main = BuildConfig.admob_interstitial_id_main;
    public static String interstitial_video = BuildConfig.video_interstitial;

    public static String banner_admob_inApp = BuildConfig.admob_banner_id;
    public static String banner_facebook = BuildConfig.facebook_banner;
    public static String banner_medium_admob_inApp = BuildConfig.medium_admob_banner_id;
    public static String intro_banner_medium_admob_inApp = BuildConfig.admob_medium_banner_intro_id;
    public static String rewarded_ad = BuildConfig.rewarded;
    public static String app_open_ad_id_admob = BuildConfig.app_open_ad_id_admob;
    public static String app_open_splash_ad_id_admob = BuildConfig.app_open_ad_id_admob_splash;

    public static String native_admob_inApp = BuildConfig.native_small;
    public static String native_facebook = BuildConfig.facebook_native;
    public static String intro_native_admob_inApp = BuildConfig.intro_admob_native_id;


    public static boolean shouldShowAdmob = true;

    public static boolean shouldShowAds = false;
    public static boolean isAdsShow = true;

    public static boolean canShowAppOpen = true;

    public static double ad_click_value_key_var = 1;
    public static double ad_impression_value_key_var = 6;
    public static float percentage;
    public static boolean haveGotSnapshot = false;
    public static boolean should_show_allfb_ads = true;

    public static double current_counter = 19;

    /*new 13/1/2022*/
    public static double splash_counter = 1;
    public static double splash_threshold = 2;


    public static boolean ctr_control = false;
    public static boolean show_splash_ad = true;
    public static long next_ads_time = 12000;

    public static boolean shouldGoForAds = true;

    public static InterstitialAd admobInterstitialAdHoneyBee;
    public static InterstitialAd admobInterstitialAdMain;
    public static InterstitialAd videoInterstitial;
    public static InterstitialAd admobInterstitialAdHoneyBeeSplash;
    public static boolean canReLoadedAdMob = false;
    public static boolean canReLoadedHighAdMob = false;

    public static int adClickCounter = 0;
    public static int adShowAfter = 5;
    public static int user_count = 0;
    public static RewardedAd rewardedAd;
    private static final Handler myHandler = new Handler();


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

    public static void loadAdMobMediumBannerAd(Context context, final LinearLayout adContainer, final View mView) {

        SpeedMeterBillingHelper billingHelper = new SpeedMeterBillingHelper(context);
        if (billingHelper.shouldShowAds()) {
            AdView mAdView = new AdView(context);
            mAdView.setAdUnitId(SpeedMeterLoadAds.banner_medium_admob_inApp);
            mAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
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
                    mView.setVisibility(View.GONE);
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

    public static void loadAdMobIntroMediumBannerAd(Context context, final LinearLayout adContainer, final View mView) {
        SpeedMeterBillingHelper billingHelper = new SpeedMeterBillingHelper(context);
        if (billingHelper.shouldShowAds()) {
            AdView mAdView = new AdView(context);
            mAdView.setAdUnitId(SpeedMeterLoadAds.intro_banner_medium_admob_inApp);
            mAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
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
    public static void preLoadAdsHoneyBee(final Context context) {
        SpeedMeterBillingHelper billingHelper = new SpeedMeterBillingHelper(context);
        if (billingHelper.shouldShowAds()) {
            //admobeload
            if (admobInterstitialAdMain == null) {
                canReLoadedAdMob = false;
                InterstitialAd.load(context, SpeedMeterLoadAds.interstitial_admob_inApp_main, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        Log.d("ConstantAdsLoadAds", "Admob loaded");
                        canReLoadedAdMob = true;
                        admobInterstitialAdMain = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.d("ConstantAdsLoadAds", "Admob Faild: " + loadAdError.toString());
                        canReLoadedAdMob = true;
                        admobInterstitialAdMain = null;
                    }
                });
            } else {
                Log.d("ConstantAdsLoadAds", "admobe AlReady loaded");
            }

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


    //all
    public static void preReLoadAdsHoneyBee(final Context context) {
        SpeedMeterBillingHelper billingHelper = new SpeedMeterBillingHelper(context);
        if (billingHelper.shouldShowAds()) {
            //admobeload
            if (shouldShowAdmob) {
                if (admobInterstitialAdMain != null) {
                    Log.d("ConstantAdsLoadAds", "admobe ReAlReady loaded");
                } else {
                    Log.d("ConstantAdsLoadAds", "canReLoadedAdMob " + canReLoadedAdMob);
                    if (canReLoadedAdMob) {
                        canReLoadedAdMob = false;

                        InterstitialAd.load(context, SpeedMeterLoadAds.interstitial_admob_inApp_main, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                super.onAdLoaded(interstitialAd);
                                Log.d("ConstantAdsLoadAds", "Admob Reloaded");
                                canReLoadedAdMob = true;
                                admobInterstitialAdMain = interstitialAd;
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                super.onAdFailedToLoad(loadAdError);
                                Log.d("ConstantAdsLoadAds", "Admob ReFaild: " + loadAdError.toString());
                                canReLoadedAdMob = true;
                                admobInterstitialAdMain = null;
                            }
                        });

                    } else {

                        Log.d("ConstantAdsLoadAds", "Admob last ad request is in pending");
                    }
                }
            }
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


    public static void loadRewardedAd(Context context, RewardedCallback callbacks) {
        Log.d("ConstantAdsLoadAds", "Request for Rewarded Ad");
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(context, rewarded_ad,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d("ConstantAdsLoadAds", loadAdError.toString());
                        rewardedAd = null;
                        callbacks.onAdFailed();
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        callbacks.onAdLoad();
                        Log.d("ConstantAdsLoadAds", "Ad was loaded.");
                    }
                });

    }

    public interface RewardedCallback {
        void onAdLoad();

        void onAdFailed();
    }

}
