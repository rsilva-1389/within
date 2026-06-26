package com.within.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.within.app.billing.BillingManager
import com.within.app.ui.navigation.AppNavigation
import com.within.app.ui.theme.WithinTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var billingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Connect to Play, load prices, and restore any existing entitlement.
        billingManager.start()
        enableEdgeToEdge()
        setContent {
            WithinTheme {
                AppNavigation()
            }
        }
    }
}
