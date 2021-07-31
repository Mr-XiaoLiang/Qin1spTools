package com.lollipop.qin1sptools.activity.games

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import com.lollipop.qin1sptools.activity.base.BaseActivity
import com.lollipop.qin1sptools.databinding.ActivityMicroDisplayBinding
import com.lollipop.qin1sptools.utils.lazyBind
import com.lollipop.qin1sptools.utils.task
import ru.playsoftware.j2meloader.util.Constants
import java.lang.Exception
import java.lang.RuntimeException
import javax.microedition.lcdui.Displayable
import javax.microedition.shell.MicroLoader
import javax.microedition.shell.MidletThread
import javax.microedition.util.ContextHolder
import javax.microedition.util.DisplayHost

class MicroDisplayActivity : BaseActivity(), DisplayHost {

    private val binding: ActivityMicroDisplayBinding by lazyBind()

    private var isResumed = false

    private var display: Displayable? = null

    private var appName = ""

    private var microLoader: MicroLoader? = null

    private val updateCurrentTask = task {
        TODO("更新当前的画板")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        ContextHolder.setCurrentHost(this)
        ContextHolder.setVibration(true)
        appName = intent.getStringExtra(Constants.KEY_MIDLET_NAME) ?: ""
        val data = intent.data
        if (data == null) {
            onError()
            return
        }
        val appPath = data.toString()
        microLoader = MicroLoader(this, appPath)
        if (microLoader?.init() != true) {
            onError()
            return
        }
        microLoader?.applyConfiguration()
        setOrientation(microLoader?.orientation ?: -1)
        try {
            loadMIDlet()
        } catch (e: Exception) {
            e.printStackTrace()
            onError()
        }
    }

    @Throws(Exception::class)
    private fun loadMIDlet() {
        val midletList =
            microLoader?.loadMIDletList() ?: throw RuntimeException("MicroLoader not found")
        val size = midletList.size
        val midletNameArray = midletList.values.toTypedArray()
        val midletClassArray = midletList.keys.toTypedArray()
        when (size) {
            0 -> {
                throw Exception("No MIDlet found")
            }
            1 -> {
                MidletThread.create(microLoader, midletClassArray[0])
            }
            else -> {
                showMidletDialog(midletNameArray, midletClassArray)
            }
        }
    }

    private fun showMidletDialog(nameArray: Array<String>, classArray: Array<String>) {
        TODO("程序选择器")
    }

    private fun onError() {
        finish()
    }

    private fun setOrientation(orientation: Int) {
        val o = if (orientation >= 0 && orientation < Orientation.values().size) {
            Orientation.values()[orientation]
        } else {
            Orientation.DEFAULT
        }
        requestedOrientation = o.value
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

    private enum class Orientation(val value: Int) {
        DEFAULT(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED),
        AUTO(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR),
        PORTRAIT(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT),
        LANDSCAPE(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
    }

}