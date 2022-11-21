package com.lollipop.qin1sptools.floating

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.lollipop.qin1sptools.databinding.FloatingHintBinding
import com.lollipop.qin1sptools.databinding.FloatingRootBinding
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.event.KeyEvent.*
import com.lollipop.qin1sptools.event.KeyEventListener
import com.lollipop.qin1sptools.event.KeyEventProviderHelper
import com.lollipop.qin1sptools.utils.bind
import kotlin.math.abs

class FloatingPanel(private val context: Context) : KeyEventListener, View.OnKeyListener {

    companion object {
        private const val PANEL_OPEN_DURATION = 100L
    }

    private val keyEventProviderHelper by lazy {
        KeyEventProviderHelper(repeatGroup = null, selfListener = this)
    }

    private val actionPanelList = ArrayList<FloatingAction>()

    private val panelBinding: FloatingRootBinding by lazy {
        LayoutInflater.from(context).bind()
    }

    private val hintBinding: FloatingHintBinding by lazy {
        LayoutInflater.from(context).bind()
    }

    private var starTime = 0L
    private var poundTime = 0L

    private val animationHelper by lazy {
        FloatingPanelAnimationHelper(
            panelBinding.root,
            panelBinding.floatingCardView,
            panelBinding.backgroundView,
            ::removePanel
        )
    }

    fun onCreate() {
        showHintView()
    }

    private fun showHintView() {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            .or(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            .or(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            .or(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            .or(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        wm.addView(
            bindKeyListener(hintBinding.root),
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                flags,
                PixelFormat.TRANSPARENT
            ).apply {
                gravity = Gravity.BOTTOM
            }
        )
    }

    private fun showPanel() {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            .or(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            .or(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            .or(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        wm.addView(
            panelBinding.root,
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                type,
                flags,
                PixelFormat.TRANSPARENT
            )
        )
        animationHelper.close(false)
        panelBinding.root.post {
            animationHelper.open(true)
        }
    }

    private fun removePanel() {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.removeView(panelBinding.root)
    }

    private fun bindKeyListener(view: View): View {
        view.setOnKeyListener(this)
        view.isFocusableInTouchMode = true
        return view
    }

    override fun onKeyDown(event: KeyEvent, repeatCount: Int): Boolean {
        if (animationHelper.isOpened) {
            return invokeByOpened(event)
        }
        if (animationHelper.isClosed) {
            return invokeByClosed(event)
        }
        return false
    }

    private fun invokeByClosed(event: KeyEvent): Boolean {
        val now = System.currentTimeMillis()
        when (event) {
            KEY_STAR -> {
                starTime = now
            }
            KEY_POUND -> {
                poundTime = now
            }
            else -> {
                starTime = 0L
                poundTime = 0L
                return false
            }
        }
        if (abs(now - starTime) < PANEL_OPEN_DURATION && abs(now - poundTime) < PANEL_OPEN_DURATION) {
            starTime = 0L
            poundTime = 0L
            showPanel()
            return true
        }
        return false
    }

    private fun closePanel() {
        animationHelper.close(true)
    }

    private fun lastPage() {
        // TODO
    }

    private fun nextPage() {
        // TODO
    }

    private fun onPageOptionClick(position: Int) {
        // TODO
    }

    private fun invokeByOpened(event: KeyEvent): Boolean {
        when (event) {
            KEY_STAR, LEFT -> {
                lastPage()
            }
            KEY_POUND, RIGHT -> {
                nextPage()
            }
            KEY_0, BACK -> {
                closePanel()
            }
            KEY_1 -> {
                onPageOptionClick(0)
            }
            KEY_2 -> {
                onPageOptionClick(1)
            }
            KEY_3 -> {
                onPageOptionClick(2)
            }
            KEY_4 -> {
                onPageOptionClick(3)
            }
            KEY_5 -> {
                onPageOptionClick(4)
            }
            KEY_6 -> {
                onPageOptionClick(5)
            }
            KEY_7 -> {
                onPageOptionClick(6)
            }
            KEY_8 -> {
                onPageOptionClick(7)
            }
            KEY_9 -> {
                onPageOptionClick(8)
            }
            else -> {
            }
        }
        return true
    }

    override fun onKey(v: View?, keyCode: Int, event: android.view.KeyEvent?): Boolean {
        val keyDownResult = keyEventProviderHelper.onKeyDown(keyCode, event)
        val keyUpResult = keyEventProviderHelper.onKeyUp(keyCode, event)
        return (keyDownResult || keyUpResult)
    }

    fun onHotKey(keyCode: Int, pkg: String) {
        val event = KeyEventProviderHelper.findKeyByCode(keyCode)
        keyEventProviderHelper.onKeyDown(event, 0)
        keyEventProviderHelper.onKeyUp(event, 0)
    }

    private class ActionPanelInfo(
        val actions: List<FloatingAction>
    )

}