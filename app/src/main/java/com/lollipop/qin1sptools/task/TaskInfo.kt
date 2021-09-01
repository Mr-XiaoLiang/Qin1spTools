package com.lollipop.qin1sptools.task

/**
 * @author lollipop
 * @date 2021/8/30 20:36
 */
data class TaskInfo(
    val appInfo: AppInfo,
    val processName: String,
    val pid: Int,
    val uid: Int,
    val pkgList: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TaskInfo

        if (appInfo != other.appInfo) return false
        if (processName != other.processName) return false
        if (pid != other.pid) return false
        if (uid != other.uid) return false
        if (!pkgList.contentEquals(other.pkgList)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = appInfo.hashCode()
        result = 31 * result + processName.hashCode()
        result = 31 * result + pid
        result = 31 * result + uid
        result = 31 * result + pkgList.contentHashCode()
        return result
    }
}