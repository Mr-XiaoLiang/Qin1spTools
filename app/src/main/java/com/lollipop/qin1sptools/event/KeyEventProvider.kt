package com.lollipop.qin1sptools.event

/**
 * @author lollipop
 * @date 2021/7/17 17:09
 */
interface KeyEventProvider {

    fun addKeyEventListener(listener: KeyEventListener)

    fun removeKeyEventListener(listener: KeyEventListener)

}