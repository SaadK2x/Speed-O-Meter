package gps.navigation.speedmeter.ads;


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

public class SpeedMeterShowAds {

    public static void mediationBackPressedSimpleHoneyBeeMapNavigation(final Activity context, final InterstitialAd mInterstitialAd) {

        if (adShowCounter(context)) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(context);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        SpeedMeterLoadAds.canShowAppOpen = true;
                        SpeedMeterLoadAds.admobInterstitialAdHoneyBee = null;
                        SpeedMeterLoadAds.adClickCounter = 0;
                        //  HoneyBeeMapNavigationLoadAds.preReLoadAdsHoneyBee(context);
                        context.finish();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        SpeedMeterLoadAds.canShowAppOpen = false;
                    }

                });

            } else {
                context.finish();
            }
        } else {
            context.finish();
        }
    }

    public static void backpressSpecificActivity(final Activity context, final InterstitialAd mInterstitialAd) {
        if (adShowCounter(context)) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(context);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        SpeedMeterLoadAds.canShowAppOpen = true;
                        SpeedMeterLoadAds.admobInterstitialAdHoneyBee = null;
                        SpeedMeterLoadAds.adClickCounter = 0;
                        //  HoneyBeeMapNavigationLoadAds.preReLoadAdsHoneyBee(context);
                        context.finish();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        SpeedMeterLoadAds.canShowAppOpen = false;
                    }

                });

            } else {
                context.finish();
            }
        } else {
            context.finish();
        }
    }

    public static void directAdsSpecificModulesSquareXNavigation(final Context context, final InterstitialAd mInterstitialAd, final Intent intent) {
        if (adShowCounter(context)) {
            Log.d("ConstantAdsLoadAds", "showing");
            if (mInterstitialAd != null) {
                Log.d("ConstantAdsLoadAds", "not null");
                mInterstitialAd.show((Activity) context);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        SpeedMeterLoadAds.canShowAppOpen = true;
                        SpeedMeterLoadAds.admobInterstitialAdMain = null;
                        SpeedMeterLoadAds.adClickCounter = 1;
                        // HoneyBeeMapNavigationLoadAds.preReLoadAdsHoneyBee(context);
                        //  HoneyBeeMapNavigationLoadAds.setHandlerForAd();
                        // preReLoadAdsHoneyBee(context);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        SpeedMeterLoadAds.canShowAppOpen = false;
                    }
                });
            } else {
                Log.d("ConstantAdsLoadAds", "ctr is not true");
                //preReLoadAdsHoneyBee(context);
                context.startActivity(intent);
            }


        } else {
            Log.d("ConstantAdsLoadAds", "ctr is not true");
            // preReLoadAdsHoneyBee(context);
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

    public static Boolean adShowCounter(Context context) {

        SpeedMeterLoadAds.adClickCounter++;
        Log.d("adShowCounter", " " + SpeedMeterLoadAds.adClickCounter);
      /*  if (AiPhotoLoadAds.adClickCounter == 2) {
            Log.d("adShowCounter", "going to load");
            AiPhotoLoadAds.preReLoadAdsHoneyBee(context);
            return false;
        }*/
        if (SpeedMeterLoadAds.adClickCounter == 1) {
            Log.d("adShowCounter", "one:  1  show");
            Log.d("adShowCounter", " " + SpeedMeterLoadAds.adClickCounter);
            return true;

        } else if (SpeedMeterLoadAds.adClickCounter > SpeedMeterLoadAds.adShowAfter) {
            SpeedMeterLoadAds.adClickCounter = 1;
            Log.d("adShowCounter", "two:   >  show ");
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




