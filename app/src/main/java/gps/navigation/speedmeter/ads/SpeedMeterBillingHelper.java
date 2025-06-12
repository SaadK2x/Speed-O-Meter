package gps.navigation.speedmeter.ads;

import android.content.Context;
import android.content.SharedPreferences;

public class SpeedMeterBillingHelper {
    private SharedPreferences billingPreferences;

    public SpeedMeterBillingHelper(Context content) {
        billingPreferences = content.getSharedPreferences("PurchasePrefs", Context.MODE_PRIVATE);
    }

    public boolean shouldShowAds() {
        //return !(billingPreferences.getBoolean("ads_purchase", false));
        return !((billingPreferences.getBoolean("ads_purchase", false)) ||
                (billingPreferences.getBoolean("monthly_1", false)) ||
                (billingPreferences.getBoolean("weekly_7", false)) ||
                (billingPreferences.getBoolean("yearly_01", false)));
    }

    public boolean shouldShowAIStreetAds() {
        return (billingPreferences.getBoolean("a1_monthly", false) || (billingPreferences.getBoolean("a1_weekly", false)));
    }
}
