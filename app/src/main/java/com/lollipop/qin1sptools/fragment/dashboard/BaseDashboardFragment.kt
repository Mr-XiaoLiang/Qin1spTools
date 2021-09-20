package com.lollipop.qin1sptools.fragment.dashboard

import android.content.Context
import com.lollipop.qin1sptools.activity.base.BaseFragment
import com.lollipop.qin1sptools.activity.dashboard.DashboardRefreshListener
import com.lollipop.qin1sptools.activity.dashboard.DashboardRefreshProvider
import java.lang.ref.WeakReference

/**
 * @author lollipop
 * @date 2021/9/20 19:27
 */
abstract class BaseDashboardFragment: BaseFragment(), DashboardRefreshListener {

    private var dashboardRefreshProvider: WeakReference<DashboardRefreshProvider>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        identityCheck<DashboardRefreshProvider>(context) {
            it.addRefreshListener(this)
            dashboardRefreshProvider = WeakReference(it)
        }
    }

    override fun onDetach() {
        super.onDetach()
        dashboardRefreshProvider?.get()?.removeRefreshListener(this)
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

}