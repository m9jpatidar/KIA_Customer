package com.booozie.partners.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.knowitall.customer.ui.activity.main.MainActivity
import com.knowitall.customer.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var notificationId = 0

    override fun onNewToken(token: String) {

        Log.d("TAG", "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(token);
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        var chat: String? = null
        println("RRR FireBaseMessaging data payload: " + remoteMessage.data)
        openMainActivity(remoteMessage.data["message"], !TextUtils.isEmpty(chat))
    }

    private fun openMainActivity(messageBody: String?, isChat: Boolean) {
        println("RRR FireBaseMessaging messageBody = [$messageBody], isChat = [$isChat]")
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = getString(R.string.continue_text)
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.splash_logo)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(uri)
                .setContentIntent(pIntent)
        val nm =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Booozie",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            nm.createNotificationChannel(channel)
        }
        nm.notify(notificationId++, notificationBuilder.build())
    }
}