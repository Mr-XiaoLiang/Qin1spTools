package com.lollipop.qin1sptools.task

import android.graphics.drawable.Drawable

/**
 * @author lollipop
 * @date 2021/8/30 20:36
 */
data class AppInfo(
    val packageName: String,
    val icon: Drawable,
    val label: CharSequence
)