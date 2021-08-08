package com.lollipop.qin1sptools.activity.games

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.activity.base.BaseActivity
import com.lollipop.qin1sptools.databinding.ActivityMicroDisplayBinding
import com.lollipop.qin1sptools.event.EventRepeater
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.event.KeyEventProviderHelper
import com.lollipop.qin1sptools.utils.*
import ru.playsoftware.j2meloader.util.Constants
import javax.microedition.lcdui.Canvas
import javax.microedition.lcdui.Displayable
import javax.microedition.lcdui.event.KeyEventPostHelper
import javax.microedition.lcdui.overlay.OverlayView
import javax.microedition.shell.MicroLoader
import javax.microedition.shell.MidletThread
import javax.microedition.util.ContextHolder
import javax.microedition.util.DisplayHost

class MicroDisplayActivity : BaseActivity(), DisplayHost {

    companion object {
        fun start(context: Context, name: String?, path: String) {
            context.startActivity(
                Intent(context, MicroDisplayActivity::class.java).apply {
                    data = Uri.parse(path)
                    putExtra(Constants.KEY_MIDLET_NAME, name)
                }
            )
        }

        private const val CLICK_DELAY = 300L

        private const val BACK_THRESHOLD = 5

    }

    private val binding: ActivityMicroDisplayBinding by lazyBind()

    private var isResumed = false

    private var display: Displayable? = null

    private var appName = ""

    private var microLoader: MicroLoader? = null

    private var lastPresBackTime = 0L

    private var backClickCount = 0

    private val toastHelper by lazy {
        ViewToastHelper(binding.toastView) { view, value ->
            view.text = value
        }
    }

    private val eventRepeater = EventRepeater { repeater, event ->
        log("onRepeat ---- $event")
        val canvas = current
        if (canvas == null) {
            repeater.onKeyUp(event)
        } else {
            if (!KeyEventPostHelper.postKeyRepeated(
                    canvas,
                    KeyEventProviderHelper.keyToGameCode(event)
                )
            ) {
                repeater.onKeyUp(event)
            }
        }
    }

    private val updateCurrentTask = task {
        val currentDisplay = current
        currentDisplay?.clearDisplayableView()
        binding.displayableContainer.removeAllViews()
        currentDisplay?.let {
            binding.displayableContainer.addView(
                currentDisplay.displayableView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        hideSystemUI()
        initView()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = plusFlags(
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION,
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY,
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION,
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN,
            View.SYSTEM_UI_FLAG_FULLSCREEN
        )
    }

    private fun plusFlags(vararg flags: Int): Int {
        var value = 0
        flags.forEach {
            value = value or it
        }
        return value
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
        // TODO("程序选择器")
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

    override fun onKeyDown(event: KeyEvent, repeatCount: Int): Boolean {
        log("onDown ---- $event")
        if (event == KeyEvent.BACK) {
            val now = System.currentTimeMillis()
            if (now - lastPresBackTime < CLICK_DELAY) {
                backClickCount ++
                if (backClickCount >= BACK_THRESHOLD) {
                    backClickCount = 0
                    showBackDialog()
                    return true
                }
            } else {
                backClickCount = 0
            }
            lastPresBackTime = now
        }
        if (event == KeyEvent.CALL) {
            takeScreenshot()
        }
        val keyCode = KeyEventProviderHelper.keyToGameCode(event)
        if (repeatCount == 0) {
            if (KeyEventPostHelper.postKeyPressed(current, keyCode)) {
                eventRepeater.onKeyDown(event)
                return true
            }
        } else {
            if (KeyEventPostHelper.postKeyRepeated(current, keyCode)) {
                eventRepeater.onKeyDown(event)
                return true
            }
        }
        return super.onKeyDown(event, repeatCount)
    }

    override fun onKeyUp(event: KeyEvent, repeatCount: Int): Boolean {
        log("onUp ---- $event")
        eventRepeater.onKeyUp(event)
        if (KeyEventPostHelper.postKeyReleased(
                current,
                KeyEventProviderHelper.keyToGameCode(event)
            )
        ) {
            return true
        }
        return super.onKeyUp(event, repeatCount)
    }

    private fun showBackDialog() {
        // TODO
        super.onBackPressed()
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

    override fun getRootView(): ViewGroup {
        return binding.root
    }

    override fun getOverlayView(): OverlayView {
        return binding.microOverlayView
    }

    override fun onDestroy() {
        super.onDestroy()
        eventRepeater.destroy()
    }

    private fun takeScreenshot() {
        doAsync({
            onUI {
                toastHelper.show(getString(R.string.screenshot_save_error))
            }
        }) {
            val currentDisplay = current
            if (currentDisplay is Canvas) {
                val takeScreenshotSync = MicroLoader.takeScreenshotSync(currentDisplay)
                onUI {
                    toastHelper.show(getString(R.string.screenshot_save_in, takeScreenshotSync))
                }
            }
        }
    }

    private enum class Orientation(val value: Int) {
        DEFAULT(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED),
        AUTO(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR),
        PORTRAIT(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT),
        LANDSCAPE(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
    }

}