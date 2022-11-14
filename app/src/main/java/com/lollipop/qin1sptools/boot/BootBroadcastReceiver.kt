package com.lollipop.qin1sptools.boot

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootBroadcastReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
//        Intent().apply {
//            component = ComponentName(
//                BuildConfig.APPLICATION_ID,
//                "com.lollipop.qin1sptools.boot.AccessibilityService"
//            )
//        }
        context.startService(Intent(context, AccessibilityService::class.java))
    }
}