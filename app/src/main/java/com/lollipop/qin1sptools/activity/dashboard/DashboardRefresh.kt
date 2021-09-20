package com.lollipop.qin1sptools.activity.dashboard

/**
 * @author lollipop
 * @date 2021/9/20 19:26
 */
interface DashboardRefreshProvider {
    fun addRefreshListener(listener: DashboardRefreshListener)
    fun removeRefreshListener(listener: DashboardRefreshListener)
}

fun interface DashboardRefreshListener {
    fun onRefresh()
}