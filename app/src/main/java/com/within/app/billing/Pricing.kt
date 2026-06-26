package com.within.app.billing

/**
 * Product identifiers and fallback paywall copy. The IDs must match the subscription product and
 * its base plans configured in the Play Console. The price literals are placeholders shown only
 * until real [com.android.billingclient.api.ProductDetails] load from Play (see [BillingManager]).
 */
object Pricing {
    /** Single subscription product; annual/monthly are base plans under it. */
    const val SUBSCRIPTION_PRODUCT_ID = "within_premium"
    const val ANNUAL_PLAN_ID = "annual"
    const val MONTHLY_PLAN_ID = "monthly"

    const val ANNUAL_PRICE = "$59.99 / year"
    const val MONTHLY_PRICE = "$8.99 / month"
    const val TRIAL_DAYS = 7
}
