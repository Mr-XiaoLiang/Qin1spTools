package com.lollipop.qin1sptools.event

/**
 * @author lollipop
 * @date 2021/7/17 17:02
 */
interface KeyEventListener {

    fun onKeyDown(event: KeyEvent): Boolean

    fun onKeyUp(event: KeyEvent): Boolean

    fun onKeyLongPress(event: KeyEvent): Boolean

}