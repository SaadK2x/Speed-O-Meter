package gps.navigation.speedmeter.ads;


import static gps.navigation.speedmeter.ads.SpeedMeterLoadAds.preReLoadAdsLiveEarth;
import static gps.navigation.speedmeter.ads.SpeedMeterLoadAds.setHandlerForAd;
import static gps.navigation.speedmeter.ads.SpeedMeterLoadAds.shouldGoForAds;
import static gps.navigation.speedmeter.ads.SpeedMeterLoadAds.splashInterstitial;
import static gps.navigation.speedmeter.ads.SpeedMeterLoadAds.videoInterstitial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import gps.navigation.speedmeter.utils.Constants;

public class SpeedMeterShowAds {

    public static void mediationBackPressedSimpleHoneyBeeMapNavigation(final Activity context, final InterstitialAd mInterstitialAd) {

        if (Constants.INSTANCE.getIsAppOnTimer()) {
            backPressForTimer(context, mInterstitialAd);
        } else {
            backPressForClick(context, mInterstitialAd);
        }
    }

    public static void backPressForClick(final Activity context, final InterstitialAd mInterstitialAd) {
        if (adShowCounter()) {

            if (mInterstitialAd != null) {
                mInterstitialAd.show(context);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        SpeedMeterLoadAds.canShowAppOpen = true;
                        SpeedMeterLoadAds.admobInterstitialNav = null;
                        SpeedMeterLoadAds.adClickCounter = 0;
                        preReLoadAdsLiveEarth(context);
                        context.finish();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        SpeedMeterLoadAds.canShowAppOpen = false;
                    }

                });

            } else {
                preReLoadAdsLiveEarth(context);
                context.finish();
            }
        } else {
            preReLoadAdsLiveEarth(context);
            context.finish();
        }
    }

    public static void backPressForTimer(final Activity context, final InterstitialAd mInterstitialAd) {
        if (shouldGoForAds) {

            if (mInterstitialAd != null) {
                mInterstitialAd.show(context);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        SpeedMeterLoadAds.canShowAppOpen = true;
                        SpeedMeterLoadAds.admobInterstitialNav = null;
                        preReLoadAdsLiveEarth(context);
                        setHandlerForAd();
                        context.finish();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        SpeedMeterLoadAds.canShowAppOpen = false;
                    }

                });

            } else {
                preReLoadAdsLiveEarth(context);
                context.finish();
            }
        } else {
            preReLoadAdsLiveEarth(context);
            context.finish();
        }
    }

    public static void directAdsSpecificModulesSquareXNavigation(final Context context, final InterstitialAd mInterstitialAd, final Intent intent) {
        if (Constants.INSTANCE.getIsAppOnTimer()) {
            forTimerAds(context, mInterstitialAd, intent);
        } else {
            forClickAds(context, mInterstitialAd, intent);
        }
    }

    public static void forClickAds(final Context context, final InterstitialAd mInterstitialAd, final Intent intent) {
        if (adShowCounter()) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show((Activity) context);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        SpeedMeterLoadAds.canShowAppOpen = true;
                        SpeedMeterLoadAds.admobInterstitialNav = null;
                        SpeedMeterLoadAds.adClickCounter = 0;
                        preReLoadAdsLiveEarth(context);
                        //  HoneyBeeMapNavigationLoadAds.setHandlerForAd();
                        Log.d("ConstantAdsLoadAds", "onAdDismissedFullScreenContent: " + SpeedMeterLoadAds.next_ads_time);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        SpeedMeterLoadAds.canShowAppOpen = false;
                    }
                });
            } else {
                preReLoadAdsLiveEarth(context);
                context.startActivity(intent);
            }
        } else {
            preReLoadAdsLiveEarth(context);
            context.startActivity(intent);
        }
    }

    public static void forTimerAds(final Context context, final InterstitialAd mInterstitialAd, final Intent intent) {
        if (shouldGoForAds) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show((Activity) context);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        SpeedMeterLoadAds.canShowAppOpen = true;
                        SpeedMeterLoadAds.admobInterstitialNav = null;

                        preReLoadAdsLiveEarth(context);
                        setHandlerForAd();
                        Log.d("ConstantAdsLoadAds", "onAdDismissedFullScreenContent: " + SpeedMeterLoadAds.next_ads_time);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        SpeedMeterLoadAds.canShowAppOpen = false;
                    }
                });
            } else {
                preReLoadAdsLiveEarth(context);
                context.startActivity(intent);
            }
        } else {
            preReLoadAdsLiveEarth(context);
            context.startActivity(intent);
        }
    }


    static String TAG = "AdLogger:";

    public static void logAnalyticsForClicks(String value, Context context) {
        FirebaseAnalytics firebaseAnalytics;
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        try {
            firebaseAnalytics.logEvent(value, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void navigateToDynamicFeature(Context context) {
        Intent intent = new Intent();
        intent.setClassName(
                "com.earth.map.street.view.live.gps.navigation",
                "com.example.dynamicfeature.GlobeMapActivity"
        );
        context.startActivity(intent);
    }

    public static Boolean adShowCounter() {
        SpeedMeterLoadAds.adClickCounter++;
        Log.d("adShowCounter", " " + SpeedMeterLoadAds.adClickCounter);
        if (SpeedMeterLoadAds.adClickCounter >= SpeedMeterLoadAds.adShowAfter) {
            Log.d("adShowCounter", "two:   >  show ");
            SpeedMeterLoadAds.adClickCounter = 0;
            return true;
        } else {
            Log.d("adShowCounter", " three  else  No");
            return false;
        }
    }

    public static void showingVideoAd(final Activity context, OnAdShowed callback) {
        if (videoInterstitial != null) {
            videoInterstitial.show(context);
            videoInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    SpeedMeterLoadAds.canShowAppOpen = true;
                    SpeedMeterLoadAds.canReLoadedAdMob = true;
                    videoInterstitial = null;
                    callback.onDismiss();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    SpeedMeterLoadAds.canShowAppOpen = false;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    SpeedMeterLoadAds.canShowAppOpen = true;
                    callback.onDismiss();

                }
            });
        } else {
            SpeedMeterLoadAds.canShowAppOpen = true;
            callback.onDismiss();
        }

    }

    public static void showingSplashAd(final Activity context, OnAdShowed callback) {
        if (splashInterstitial != null) {
            splashInterstitial.show(context);
            splashInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    SpeedMeterLoadAds.canShowAppOpen = true;
                    SpeedMeterLoadAds.canReLoadedAdMob = true;
                    splashInterstitial = null;
                    callback.onDismiss();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    SpeedMeterLoadAds.canShowAppOpen = false;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    SpeedMeterLoadAds.canShowAppOpen = true;
                    callback.onDismiss();

                }
            });
        } else {
            SpeedMeterLoadAds.canShowAppOpen = true;
            callback.onDismiss();
        }

    }

    public interface OnAdShowed {
        void onDismiss();
    }

    public static interface AdDismissCallback {
        void onAdDismissed();
    }
}




