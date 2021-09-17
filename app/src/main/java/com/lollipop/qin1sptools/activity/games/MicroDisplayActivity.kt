package com.lollipop.qin1sptools.activity.games

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.activity.base.BaseActivity
import com.lollipop.qin1sptools.databinding.ActivityMicroDisplayBinding
import com.lollipop.qin1sptools.dialog.MessageDialog
import com.lollipop.qin1sptools.dialog.OptionDialog
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.event.KeyEventProviderHelper
import com.lollipop.qin1sptools.event.SimpleKeyEventRepeatCallback
import com.lollipop.qin1sptools.guide.Guide
import com.lollipop.qin1sptools.utils.*
import ru.playsoftware.j2meloader.util.Constants
import javax.microedition.lcdui.Canvas
import javax.microedition.lcdui.Command
import javax.microedition.lcdui.Displayable
import javax.microedition.lcdui.event.KeyEventPostHelper
import javax.microedition.lcdui.overlay.OverlayView
import javax.microedition.shell.MicroLoader
import javax.microedition.shell.MidletThread
import javax.microedition.util.ContextHolder
import javax.microedition.util.DisplayHost
import kotlin.math.min

class MicroDisplayActivity : BaseActivity(), DisplayHost {

    companion object {

        private const val PARAMS_STATUS_BAR_SIZE = "PARAMS_STATUS_BAR_SIZE"

        fun start(context: Context, name: String?, path: String, statusBarSize: Int = -1) {
            context.startActivity(
                Intent(context, MicroDisplayActivity::class.java).apply {
                    data = Uri.parse(path)
                    putExtra(Constants.KEY_MIDLET_NAME, name)
                    putExtra(PARAMS_STATUS_BAR_SIZE, statusBarSize)
                }
            )
        }

    }

    private val binding: ActivityMicroDisplayBinding by lazyBind()

    private var isResumed = false

    private var display: Displayable? = null

    private var appName = ""

    private var microLoader: MicroLoader? = null

    private var exitDialog: MessageDialog? = null

    private var commentDialog: OptionDialog? = null

    private val toastHelper by lazy {
        ViewToastHelper(binding.toastView) { view, value ->
            view.text = value
        }
    }

