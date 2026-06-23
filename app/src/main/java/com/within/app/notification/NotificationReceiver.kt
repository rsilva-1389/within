package com.within.app.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.within.app.MainActivity
import com.within.app.R
import com.within.app.data.preferences.UserPreferences
import com.within.app.data.repository.MessageRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject lateinit var messageRepository: MessageRepository
    @Inject lateinit var userPreferences: UserPreferences
    @Inject lateinit var notificationScheduler: NotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val slotIndex = intent.getIntExtra(NotificationScheduler.EXTRA_SLOT_INDEX, 0)
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val categories = userPreferences.enabledCategories.first()
                val shownIds = userPreferences.shownMessageIds.first()
                val message = messageRepository.getRandomMessage(categories, shownIds)

                if (message != null) {
                    userPreferences.recordShownMessage(message.id)
                    showNotification(context, message.text, slotIndex)
                }

                val times = userPreferences.notificationTimes.first()
                notificationScheduler.scheduleAll(times)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, text: String, notificationId: Int) {
        val tapIntent = PendingIntent.getActivity(
            context, notificationId,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
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
}
