package com.lollipop.qin1sptools.floating

import android.content.Context
import android.view.View
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.event.KeyEventListener
import com.lollipop.qin1sptools.event.KeyEventProviderHelper

class FloatingPanel(private val context: Context) : KeyEventListener, View.OnKeyListener {

    private val keyEventProviderHelper by lazy {
        KeyEventProviderHelper(repeatGroup = null, selfListener = this)
    }

    private val actionPanelList = ArrayList<FloatingAction>()

    fun onCreate() {

    }

    init {

    }

    override fun onKeyDown(event: KeyEvent, repeatCount: Int): Boolean {
        return false
    }

    override fun onKey(v: View?, keyCode: Int, event: android.view.KeyEvent?): Boolean {
        return (keyEventProviderHelper.onKeyDown(keyCode, event)
                || keyEventProviderHelper.onKeyUp(keyCode, event))
    }

    private class ActionPanelInfo(
        val actions: List<FloatingAction>
    )

}