package com.lollipop.qin1sptools.event

/**
 * @author lollipop
 * @date 2021/7/17 17:02
 */
interface KeyEventListener {

    fun onKeyDown(event: KeyEvent, repeatCount: Int): Boolean {
        return onKeyDown(event)
    }

    fun onKeyUp(event: KeyEvent, repeatCount: Int): Boolean {
        return onKeyUp(event)
    }

    fun onKeyLongPress(event: KeyEvent, repeatCount: Int): Boolean {
        return onKeyLongPress(event)
    }

    fun onKeyDown(event: KeyEvent): Boolean {
        return false
    }

    fun onKeyUp(event: KeyEvent): Boolean {
        return false
    }

    fun onKeyLongPress(event: KeyEvent): Boolean {
        return false
    }

}