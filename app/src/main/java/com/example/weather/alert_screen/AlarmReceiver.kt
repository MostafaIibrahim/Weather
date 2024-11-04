package com.example.weather.alert_screen

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.weather.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        println("I am here in on recieve")
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
        println("I am in show Notification")
        createNotificationChannel(context)

        val channelId = "alarm_channel_id"
        val notificationId = System.currentTimeMillis().toInt() // Unique ID to avoid overwriting notifications

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_alarm_24)
            .setContentTitle("Weather Alert")
            .setContentText("It's time for your weather alert at $location! Stay prepared.")
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
            println("I am in play alarm")

            // Get the alarm sound URI, fallback to notification sound if unavailable
            val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone: Ringtone = RingtoneManager.getRingtone(context, alarmUri)

            // Play the ringtone if it's not already playing
            if (!ringtone.isPlaying) {
                ringtone.play()
            }

            // Show the alarm dialog with "Dismiss" and "Open" options
            AlertDialog.Builder(context).apply {
                setTitle("Alarm")
                setMessage("The alarm is ringing. What would you like to do?")

                // Dismiss option
                setNegativeButton("Dismiss") { dialog, _ ->
                    ringtone.stop() // Stop the alarm sound
                    dialog.dismiss() // Close the dialog
                }

                // Open option to navigate to the Alarm screen
                setPositiveButton("Open") { dialog, _ ->
                    ringtone.stop() // Stop the alarm sound
                    dialog.dismiss() // Close the dialog

                    if (context is FragmentActivity) {
                        val fragmentManager: FragmentManager = context.supportFragmentManager
                        val alarmFragment = AlertFragment() // Replace with your actual AlarmFragment

                        // Begin a fragment transaction to replace the current fragment with AlarmFragment
                        fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, alarmFragment) // Replace with your container ID
                            .addToBackStack(null) // Add to back stack to allow going back
                            .commit()
                    }
                }

                // Make the dialog not cancellable so the user has to choose an option
                setCancelable(false)
                create().show()
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
    class StopAlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            cancelAlarm(context, intent.getIntExtra("location",0)) // Pass the request code used when setting the alarm

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(1) // Cancel the alarm notification
        }

        private fun cancelAlarm(context: Context, requestCode: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}


