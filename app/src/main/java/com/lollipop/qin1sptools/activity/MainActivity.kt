package com.lollipop.qin1sptools.activity

import android.os.Bundle
import com.lollipop.qin1sptools.databinding.ActivityMainBinding
import com.lollipop.qin1sptools.utils.lazyBind

class MainActivity : FeatureBarActivity() {

    private val binding: ActivityMainBinding by lazyBind()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding)
    }

}