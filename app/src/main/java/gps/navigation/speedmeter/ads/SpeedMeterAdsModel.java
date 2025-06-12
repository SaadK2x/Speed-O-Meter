package gps.navigation.speedmeter.ads;

import androidx.annotation.Keep;

@Keep
public class SpeedMeterAdsModel {
    private String appid_admob_inApp;
    private String banner_admob_inApp;
    private String interstitial_admob_inApp;
    private String native_admob_inApp;
    private String app_open_admob_inApp;
    private String app_open_splash_ad_id_admob;


    public SpeedMeterAdsModel() {
    }

    public SpeedMeterAdsModel(String appid_admob_inApp, String banner_admob_inApp, String interstitial_admob_inApp, String native_admob_inApp, String app_open_admob_inApp, String app_open_splash_ad_id_admob) {
        this.appid_admob_inApp = appid_admob_inApp;
        this.banner_admob_inApp = banner_admob_inApp;
        this.interstitial_admob_inApp = interstitial_admob_inApp;
        this.native_admob_inApp = native_admob_inApp;
        this.app_open_admob_inApp = app_open_admob_inApp;
        this.app_open_splash_ad_id_admob = app_open_splash_ad_id_admob;
    }

    public String getAppid_admob_inApp() {
        return appid_admob_inApp;
    }

    public void setAppid_admob_inApp(String appid_admob_inApp) {
        this.appid_admob_inApp = appid_admob_inApp;
    }

    public String getBanner_admob_inApp() {
        return banner_admob_inApp;
    }

    public void setBanner_admob_inApp(String banner_admob_inApp) {
        this.banner_admob_inApp = banner_admob_inApp;
    }

    public String getInterstitial_admob_inApp() {
        return interstitial_admob_inApp;
    }

    public void setInterstitial_admob_inApp(String interstitial_admob_inApp) {
        this.interstitial_admob_inApp = interstitial_admob_inApp;
    }

    public String getNative_admob_inApp() {
        return native_admob_inApp;
    }

    public void setNative_admob_inApp(String native_admob_inApp) {
        this.native_admob_inApp = native_admob_inApp;
    }

    public String getApp_open_admob_inApp() {
        return app_open_admob_inApp;
    }

    public void setApp_open_admob_inApp(String app_open_admob_inApp) {
        this.app_open_admob_inApp = app_open_admob_inApp;
    }

    public String getApp_open_splash_ad_id_admob() {
        return app_open_splash_ad_id_admob;
    }

    public void setApp_open_splash_ad_id_admob(String app_open_splash_ad_id_admob) {
        this.app_open_splash_ad_id_admob = app_open_splash_ad_id_admob;
    }
}
