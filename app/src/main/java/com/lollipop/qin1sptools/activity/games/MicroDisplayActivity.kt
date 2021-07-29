package com.lollipop.qin1sptools.activity.games

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import com.lollipop.qin1sptools.databinding.ActivityMicroDisplayBinding
import com.lollipop.qin1sptools.utils.lazyBind
import com.lollipop.qin1sptools.utils.task
import ru.playsoftware.j2meloader.base.BaseActivity
import javax.microedition.lcdui.Displayable
import javax.microedition.shell.MidletThread
import javax.microedition.util.ContextHolder
import javax.microedition.util.DisplayHost

class MicroDisplayActivity : BaseActivity(), DisplayHost {

    private val binding: ActivityMicroDisplayBinding by lazyBind()

    private var isResumed = false

    private var display: Displayable? = null

    private val updateCurrentTask = task {
        // TODO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        ContextHolder.setCurrentHost(this)
    }

    override fun onResume() {
        super.onResume()
        isResumed = true
        MidletThread.resumeApp()
    }

    override fun onPause() {
        isResumed = false
        MidletThread.pauseApp()
        super.onPause()
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun isVisible(): Boolean {
        return isResumed
    }

    override fun getCurrent(): Displayable? {
        return display
    }

    override fun setCurrent(displayable: Displayable?) {
        this.display = displayable
        updateCurrentTask.cancel()
        updateCurrentTask.sync()
    }

}