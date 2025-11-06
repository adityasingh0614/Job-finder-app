package com.example.jobfinderapp.data.fcm
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.jobfinderapp.MainActivity
import com.example.jobfinderapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains notification payload
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "New Job Alert"
            val body = notification.body ?: "Check out new opportunities"

            // Extract job data if present
            val jobId = remoteMessage.data["jobId"]?.toIntOrNull()

            sendNotification(title, body, jobId)
        }

        // Handle data payload
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            if (remoteMessage.data.containsKey("jobId")) {
                val jobId = remoteMessage.data["jobId"]?.toIntOrNull()
                val title = remoteMessage.data["title"] ?: "New Job Alert"
                val body = remoteMessage.data["body"] ?: "Check out new opportunities"

                sendNotification(title, body, jobId)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")

        // Send token to your server
        sendTokenToServer(token)
    }

    private fun sendNotification(title: String, body: String, jobId: Int?) {
        // Create notification channel
        createNotificationChannel()

        // Create intent for notification tap
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (jobId != null) {
                putExtra("jobId", jobId)
                // Deep link data
                data = android.net.Uri.parse("jobfinder://job/$jobId")
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            jobId ?: 0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)

        // Add action buttons
        if (jobId != null) {
            val viewJobIntent = Intent(this, MainActivity::class.java).apply {
                data = android.net.Uri.parse("jobfinder://job/$jobId")
            }
            val viewJobPendingIntent = PendingIntent.getActivity(
                this,
                jobId + 1000,
                viewJobIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            notificationBuilder.addAction(
                R.drawable.ic_notification,
                "View Job",
                viewJobPendingIntent
            )
        }

        // Show notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(jobId ?: System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendTokenToServer(token: String) {
        // TODO: Send token to your AWS backend
        Log.d(TAG, "Sending token to server: $token")
    }

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "job_alerts_channel"
        private const val CHANNEL_NAME = "Job Alerts"
        private const val CHANNEL_DESCRIPTION = "Notifications for new job opportunities"
    }
}
