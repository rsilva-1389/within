package com.within.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.within.app.data.preferences.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var userPreferences: UserPreferences
    @Inject lateinit var notificationScheduler: NotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val onboarded = userPreferences.isOnboardingCompleted.first()
                if (onboarded) {
                    val times = userPreferences.notificationTimes.first()
                    notificationScheduler.scheduleAll(times)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
