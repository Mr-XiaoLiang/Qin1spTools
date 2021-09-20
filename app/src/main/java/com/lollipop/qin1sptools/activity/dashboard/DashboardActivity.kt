package com.lollipop.qin1sptools.activity.dashboard

import android.os.Bundle
import com.lollipop.qin1sptools.activity.base.FeatureBarActivity
import com.lollipop.qin1sptools.databinding.ActivityDashboardBinding
import com.lollipop.qin1sptools.utils.FeatureIcon
import com.lollipop.qin1sptools.utils.lazyBind
import com.lollipop.qin1sptools.utils.onUI

class DashboardActivity : FeatureBarActivity(), DashboardRefreshProvider {

    private val binding: ActivityDashboardBinding by lazyBind()

    private val refreshListenerList = ArrayList<DashboardRefreshListener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding)
        setFeatureButtons(left = FeatureIcon.REFRESH)
    }

    override fun onLeftFeatureButtonClick(): Boolean {
        onUI {
            refreshListenerList.forEach {
                it.onRefresh()
            }
        }
        return true
    }

    override fun addRefreshListener(listener: DashboardRefreshListener) {
        refreshListenerList.add(listener)
    }

    override fun removeRefreshListener(listener: DashboardRefreshListener) {
        refreshListenerList.remove(listener)
    }
}