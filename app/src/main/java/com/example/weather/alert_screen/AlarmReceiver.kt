package com.example.weather.alert_screen

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.weather.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationType = intent.getStringExtra("notificationType") ?: "Alarm"
        val location = intent.getStringExtra("location") ?: "Unknown Location"

        // Check if notification type is "Notification" and if permissions are granted
        if (notificationType == "Notification") {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                showNotification(context, location)
            } else {
                // Log or inform the user that permission is not granted (if UI component is involved)
                // This is usually handled at the UI level when setting up notifications
            }
        } else {
            playAlarmSound(context)
        }
    }

    private fun showNotification(context: Context, location: String) {
        // Ensure the notification channel is created
        createNotificationChannel(context)

        val channelId = "alarm_channel_id"
        val notificationId = System.currentTimeMillis().toInt() // Unique ID to avoid overwriting notifications

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_alarm_24)
            .setContentTitle("Weather Alert")
            .setContentText("Weather alert for $location")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)

        // Check permission again before showing the notification (in case this gets called elsewhere)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(notificationId, builder.build())
        }
    }

    private fun playAlarmSound(context: Context) {
        try {
            val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // Fallback to notification sound
            val ringtone: Ringtone = RingtoneManager.getRingtone(context, alarmUri)

            // Play the ringtone
            if (!ringtone.isPlaying) {
                ringtone.play()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Optionally log the error or inform the user
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "alarm_channel_id"
            val channelName = "Weather Alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for weather alerts"
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
