package com.lollipop.qin1sptools.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Intent

object AccessibilityHelper {

    fun startMouseController(activity: Activity) {
        activity.startActivity(Intent().apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            component = ComponentName("com.android.settings", "com.android.settings.Settings\$MouseControlerFragmentActivity")
        })
    }

}