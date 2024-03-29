package com.lollipop.qin1sptools.boot

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.activity.games.J2meActivity
import com.lollipop.qin1sptools.floating.FloatingPanel
import com.lollipop.xposed.impl.HotKeyBroadcastReceiver


class AccessibilityService : Service(), HotKeyBroadcastReceiver.OnHotKeyDownCallback {

    companion object {
        private const val FOREGROUND_NOTIFICATION_ID = 10111909
        private const val NOTIFICATION_CHANNEL = "lollipop"
        private val MAIN_ACTIVITY = J2meActivity::class.java
    }

    private val floatingPanel = FloatingPanel(this)

    private val onHotKeyDownCallback = HotKeyBroadcastReceiver(this)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sendNotification()
        showFloatingView()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        onHotKeyDownCallback.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        onHotKeyDownCallback.unregister(this)
    }

    private fun showFloatingView() {
        floatingPanel.onCreate()
    }

    private fun sendNotification() {
        val notificationManager = NotificationManagerCompat.from(this)

        notificationManager.createNotificationChannel(
            NotificationChannelCompat.Builder(
                NOTIFICATION_CHANNEL,
                NotificationManagerCompat.IMPORTANCE_DEFAULT
            ).build()
        )

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setContentTitle(getString(R.string.title_accessibility_service))
            .setContentText(getString(R.string.msg_accessibility_service))
            .setOngoing(true)
            .setLocalOnly(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MAIN_ACTIVITY),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()

        startForeground(FOREGROUND_NOTIFICATION_ID, notification)
    }

    override fun onHotKeyDown(keyCode: Int, pkg: String) {
        floatingPanel.onHotKey(keyCode, pkg)
    }

}