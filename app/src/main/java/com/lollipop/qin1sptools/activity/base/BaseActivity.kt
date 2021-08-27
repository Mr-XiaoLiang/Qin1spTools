package com.lollipop.qin1sptools.activity.base

import android.view.KeyEvent
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.lollipop.qin1sptools.debug.DebugVirtualKeyboard
import com.lollipop.qin1sptools.event.*
import com.lollipop.qin1sptools.guide.Guide
import com.lollipop.qin1sptools.utils.get
import com.lollipop.qin1sptools.utils.set

/**
 * @author lollipop
 * @date 2021/7/17 17:06
 */
open class BaseActivity : AppCompatActivity(), KeyEventProvider, KeyEventListener,
    KeyEventRepeatProvider {

    companion object {
        private const val SHOW_GUIDE = "SHOW_GUIDE"
    }

    private val keyEventRepeatGroup by lazy {
        KeyEventRepeatGroup()
    }

    private val keyEventProviderHelper by lazy {
        KeyEventProviderHelper(repeatGroup = keyEventRepeatGroup, selfListener = this)
    }

    private val debugVirtualKeyboard: DebugVirtualKeyboard by lazy {
        DebugVirtualKeyboard(window.decorView as ViewGroup, object : KeyEventListener {
            override fun onKeyDown(
                event: com.lollipop.qin1sptools.event.KeyEvent,
                repeatCount: Int
            ): Boolean {
                return keyEventProviderHelper.onKeyDown(event, repeatCount)
            }

            override fun onKeyUp(
                event: com.lollipop.qin1sptools.event.KeyEvent,
                repeatCount: Int
            ): Boolean {
                return keyEventProviderHelper.onKeyUp(event, repeatCount)
            }

        })
    }

    protected open fun setContentView(binding: ViewBinding) {
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        if (DebugVirtualKeyboard.AUTO_SHOW_VIRTUAL_KEYBOARD) {
            debugVirtualKeyboard.show()
        }
        val name = this::class.java.simpleName
        val showGuideKey = name + SHOW_GUIDE
        val isShownGuide = get(showGuideKey, false)
        if (!isShownGuide) {
            set(showGuideKey, true)
            showGuide()
        }
    }

    protected fun showVirtualKeyboard() {
        DebugVirtualKeyboard.enableVirtualKeyboard()
        debugVirtualKeyboard.show()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return (keyEventProviderHelper.onKeyUp(keyCode, event)
                || super<AppCompatActivity>.onKeyUp(keyCode, event))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return (keyEventProviderHelper.onKeyDown(keyCode, event)
                || super<AppCompatActivity>.onKeyDown(keyCode, event))
    }

    override fun addKeyEventListener(listener: KeyEventListener) {
        keyEventProviderHelper.addKeyEventListener(listener)
    }

    override fun removeKeyEventListener(listener: KeyEventListener) {
        keyEventProviderHelper.removeKeyEventListener(listener)
    }

    override fun addKeyEventRepeatListener(
        listener: KeyEventRepeatListener,
        vararg keyEvents: com.lollipop.qin1sptools.event.KeyEvent
    ) {
        keyEventRepeatGroup.addKeyEventRepeatListener(listener, *keyEvents)
    }

    override fun removeKeyEventRepeatListener(
        listener: KeyEventRepeatListener,
        vararg keyEvents: com.lollipop.qin1sptools.event.KeyEvent
    ) {
        keyEventRepeatGroup.removeKeyEventRepeatListener(listener, *keyEvents)
    }

    override fun onDestroy() {
        super.onDestroy()
        keyEventProviderHelper.clear()
        keyEventRepeatGroup.clear()
    }

    protected open fun buildGuide(builder: Guide.Builder) {
        // 默认没有实现，那么就不会显示
    }

    protected fun showGuide() {
        val guide = Guide.create(this)
        buildGuide(guide)
        guide.show()
    }

}