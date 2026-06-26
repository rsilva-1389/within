package com.within.app.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.within.app.data.preferences.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Owns the Google Play [BillingClient] and is the source of truth for the premium entitlement.
 * It writes the resolved entitlement into [UserPreferences] so the rest of the app keeps reading
 * `userPreferences.isPremium` — billing stays isolated here.
 *
 * Lifecycle: [start] (called once from MainActivity) connects, loads prices, and restores any
 * existing purchase. Entitlement is only *updated* on a successful Play query, so a dropped
 * connection never silently revokes access.
 */
@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _productDetails = MutableStateFlow<ProductDetails?>(null)
    val productDetails: StateFlow<ProductDetails?> = _productDetails.asStateFlow()

    /** Formatted price per base plan id (e.g. "annual" -> "$59.99"), from Play when available. */
    private val _planPrices = MutableStateFlow<Map<String, String>>(emptyMap())
    val planPrices: StateFlow<Map<String, String>> = _planPrices.asStateFlow()

    private val purchasesListener = PurchasesUpdatedListener { result, purchases ->
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.forEach { handlePurchase(it) }
        } else if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "Purchase canceled by user")
        } else {
            Log.w(TAG, "Purchase update failed: ${result.debugMessage}")
        }
    }

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(purchasesListener)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    private var connecting = false

    /** Connect (idempotent) and, once ready, load prices + restore purchases. */
    fun start() {
        if (billingClient.isReady || connecting) {
            if (billingClient.isReady) onConnected()
            return
        }
        connecting = true
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                connecting = false
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    onConnected()
                } else {
                    Log.w(TAG, "Billing setup failed: ${result.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                connecting = false // a later call to start() will reconnect
            }
        })
    }

    private fun onConnected() {
        queryProductDetails()
        restorePurchases()
    }

    private fun queryProductDetails() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(Pricing.SUBSCRIPTION_PRODUCT_ID)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            )
            .build()
        billingClient.queryProductDetailsAsync(params) { result, productDetailsList ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.w(TAG, "queryProductDetails failed: ${result.debugMessage}")
                return@queryProductDetailsAsync
            }
            val details = productDetailsList.firstOrNull() ?: return@queryProductDetailsAsync
            _productDetails.value = details
            _planPrices.value = details.subscriptionOfferDetails
                ?.associate { offer ->
                    offer.basePlanId to (
                        offer.pricingPhases.pricingPhaseList.firstOrNull()?.formattedPrice ?: ""
                        )
                }
                ?.filterValues { it.isNotEmpty() }
                ?: emptyMap()
        }
    }

    /** Restore: entitled iff an active, purchased subscription exists for our product. */
    private fun restorePurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        billingClient.queryPurchasesAsync(params) { result, purchases ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.w(TAG, "queryPurchases failed: ${result.debugMessage}")
                return@queryPurchasesAsync
            }
            val entitled = purchases.any { it.isOurPurchase() && it.isPurchased() }
            // Acknowledge anything not yet acknowledged (e.g. purchased on another device).
            purchases.filter { it.isOurPurchase() && it.isPurchased() }.forEach(::acknowledge)
            setEntitled(entitled)
        }
    }

    /**
     * Launch the Play purchase flow for the given base plan. No-op (logged) if products haven't
     * loaded yet — e.g. when the subscription isn't configured in the Play Console.
     */
    fun launchPurchase(activity: Activity, planId: String) {
        val details = _productDetails.value
        if (details == null) {
            Log.w(TAG, "launchPurchase: product details not loaded; is '$planId' configured in Play?")
            start() // try to (re)connect so a later tap can succeed
            return
        }
        val offerToken = details.subscriptionOfferDetails
            ?.firstOrNull { it.basePlanId == planId }
            ?.offerToken
        if (offerToken == null) {
            Log.w(TAG, "launchPurchase: no offer for base plan '$planId'")
            return
        }
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(details)
                        .setOfferToken(offerToken)
                        .build()
                )
            )
            .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

    private fun handlePurchase(purchase: Purchase) {
        if (!purchase.isOurPurchase() || !purchase.isPurchased()) return
        acknowledge(purchase)
        setEntitled(true)
    }

    private fun acknowledge(purchase: Purchase) {
        if (purchase.isAcknowledged) return
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { result ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.w(TAG, "acknowledge failed: ${result.debugMessage}")
            }
        }
    }

    private fun setEntitled(entitled: Boolean) {
        scope.launch { userPreferences.setPremium(entitled) }
    }

    private fun Purchase.isOurPurchase() = products.contains(Pricing.SUBSCRIPTION_PRODUCT_ID)
    private fun Purchase.isPurchased() = purchaseState == Purchase.PurchaseState.PURCHASED

    companion object {
        private const val TAG = "BillingManager"
    }
}
