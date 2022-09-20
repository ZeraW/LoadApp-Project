package com.udacity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

// Notification ID.
private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String,fileName:String,status:String, applicationContext: Context) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra("fileName",fileName)
    contentIntent.putExtra("status",status)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_CANCEL_CURRENT
    )


    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.success_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_baseline_cloud_download)
        .setContentTitle(applicationContext
            .getString(R.string.app_name))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)

        .addAction(
            R.drawable.ic_baseline_cloud_download,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent
        )
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}


fun NotificationManager.cancelNotifications() {
    cancelAll()
}