    private val commandOptionBarDelegate by lazy {
        CommandOptionBarDelegate(
            optionBar = { binding.featureBar },
            leftButton = { binding.leftOptionBtn },
            centerButton = { binding.centerOptionBtn },
            rightButton = { binding.rightOptionBtn },
            fireCommand = ::fireCommand,
            showCommandMenu = ::showCommentDialog
        )
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
            commandOptionBarDelegate.updateFeatureBar(currentDisplay.commands)
            val title = currentDisplay.title ?: ""
            if (title.isNotEmpty()) {
                toastHelper.show(title)
            }
        }
        hideSystemUI()
    }

    private fun updateStatusBarSize() {
        val size = intent?.getIntExtra(PARAMS_STATUS_BAR_SIZE, -1) ?: -1
        if (size < 0) {
            return
        }
        ContextHolder.setStatusBarSize(size)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        updateStatusBarSize()
        hideSystemUI()
        initView()
        initEventListener()
        addKeyEventListener(commandOptionBarDelegate)
        commandOptionBarDelegate.updateFeatureBar(null)
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = plusFlags(
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION,
            View.SYSTEM_UI_FLAG_IMMERSIVE,
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION,
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN,
            View.SYSTEM_UI_FLAG_FULLSCREEN
        )
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    private fun plusFlags(vararg flags: Int): Int {
        var value = 0
        flags.forEach {
            value = value or it
        }
        return value
    }

    private fun initView() {
        binding.osdContainer.setPadding(0, ContextHolder.getStatusBarSize(), 0, 0)
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

    private fun initEventListener() {
        addKeyEventRepeatListener(KeyEvent.BACK,
            SimpleKeyEventRepeatCallback {
                showBackDialog()
                onKeyUp(it, 0)
                true
            })
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
                showMidletDialog(midletNameArray, midletClassArray) {
                    MidletThread.create(microLoader, midletClassArray[0])
                }
            }
        }
    }

    private fun showMidletDialog(
        nameArray: Array<String>,
        classArray: Array<String>,
        onSelected: (String) -> Unit
    ) {
        OptionDialog.build(this) {
            setTitle(R.string.menu)
            dataList.clear()
            for (index in 0 until min(nameArray.size, classArray.size)) {
                dataList.add(OptionDialog.Item(nameArray[index], index))
            }
            setLeftButton(R.string.ok) {
                if (it is OptionDialog) {
                    val selectedPosition = it.selectedPosition
                    if (selectedPosition in classArray.indices) {
                        onSelected(classArray[selectedPosition])
                        it.dismiss()
                        return@setLeftButton
                    }
                }
                finish()
            }
            setRightButton(R.string.exit) {
                finish()
            }
        }.show()
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


    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        // 重写方法，优先供给游戏
        if (event != null) {
            val gameKey = KeyEventProviderHelper.keyToGameCode(
                KeyEventProviderHelper.findKeyByCode(keyCode)
            )
            val repeatCount = event.repeatCount
            if (repeatCount == 0) {
                KeyEventPostHelper.postKeyPressed(current, gameKey)
            } else {
                KeyEventPostHelper.postKeyRepeated(current, gameKey)
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        // 重写方法，优先供给游戏
        if (event != null) {
            val gameKey = KeyEventProviderHelper.keyToGameCode(
                KeyEventProviderHelper.findKeyByCode(keyCode)
            )
            KeyEventPostHelper.postKeyReleased(current, gameKey)
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyDown(event: KeyEvent, repeatCount: Int): Boolean {
        log("onDown ---- $event: $repeatCount")
        if (event == KeyEvent.CALL) {
            takeScreenshot()
        }
        return super.onKeyDown(event, repeatCount)
    }

    private fun showBackDialog() {
        if (exitDialog != null) {
            return
        }
        exitDialog = MessageDialog.build(this) {
            setMessage(R.string.dialog_msg_exit)
            setLeftButton(R.string.exit) {
                finish()
            }
            setRightButton(R.string.stay) {
                resume()
                it.dismiss()
                exitDialog = null
            }
        }
        exitDialog?.show()
        pause()
    }

    private fun showCommentDialog(commandList: List<Command>) {
        val localCommand = ArrayList<Command>()
        localCommand.addAll(commandList)
        commentDialog = OptionDialog.build(this) {
            setTitle(R.string.menu)
            dataList.clear()
            localCommand.forEach {
                dataList.add(OptionDialog.Item(it.label ?: "", it.commandType, it))
            }
            setLeftButton(R.string.ok) {
                if (it is OptionDialog) {
                    val selectedPosition = it.selectedPosition
                    val command = localCommand[selectedPosition]
                    fireCommand(command)
                }
                it.dismiss()
            }
            setRightButton(R.string.cancel) {
                it.dismiss()
            }
            onDismiss {
                resume()
                commentDialog = null
            }
        }
        commentDialog?.show()
        pause()
    }

    private fun fireCommand(command: Command) {
        current?.fireCommandAction(command, current)
    }

    override fun onResume() {
        super.onResume()
        resume()
    }

    override fun onPause() {
        pause()
        super.onPause()
    }

    private fun pause() {
        isResumed = false
        MidletThread.pauseApp()
    }

    private fun resume() {
        isResumed = true
        MidletThread.resumeApp()
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        hideSystemUI()
    }

    override fun onBackPressed() {
        onKeyDown(KeyEvent.BACK, 0)
        onKeyUp(KeyEvent.BACK, 0)
    }

    override fun buildGuide(builder: Guide.Builder) {
        builder.clean()
            .next(KeyEvent.BACK, R.string.guide_micro_back)
            .next(KeyEvent.CALL, R.string.guide_micro_screenshot)
    }

    private enum class Orientation(val value: Int) {
        DEFAULT(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED),
        AUTO(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR),
        PORTRAIT(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT),
        LANDSCAPE(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
    }

}