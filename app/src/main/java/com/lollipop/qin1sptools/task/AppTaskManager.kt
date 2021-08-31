package com.lollipop.qin1sptools.task

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable

/**
 * @author lollipop
 * @date 2021/8/30 20:35
 */
class AppTaskManager {

    companion object {
        /**
         * 应用原始信息
         */
        private val appResolveInfo = ArrayList<AppResolveInfo>()

        private const val LOCK_KEY = "AppInfoLock"

        private var needReloadAppInfo = true

        /**
         * 获取包名对应的应用名称
         */
        fun getLabel(context: Context, packageName: String): CharSequence {
            synchronized(LOCK_KEY) {
                for (info in appResolveInfo) {
                    if (packageName == info.pkgName) {
                        return info.getLabel(context.packageManager)
                    }
                }
                return ""
            }
        }

        /**
         * 获取包名对应的图标
         */
        fun loadIcon(context: Context, packageName: String): Drawable? {
            synchronized(LOCK_KEY) {
                for (info in appResolveInfo) {
                    if (packageName == info.pkgName) {
                        return info.loadIcon(context.packageManager)
                    }
                }
                return null
            }
        }
    }

    private var onAppInfoChanged = true

    val appInfoList = ArrayList<AppInfo>()

    val runningTaskList = ArrayList<TaskInfo>()

    fun refresh(context: Context) {

    }

    /**
     * 加载APP的信息
     */
    private fun loadAppInfo(context: Context) {
        synchronized(LOCK_KEY) {
            if (appResolveInfo.isEmpty() || needReloadAppInfo) {
                val pm = context.packageManager
                val mainIntent = Intent(Intent.ACTION_MAIN)
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                val appList = pm.queryIntentActivities(mainIntent, 0)
                appResolveInfo.clear()
                for (info in appList) {
                    appResolveInfo.add(AppResolveInfo(info))
                }
                onAppInfoChanged = true
            }
        }
        val tempAppInfoList = ArrayList<AppInfo>()
        tempAppInfoList.addAll(appInfoList)
        appInfoList.clear()

        val iconCache: HashMap<String, Drawable>?
        if (onAppInfoChanged) {
            // app的信息重新加载了，那么也丢掉图标的缓存
            iconCache = null
        } else {
            iconCache = HashMap()
            tempAppInfoList.forEach { appInfo ->
                iconCache[appInfo.packageName] = appInfo.icon
            }
        }
        tempAppInfoList.clear()
        val packageManager = context.packageManager
        appResolveInfo.forEach { appResolveInfo ->
            val pkgName = appResolveInfo.pkgName
            val label = appResolveInfo.getLabel(packageManager)
            val icon = iconCache?.get(pkgName) ?: appResolveInfo.loadIcon(packageManager)
            tempAppInfoList.add(AppInfo(pkgName, icon, label))
        }
        appInfoList.clear()
        appInfoList.addAll(tempAppInfoList)
        onAppInfoChanged = false
    }


    private class AppResolveInfo(
        val resolveInfo: ResolveInfo,
        var label: CharSequence = ""
    ) {
        val pkgName: String = resolveInfo.activityInfo.packageName

        fun getLabel(packageManager: PackageManager): CharSequence {
            if (label.isEmpty()) {
                val newLabel = resolveInfo.loadLabel(packageManager)
                label = newLabel
            }
            return label
        }

        fun loadIcon(packageManager: PackageManager): Drawable {
            return resolveInfo.loadIcon(packageManager)
        }

    }

}