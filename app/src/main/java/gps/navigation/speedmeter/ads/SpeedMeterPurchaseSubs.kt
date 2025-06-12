package gps.navigation.speedmeter.ads

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResponseListener
import gps.navigation.speedmeter.utils.Constants
import org.json.JSONObject

class SpeedMeterPurchaseSubs(private val activityContext: Context) :
    PurchasesUpdatedListener {

    private lateinit var googleBillingHoneyBeeMapNavigationClient: BillingClient
    private val TAG = "BillingLoggerAI:"
    private val listAvailHoneyBeeMapNavigationPurchases = ArrayList<SkuDetails>()
    private lateinit var billingHoneyBeeMapNavigationPreferences: SharedPreferences
    private val listAvailEarthLiveSubsMapPurchases = ArrayList<SkuDetails>()

    init {
        initMyBillingClientHoneyBeeMapNavigation()
    }

    private fun initMyBillingClientHoneyBeeMapNavigation() {
        billingHoneyBeeMapNavigationPreferences =
            activityContext.getSharedPreferences("PurchasePrefs", Context.MODE_PRIVATE)
        googleBillingHoneyBeeMapNavigationClient = BillingClient
            .newBuilder(activityContext)
            .enablePendingPurchases()
            .setListener(this)
            .build()
        googleBillingHoneyBeeMapNavigationClient.startConnection(object :
            BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Google Billing is Connected")

                    fetchGlobeMapAllSubFromConsole()
                    fetchGlobeMapPurchasedSubFromConsole()
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d(TAG, "Google Billing is  Disconnected")
            }
        })
    }


    fun extractPriceAmountMicros(jsonString: String): String? {
        try {
            val jsonObject = JSONObject(jsonString)
            return jsonObject.getString("price")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun purchaseAIStreetViewWeeklyPackage() {
        Log.d(TAG, "Going to purchase ads_purchase")
        if (listAvailEarthLiveSubsMapPurchases.size > 0) {
            try {
                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(listAvailEarthLiveSubsMapPurchases[1])
                    .build()
                val responseCode =
                    googleBillingHoneyBeeMapNavigationClient.launchBillingFlow(
                        activityContext as Activity,
                        flowParams
                    ).responseCode
                Log.d(TAG, "Google Billing Response : $responseCode")
            } catch (e: Exception) {
            }
        } else {
            Log.d(TAG, "Nothing to purchase for google billing")
        }
    }

    fun purchaseAIStreetViewMonthlyPackage() {
        Log.d(TAG, "Going to purchase ads_purchase")
        if (listAvailEarthLiveSubsMapPurchases.size > 0) {
            try {
                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(listAvailEarthLiveSubsMapPurchases[0])
                    .build()
                val responseCode =
                    googleBillingHoneyBeeMapNavigationClient.launchBillingFlow(
                        activityContext as Activity,
                        flowParams
                    ).responseCode
                Log.d(TAG, "Google Billing Response : $responseCode")
            } catch (e: Exception) {
            }
        } else {
            Log.d(TAG, "Nothing to purchase for google billing")
        }
    }

    fun purchaseAIStreetViewYearlyPackage() {
        Log.d(TAG, "Going to purchase ads_purchase")
        if (listAvailEarthLiveSubsMapPurchases.size > 0) {
            try {
                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(listAvailEarthLiveSubsMapPurchases[2])
                    .build()
                val responseCode =
                    googleBillingHoneyBeeMapNavigationClient.launchBillingFlow(
                        activityContext as Activity,
                        flowParams
                    ).responseCode
                Log.d(TAG, "Google Billing Response : $responseCode")
            } catch (e: Exception) {
            }
        } else {
            Log.d(TAG, "Nothing to purchase for google billing")
        }
    }

    fun purchaseHoneyBeeMapNavigationAdsPackage() {
        Log.d(TAG, "Going to purchase ads_purchase")
        if (listAvailHoneyBeeMapNavigationPurchases.size > 0) {
            try {
                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(listAvailHoneyBeeMapNavigationPurchases[0])
                    .build()
                val responseCode =
                    googleBillingHoneyBeeMapNavigationClient.launchBillingFlow(
                        activityContext as Activity,
                        flowParams
                    ).responseCode
                Log.d(TAG, "Google Billing Response : $responseCode")
            } catch (e: Exception) {
            }
        } else {
            Log.d(TAG, "Nothing to purchase for google billing")
        }
    }

    private fun fetchGlobeMapAllSubFromConsole() {
        val skuListToQuery = ArrayList<String>()
        skuListToQuery.add("monthly_1")
        skuListToQuery.add("weekly_7")
        skuListToQuery.add("yearly_01")


        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuListToQuery).setType(BillingClient.SkuType.SUBS)
        googleBillingHoneyBeeMapNavigationClient.querySkuDetailsAsync(
            params.build(),
            object : SkuDetailsResponseListener {
                override fun onSkuDetailsResponse(
                    result: BillingResult,
                    skuDetails: MutableList<SkuDetails>?,
                ) {
                    //Log.i(TAG, "onSkuResponse ${result?.responseCode}")
                    if (skuDetails != null) {
                        var i = 0
                        for (skuDetail2 in skuDetails) {

                            listAvailEarthLiveSubsMapPurchases.add(skuDetail2)
                            Log.i(TAG, skuDetail2.toString())

                            val helloWorld = skuDetail2.toString()
                            val hellWrld = helloWorld.replace("SkuDetails: ", "")
                            val hellWrld1 = hellWrld.replace("[", "")
                            val hellWrld2 = hellWrld1.replace("]", "")

                            Log.i(TAG + "abcd2", hellWrld2.toString())
                            try {
                                val priceAmountMicros = extractPriceAmountMicros(hellWrld2)
                                println("Price Amount Micros: $priceAmountMicros")
                                if (i == 1) {
                                    Constants.billingWeekly = priceAmountMicros!!
                                    Log.d(TAG, "billingAiWeekly: " + priceAmountMicros.toString())

                                } else if (i == 0) {
                                    Constants.billingMonthly = priceAmountMicros!!
                                    Log.d(TAG, "billingAiMonthly: " + priceAmountMicros.toString())

                                } else {
                                    Constants.billingYearly = priceAmountMicros!!
                                    Log.d(TAG, "billingAiYearly: " + priceAmountMicros.toString())
                                }


                            } catch (ex: Exception) {

                            }
                            i++
                        }
                    } else {
                        Log.i(TAG, "No skus for this application")
                    }
                }

            })
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?,
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                Log.d(TAG, "onPurchases Successfully Purchased : " + purchase.skus[0])
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "Google Billing Cancelled")
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Log.d(TAG, "Google Billing Purchased Already")
            Toast.makeText(
                activityContext,
                "You have already purchased this item",
                Toast.LENGTH_LONG
            )
                .show()
        } else {
            Log.d(TAG, "Google billing other error " + billingResult.responseCode)
        }
    }

    private val purchaseAcknowledgedListener: AcknowledgePurchaseResponseListener = object
        : AcknowledgePurchaseResponseListener {
        override fun onAcknowledgePurchaseResponse(p0: BillingResult) {
            Log.d(TAG, "Success Acknowledged : ${p0.responseCode}  :${p0.debugMessage}")
            // fetchHoneyBeeMapNavigationPurchasedInAppsFromConsole()
        }

    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                Log.d(TAG, "Process acknowledging: ${purchase.skus[0]}")
                val acknowledgeParamaters = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                googleBillingHoneyBeeMapNavigationClient.acknowledgePurchase(
                    acknowledgeParamaters,
                    purchaseAcknowledgedListener
                )
                /*Now update preferences..either restart app so that query will
                execute or here make preferences true for ads*/

                /*here only one product so call preferences in acknowledged.*/

                /*or after acknowledged call query Method*/
            }
        }
    }

    fun fetchGlobeMapPurchasedSubFromConsole() {
        googleBillingHoneyBeeMapNavigationClient.queryPurchasesAsync(BillingClient.SkuType.SUBS,
            object : PurchasesResponseListener {
                override fun onQueryPurchasesResponse(
                    billingResult: BillingResult,
                    listPurchased: MutableList<Purchase>,
                ) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

                        if (listPurchased.size > 0) {
                            for (singlePurchase in listPurchased) {
                                if (singlePurchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                    Log.d(TAG, "Product Purchased: ${singlePurchase.skus[0]}")
                                    billingHoneyBeeMapNavigationPreferences.edit()
                                        .putBoolean(singlePurchase.skus[0], true).apply()
                                } else {
                                    billingHoneyBeeMapNavigationPreferences.edit()
                                        .putBoolean(singlePurchase.skus[0], false).apply()
                                    Log.d(TAG, "Product Not Purchased: ${singlePurchase.skus[0]}")
                                }
                            }
                        } else {
                            Log.d(TAG, "Array List Purchase Null 1 $listPurchased")
                        }
                    } else {
                    }
                }

            })


    }
}