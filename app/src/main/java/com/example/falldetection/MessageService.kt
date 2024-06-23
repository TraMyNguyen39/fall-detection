package com.example.falldetection

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bumptech.glide.util.Util
import com.example.falldetection.data.model.FallHistoryItem
import com.example.falldetection.ui.DangerousAlertActivity
import com.example.falldetection.utils.Utils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MessageService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: "Fall Detection"
            val patientEmail = remoteMessage.data["patientEmail"] ?: "Cảnh báo"
            val patientName = remoteMessage.data["patientName"] ?: "Không rõ"
            val address = remoteMessage.data["address"] ?: "Cảnh báo"
            val time = remoteMessage.data["time"] ?: "Cảnh báo"

//            val history = FallHistoryItem(patientEmail, Utils.timeStringToDate(time), address)
            sendNotification(title, patientName, patientEmail, time)
        }
    }

    private fun isAppInBackground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return true

        for (processInfo in runningAppProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                && processInfo.processName == context.packageName
            ) {
                return false
            }
        }
        return true
    }

    private fun startAlertActivity(
        patientName: String,
        patientEmail: String,
        time: String
    ): Intent {
        val intent = Intent(this, DangerousAlertActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        intent.putExtra(DangerousAlertActivity.PATIENT_EMAIL, patientEmail)
        intent.putExtra(DangerousAlertActivity.PATIENT_NAME, patientName)
//        intent.putExtra(DangerousAlertActivity.ADDRESS, fallHistoryItem.address)
        intent.putExtra(DangerousAlertActivity.TIME, time)
        return intent
    }

    private fun sendNotification(title: String, patientName: String, patientEmail: String, time: String) {
        // Intent for the notification click
        val intent = startAlertActivity(patientName, patientEmail, time)

        val pendingIntent = PendingIntent.getActivity(
            this,
            100,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val cancelIntent = Intent(this, CancelActionReceiver::class.java)
        val cancelPendingIntent = PendingIntent.getBroadcast(
            this,
            102,
            cancelIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val customSoundUri =
            Uri.parse("android.resource://" + packageName + "/" + R.raw.military_alarm)
        val channelId = getString(R.string.channel_dangerous_id)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.dangerous)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.alert))
            .setContentTitle(title)
            .setContentText("Khẩn cấp $patientName bị ngã!")
//            .setAutoCancel(true)
            .setSound(customSoundUri, AudioManager.STREAM_ALARM)
            .addAction(R.drawable.alert, "Xem thông tin", pendingIntent)
            .addAction(R.drawable.ic_cancel, "Hủy", cancelPendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notification channel for Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel =
                NotificationChannel(channelId, "Dangerous", NotificationManager.IMPORTANCE_HIGH)
//            channel.setSound(customSoundUri, audioAttributes)
//            channel.setSound(customSoundUri, audioAttributes)
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.setSound(customSoundUri, audioAttributes)

            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}

class CancelActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(0) // Hủy thông báo với id là 0
    }
}