package com.lollipop.qin1sptools.activity

import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
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

    protected open fun setContentView(binding: ViewBinding) {
        setContentView(binding.root)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return keyEventProviderHelper.onKeyUp(keyCode, event) || super.onKeyUp(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return keyEventProviderHelper.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        return keyEventProviderHelper.onKeyLongPress(
            keyCode,
            event
        ) || super.onKeyLongPress(keyCode, event)
    }

    override fun addKeyEventListener(listener: KeyEventListener) {
        keyEventProviderHelper.addKeyEventListener(listener)
    }

    override fun removeKeyEventListener(listener: KeyEventListener) {
        keyEventProviderHelper.removeKeyEventListener(listener)
    }

    override fun onKeyDown(event: com.lollipop.qin1sptools.event.KeyEvent): Boolean {
        return false
    }

    override fun onKeyUp(event: com.lollipop.qin1sptools.event.KeyEvent): Boolean {
        return false
    }

    override fun onKeyLongPress(event: com.lollipop.qin1sptools.event.KeyEvent): Boolean {
        return false
    }

}