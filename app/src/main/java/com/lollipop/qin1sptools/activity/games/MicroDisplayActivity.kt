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
        fun start(context: Context, name: String?, path: String) {
            context.startActivity(
                Intent(context, MicroDisplayActivity::class.java).apply {
                    data = Uri.parse(path)
                    putExtra(Constants.KEY_MIDLET_NAME, name)
                }
            )
        }

        private val LEFT_COMMAND_SEQUENCE = intArrayOf(
            Command.OK,
            Command.ITEM,
            Command.SCREEN,
            Command.STOP,
            Command.HELP,
            Command.EXIT,
            Command.CANCEL,
            Command.BACK,
        )

        private val CENTER_COMMAND_SEQUENCE = intArrayOf(
            Command.HELP,
            Command.ITEM,
            Command.SCREEN,
            Command.OK,
            Command.CANCEL,
            Command.EXIT,
            Command.STOP,
            Command.BACK,
        )

        private val RIGHT_COMMAND_SEQUENCE = intArrayOf(
            Command.BACK,
            Command.CANCEL,
            Command.HELP,
            Command.EXIT,
            Command.STOP,
            Command.SCREEN,
            Command.ITEM,
            Command.OK,
        )

    }

    private val binding: ActivityMicroDisplayBinding by lazyBind()

    private var isResumed = false

    private var display: Displayable? = null

    private var appName = ""

    private var microLoader: MicroLoader? = null

    private var exitDialog: MessageDialog? = null

    private var commentDialog: OptionDialog? = null

    private var leftOptionCommands = ArrayList<Command>()
    private var centerOptionCommand: Command? = null
    private var rightOptionCommand: Command? = null

    private val toastHelper by lazy {
        ViewToastHelper(binding.toastView) { view, value ->
            view.text = value
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
            updateFeatureBar(currentDisplay.commands)
            val title = currentDisplay.title ?: ""
            if (title.isNotEmpty()) {
                toastHelper.show(title)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        hideSystemUI()
        initView()
        initEventListener()
        updateFeatureBar(null)
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = plusFlags(
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION,
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY,
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION,
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN,
            View.SYSTEM_UI_FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    private fun updateFeatureBar(commands: Array<Command>?) {
        leftOptionCommands.clear()
        centerOptionCommand = null
        rightOptionCommand = null
        if (commands == null || commands.isEmpty()) {
            binding.featureBar.visibleOrGone(false)
            return
        }

        val overflowCommand = commands.size > 3

        commands.forEach { command ->
            val commandType = command.commandType
            val rightIndex = findIndex(commandType, RIGHT_COMMAND_SEQUENCE, rightOptionCommand)
            val centerIndex = findIndex(commandType, CENTER_COMMAND_SEQUENCE, centerOptionCommand)
            val leftIndex = findIndex(commandType, LEFT_COMMAND_SEQUENCE, null)

            if (overflowCommand) {
                if (rightIndex >= 0 && centerIndex >= 0) {
                    if (rightIndex > centerIndex) {
                        rightOptionCommand = command
                    } else {
                        centerOptionCommand = command
                    }
                } else if (rightIndex >= 0) {
                    rightOptionCommand = command
                } else if (centerIndex >= 0) {
                    centerOptionCommand = command
                } else {
                    leftOptionCommands.add(command)
                }
            } else {
                when (maxIndex(leftIndex, centerIndex, rightIndex)) {
                    0 -> {
                        leftOptionCommands.add(command)
                    }
                    1 -> {
                        centerOptionCommand = command
                    }
                    2 -> {
                        rightOptionCommand = command
                    }
                }
            }
        }

        updateCommandName()
    }

    private fun updateCommandName() {
        binding.featureBar.visibleOrGone(true)
        when {
            leftOptionCommands.isEmpty() -> {
                binding.leftOptionBtn.text = ""
            }
            leftOptionCommands.size == 1 -> {
                binding.leftOptionBtn.text = leftOptionCommands[0].label
            }
            else -> {
                binding.leftOptionBtn.setText(R.string.menu)
            }
        }
        binding.centerOptionBtn.text = centerOptionCommand?.label ?: ""
        binding.rightOptionBtn.text = rightOptionCommand?.label ?: ""
    }

    private fun maxIndex(vararg values: Int): Int {
        var maxValue = Int.MIN_VALUE
        var maxIndex = -1
        for (index in values.indices) {
            val value = values[index]
            if (value > maxValue) {
                maxValue = value
                maxIndex = index
            }
        }
        return maxIndex
    }

    private fun findIndex(type: Int, sequence: IntArray, value: Command?): Int {
        if (value != null) {
            return -1
        }
        return sequence.indexOf(type)
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

    private fun initEventListener() {
        addKeyEventRepeatListener(KeyEvent.BACK) {
            showBackDialog()
            onKeyUp(it, 0)
            true
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

    override fun onKeyDown(event: KeyEvent, repeatCount: Int): Boolean {
        log("onDown ---- $event: $repeatCount")
        if (event == KeyEvent.CALL) {
            takeScreenshot()
        }
        val keyCode = KeyEventProviderHelper.keyToGameCode(event)
        if (repeatCount == 0) {
            if (KeyEventPostHelper.postKeyPressed(current, keyCode)) {
                return true
            }
        } else {
            if (KeyEventPostHelper.postKeyRepeated(current, keyCode)) {
                return true
            }
        }
        return super.onKeyDown(event, repeatCount)
    }

    override fun onKeyUp(event: KeyEvent, repeatCount: Int): Boolean {
        log("onUp ---- $event: $repeatCount")
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
                    current?.fireCommandAction(command, current)
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

    private enum class Orientation(val value: Int) {
        DEFAULT(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED),
        AUTO(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR),
        PORTRAIT(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT),
        LANDSCAPE(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
    }

}