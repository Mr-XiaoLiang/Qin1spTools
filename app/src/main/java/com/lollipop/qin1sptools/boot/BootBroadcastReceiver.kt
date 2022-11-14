package com.lollipop.qin1sptools.boot

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.lollipop.qin1sptools.BuildConfig

class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        context.startActivity(Intent().apply {
            component = ComponentName(BuildConfig.APPLICATION_ID, "com.lollipop.qin1sptools.activity.MainActivity")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}