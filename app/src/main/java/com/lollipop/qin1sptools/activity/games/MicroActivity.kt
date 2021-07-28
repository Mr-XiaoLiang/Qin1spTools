package com.lollipop.qin1sptools.activity.games

import android.os.Bundle
import android.view.WindowManager
import com.lollipop.qin1sptools.databinding.ActivityMicroBinding
import com.lollipop.qin1sptools.utils.lazyBind
import ru.playsoftware.j2meloader.base.BaseActivity

class MicroActivity : BaseActivity() {

    private val binding: ActivityMicroBinding by lazyBind()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    }

}