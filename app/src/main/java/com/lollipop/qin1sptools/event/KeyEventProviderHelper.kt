package com.lollipop.qin1sptools.event

/**
 * @author lollipop
 * @date 2021/7/17 17:10
 */
class KeyEventProviderHelper(
    private val selfListener: KeyEventListener? = null
) : KeyEventProvider, KeyEventListener {

    private val listenerList = ArrayList<KeyEventListener>()

    override fun addKeyEventListener(listener: KeyEventListener) {
        listenerList.add(listener)
    }

    override fun removeKeyEventListener(listener: KeyEventListener) {
        listenerList.remove(listener)
    }
    private fun isActive(event: android.view.KeyEvent?): Boolean {
        event ?: return false
        return !event.isCanceled
    }

    private fun findKeyByCode(code: Int): KeyEvent {
        return when (code) {
            android.view.KeyEvent.KEYCODE_0 -> KeyEvent.KEY_0
            android.view.KeyEvent.KEYCODE_1 -> KeyEvent.KEY_1
            android.view.KeyEvent.KEYCODE_2 -> KeyEvent.KEY_2
            android.view.KeyEvent.KEYCODE_3 -> KeyEvent.KEY_3
            android.view.KeyEvent.KEYCODE_4 -> KeyEvent.KEY_4
            android.view.KeyEvent.KEYCODE_5 -> KeyEvent.KEY_5
            android.view.KeyEvent.KEYCODE_6 -> KeyEvent.KEY_6
            android.view.KeyEvent.KEYCODE_7 -> KeyEvent.KEY_7
            android.view.KeyEvent.KEYCODE_8 -> KeyEvent.KEY_8
            android.view.KeyEvent.KEYCODE_9 -> KeyEvent.KEY_9
            android.view.KeyEvent.KEYCODE_STAR -> KeyEvent.KEY_STAR
            android.view.KeyEvent.KEYCODE_POUND -> KeyEvent.KEY_POUND
            android.view.KeyEvent.KEYCODE_BACK -> KeyEvent.BACK
            android.view.KeyEvent.KEYCODE_CALL -> KeyEvent.CALL
            android.view.KeyEvent.KEYCODE_MENU -> KeyEvent.OPTION
            android.view.KeyEvent.KEYCODE_DPAD_UP -> KeyEvent.UP
            android.view.KeyEvent.KEYCODE_DPAD_DOWN -> KeyEvent.DOWN
            android.view.KeyEvent.KEYCODE_DPAD_LEFT -> KeyEvent.LEFT
            android.view.KeyEvent.KEYCODE_DPAD_RIGHT -> KeyEvent.RIGHT
            android.view.KeyEvent.KEYCODE_DPAD_CENTER -> KeyEvent.CENTER
            else -> KeyEvent.UNKNOWN
        }
    }

    fun onKeyUp(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (!isActive(event)) {
            return false
        }
        val key = findKeyByCode(keyCode)
        return onKeyUp(key)
    }

    fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (!isActive(event)) {
            return false
        }
        val key = findKeyByCode(keyCode)
        return onKeyDown(key) || selfListener?.onKeyDown(key) ?: false
    }

    fun onKeyLongPress(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (!isActive(event)) {
            return false
        }
        val key = findKeyByCode(keyCode)
        return onKeyLongPress(key) || selfListener?.onKeyLongPress(key) ?: false
    }


    override fun onKeyDown(event: KeyEvent): Boolean {
        listenerList.forEach {
            if (it.onKeyDown(event)) {
                return true
            }
        }
        return selfListener?.onKeyLongPress(event) ?: false
    }

    override fun onKeyUp(event: KeyEvent): Boolean {
        listenerList.forEach {
            if (it.onKeyUp(event)) {
                return true
            }
        }
        return selfListener?.onKeyUp(event) ?: false
    }

    override fun onKeyLongPress(event: KeyEvent): Boolean {
        listenerList.forEach {
            if (it.onKeyLongPress(event)) {
                return true
            }
        }
        return selfListener?.onKeyLongPress(event) ?: false
    }

    fun clear() {
        listenerList.clear()
    }

}