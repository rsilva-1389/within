package com.within.app.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.within.app.MainActivity
import com.within.app.R
import com.within.app.data.preferences.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject lateinit var dailyContentResolver: DailyContentResolver
    @Inject lateinit var userPreferences: UserPreferences
    @Inject lateinit var notificationScheduler: NotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val slotIndex = intent.getIntExtra(NotificationScheduler.EXTRA_SLOT_INDEX, 0)
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (val content = dailyContentResolver.resolve()) {
                    is DailyContent.Journey ->
                        showNotification(context, content.text, slotIndex, content.deepLink())
                    is DailyContent.Message -> {
                        userPreferences.recordShownMessage(content.messageId)
                        showNotification(context, content.text, slotIndex, deepLink = null)
                    }
                    DailyContent.None -> Unit
                }

                val times = userPreferences.notificationTimes.first()
                notificationScheduler.scheduleAll(times)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, text: String, notificationId: Int, deepLink: String?) {
        val tapIntent = PendingIntent.getActivity(
            context, notificationId,
            buildContentIntent(context, deepLink),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationScheduler.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(tapIntent)
            .setAutoCancel(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(notificationId, notification)
    }

    /** Deep links into the day when it's a journey prompt; otherwise just opens the app. */
    private fun buildContentIntent(context: Context, deepLink: String?): Intent =
        if (deepLink != null) {
            Intent(Intent.ACTION_VIEW, Uri.parse(deepLink), context, MainActivity::class.java)
        } else {
            Intent(context, MainActivity::class.java)
        }.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
}

private fun DailyContent.Journey.deepLink(): String = "within://journey/$journeyId/day/$day"
