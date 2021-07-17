package com.lollipop.qin1sptools

import android.os.Bundle
import com.lollipop.qin1sptools.databinding.ActivityMainBinding
import com.lollipop.qin1sptools.utils.lazyBind

class MainActivity : BaseActivity() {

    private val binding: ActivityMainBinding by lazyBind()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding)
    }

}