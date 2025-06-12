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
import org.json.JSONObject

class SpeedMeterPurchaseInApp(private val activityContext: Context) :
    PurchasesUpdatedListener {
    private val listAvailGlobeMapPurchases = ArrayList<SkuDetails>()
    private val listAvailGlobeMapSubPurchases = ArrayList<SkuDetails>()
    private lateinit var googleBillingHoneyBeeMapNavigationClient: BillingClient
    private val TAG = "BillingLogger:"
    private val listAvailHoneyBeeMapNavigationPurchases = ArrayList<SkuDetails>()
    private lateinit var billingHoneyBeeMapNavigationPreferences: SharedPreferences

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
                    fetchHoneyBeeMapNavigationAllInAppsFromConsole() /*available on console*/
                    fetchHoneyBeeMapNavigationPurchasedInAppsFromConsole()

                    fetchGlobeMapAllSubFromConsole()
                    fetchGlobeMapPurchasedSubFromConsole() /*user has purchased*/
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d(TAG, "Google Billing is  Disconnected")
            }
        })
    }

    private fun fetchHoneyBeeMapNavigationAllInAppsFromConsole() {
        val skuListToQuery = ArrayList<String>()
        skuListToQuery.add("ads_purchase")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuListToQuery).setType(BillingClient.SkuType.INAPP)
        googleBillingHoneyBeeMapNavigationClient.querySkuDetailsAsync(
            params.build(),
            object : SkuDetailsResponseListener {
                override fun onSkuDetailsResponse(
                    result: BillingResult,
                    skuDetails: MutableList<SkuDetails>?
                ) {
                    //Log.i(TAG, "onSkuResponse ${result?.responseCode}")
                    if (skuDetails != null) {
                        for (skuDetail2 in skuDetails) {
                            listAvailHoneyBeeMapNavigationPurchases.add(skuDetail2)
                            Log.i(TAG, "INAPP:" + skuDetail2.toString())
                            val helloWorld = skuDetail2.toString()
                            val hellWrld = helloWorld.replace("SkuDetails: ", "")
                            val hellWrld1 = hellWrld.replace("[", "")
                            val hellWrld2 = hellWrld1.replace("]", "")
                            Log.i(TAG + "abcd2", hellWrld2.toString())
                            try {
                                val priceAmountMicros = extractPriceAmountMicros(hellWrld2)
                                println("Price Amount Micros: $priceAmountMicros")
                                // Constants.billingLifeTime=priceAmountMicros!!
                                Log.i(TAG + "bLife", priceAmountMicros.toString())


                            } catch (ex: Exception) {

                            }
                        }
                    } else {
                        Log.i(TAG, "No skus for this application")
                    }
                }

            })
    }

    fun fetchGlobeMapPurchasedSubFromConsole() {
        googleBillingHoneyBeeMapNavigationClient.queryPurchasesAsync(BillingClient.SkuType.SUBS,
            object : PurchasesResponseListener {
                override fun onQueryPurchasesResponse(
                    billingResult: BillingResult,
                    listPurchased: MutableList<Purchase>
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

    fun fetchHoneyBeeMapNavigationPurchasedInAppsFromConsole() {

        googleBillingHoneyBeeMapNavigationClient.queryPurchasesAsync(BillingClient.SkuType.INAPP,
            object : PurchasesResponseListener {
                override fun onQueryPurchasesResponse(
                    billingResult: BillingResult,
                    listPurchased: MutableList<Purchase>
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
                            Log.d(TAG, "Array List Purchase Null$listPurchased")
                        }
                    } else {
                        Log.d(TAG, "Billing Checker Failed 1: ${billingResult.responseCode}")
                    }

                }

            })

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

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
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
            fetchHoneyBeeMapNavigationPurchasedInAppsFromConsole()
            fetchGlobeMapPurchasedSubFromConsole()
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

    fun extractPriceAmountMicros(jsonString: String): String? {
        try {
            val jsonObject = JSONObject(jsonString)
            return jsonObject.getString("price")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun fetchGlobeMapAllSubFromConsole() {
        val skuListToQuery = ArrayList<String>()

        skuListToQuery.add("1_monthly")
        skuListToQuery.add("1_weekly")
        skuListToQuery.add("1_yearly")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuListToQuery).setType(BillingClient.SkuType.SUBS)
        googleBillingHoneyBeeMapNavigationClient.querySkuDetailsAsync(
            params.build(),
            object : SkuDetailsResponseListener {
                override fun onSkuDetailsResponse(
                    result: BillingResult,
                    skuDetails: MutableList<SkuDetails>?
                ) {
                    //Log.i(TAG, "onSkuResponse ${result?.responseCode}")
                    if (skuDetails != null) {
                        var i = 0
                        for (skuDetail2 in skuDetails) {
                            i++
                            listAvailGlobeMapSubPurchases.add(skuDetail2)
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
                                    // Constants.billingMonthly=priceAmountMicros!!
                                    Log.i(TAG + "bMonthly", priceAmountMicros.toString())
                                } else if (i == 2) {
                                    //Constants.billingWeekly=priceAmountMicros!!
                                    Log.i(TAG + "bWeekly", priceAmountMicros.toString())
                                } else {
                                    // Constants.billingYearly=priceAmountMicros!!
                                    Log.i(TAG + "bYearly", priceAmountMicros.toString())
                                }


                            } catch (ex: Exception) {

                            }

                        }
                    } else {
                        Log.i(TAG, "No skus for this application")
                    }
                }

            })
    }

    fun purchaseGlobeMapMonthlyPackage() {
        Log.d(TAG, "Going to purchase ads_purchase")
        if (listAvailGlobeMapSubPurchases.size > 0) {
            try {
                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(listAvailGlobeMapSubPurchases[0])
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

    fun purchaseGlobeMapYearlyPackage() {
        Log.d(TAG, "Going to purchase ads_purchase")
        if (listAvailGlobeMapSubPurchases.size > 0) {
            try {
                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(listAvailGlobeMapSubPurchases[2])
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

    fun purchaseGlobeMapWeeklyPackage() {
        Log.d(TAG, "Going to purchase ads_purchase")
        if (listAvailGlobeMapSubPurchases.size > 0) {
            try {
                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(listAvailGlobeMapSubPurchases[1])
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

}