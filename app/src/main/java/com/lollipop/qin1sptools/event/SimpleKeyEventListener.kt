package com.lollipop.qin1sptools.event

/**
 * @author lollipop
 * @date 2021/8/27 22:35
 */
class SimpleKeyEventListener(initCallback: SimpleKeyEventListener.() -> Unit) : KeyEventListener {

    companion object {
        private const val CLICK_REPEAT_COUNT_THRESHOLD = 2
    }

    private var onKeyDownCallback: ((event: KeyEvent, repeatCount: Int) -> Boolean)? = null

    private var onKeyUpCallback: ((event: KeyEvent, repeatCount: Int) -> Boolean)? = null

    private var onClickCallback: ((event: KeyEvent) -> Unit)? = null

    private var lastDownEvent = KeyEvent.UNKNOWN

    private var lastEventRepeatCount = 0

    init {
        initCallback()
    }

    fun onKeyDown(callback: (event: KeyEvent, repeatCount: Int) -> Boolean) {
        this.onKeyDownCallback = callback
    }

    fun onKeyUp(callback: (event: KeyEvent, repeatCount: Int) -> Boolean) {
        this.onKeyUpCallback = callback
    }

    fun onClick(callback: ((event: KeyEvent) -> Unit)) {
        this.onClickCallback = callback
    }

    override fun onKeyDown(event: KeyEvent, repeatCount: Int): Boolean {
        if (onKeyDownCallback?.invoke(event, repeatCount) == true) {
            lastDownEvent = KeyEvent.UNKNOWN
            lastEventRepeatCount = 0
            return true
        }
        lastDownEvent = event
        lastEventRepeatCount = repeatCount
        return false
    }

    override fun onKeyUp(event: KeyEvent, repeatCount: Int): Boolean {
        if (onKeyUpCallback?.invoke(event, repeatCount) == true) {
            return true
        }
        val lastEvent = lastDownEvent
        val count = lastEventRepeatCount

        lastDownEvent = KeyEvent.UNKNOWN
        lastEventRepeatCount = 0

        if (lastEvent == KeyEvent.UNKNOWN || event != lastEvent) {
            return false
        }
        if (count <= CLICK_REPEAT_COUNT_THRESHOLD) {
            onClick(event)
            return true
        }
        return false
    }

    private fun onClick(event: KeyEvent) {
        onClickCallback?.invoke(event)
    }

}