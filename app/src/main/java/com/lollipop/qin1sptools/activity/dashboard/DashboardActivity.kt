package com.lollipop.qin1sptools.activity.dashboard

import android.os.Bundle
import com.lollipop.qin1sptools.activity.base.FeatureBarActivity
import com.lollipop.qin1sptools.databinding.ActivityDashboardBinding
import com.lollipop.qin1sptools.utils.lazyBind

class DashboardActivity : FeatureBarActivity() {

    private val binding: ActivityDashboardBinding by lazyBind()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding)
    }
}