package com.lollipop.qin1sptools.task

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable


/**
 * @author lollipop
 * @date 2021/8/30 20:35
 * 多任务管理类
 */
class AppTaskManager {

    companion object {
        /**
         * 应用原始信息
         */
        private val appResolveInfo = ArrayList<AppResolveInfo>()

        /**
         * 同步锁，避免异步问题
         */
        private const val LOCK_KEY = "AppInfoLock"

        private var needReloadAppInfo = true

        private val processWhiteList = arrayOf(
            "system",
            "com.Android.phone"
        )

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

        /**
         * 获取内存信息
         */
        fun getMemoryInfo(context: Context): ActivityManager.MemoryInfo {
            return ActivityManager.MemoryInfo().apply {
                context.getSystemService(Context.ACTIVITY_SERVICE)?.let {
                    if (it is ActivityManager) {
                        it.getMemoryInfo(this)
                    }
                }
            }
        }

        /**
         * 杀死进程
         */
        private fun killProcess(activityManager: ActivityManager, packageName: String): Boolean {
            return try {
                activityManager.killBackgroundProcesses(packageName)
                true
            } catch (e: Throwable) {
                e.printStackTrace()
                false
            }
        }

        fun kill(context: Context, appInfo: AppInfo): Boolean {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE)
            if (activityManager !is ActivityManager) {
                return false
            }
            return killProcess(activityManager, appInfo.packageName)
        }
    }

    private var onAppInfoChanged = true

    val appInfoList = ArrayList<AppInfo>()

    val runningTaskList = ArrayList<TaskInfo>()

    fun refresh(context: Context) {
        loadAppInfo(context)
        loadRunningProcess(context)
    }

    fun kill(context: Context, taskInfo: TaskInfo): Boolean {
        return kill(context, taskInfo.appInfo)
    }

    /**
     * 加载进程信息
     */
    private fun loadRunningProcess(context: Context) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE)
        if (activityManager !is ActivityManager) {
            runningTaskList.clear()
            return
        }
        val tempList = ArrayList<TaskInfo>()
        val runningAppProcesses = activityManager.runningAppProcesses
        for (processInfo in runningAppProcesses) {
            if (isInWhiteList(processInfo.processName)) {
                continue
            }
            val pkgList = processInfo.pkgList
            if (pkgList.isEmpty()) {
                continue
            }
            val appInfo = findAppInfoByPackage(pkgList) ?: continue
            tempList.add(TaskInfo(
                appInfo,
                processInfo.processName,
                processInfo.pid,
                processInfo.uid,
                processInfo.pkgList
            ))
        }
    }

    private fun isInWhiteList(processName: String): Boolean {
        return processName in processWhiteList
    }

    private fun findAppInfoByPackage(nameArray: Array<String>): AppInfo? {
        return appInfoList.find { it.packageName in nameArray }
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