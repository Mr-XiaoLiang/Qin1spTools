package com.lollipop.qin1sptools.activity.base

import android.view.KeyEvent
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.lollipop.qin1sptools.debug.DebugVirtualKeyboard
import com.lollipop.qin1sptools.event.KeyEventListener
import com.lollipop.qin1sptools.event.KeyEventProvider
import com.lollipop.qin1sptools.event.KeyEventProviderHelper

/**
 * @author lollipop
 * @date 2021/7/17 17:06
 */
open class BaseActivity : AppCompatActivity(), KeyEventProvider, KeyEventListener {

    private val keyEventProviderHelper by lazy {
        KeyEventProviderHelper(this)
    }

    private val debugVirtualKeyboard: DebugVirtualKeyboard by lazy {
        DebugVirtualKeyboard(window.decorView as ViewGroup, object : KeyEventListener{
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

}