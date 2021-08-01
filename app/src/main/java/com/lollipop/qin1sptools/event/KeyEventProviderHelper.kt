package com.lollipop.qin1sptools.event

/**
 * @author lollipop
 * @date 2021/7/17 17:10
 */
class KeyEventProviderHelper(
    private val selfListener: KeyEventListener? = null
) : KeyEventProvider, KeyEventListener {

    companion object {
        fun findKeyByCode(code: Int): KeyEvent {
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

        fun keyToGameCode(key: KeyEvent): Int {
            return when(key) {
                KeyEvent.BACK -> javax.microedition.lcdui.Canvas.KEY_SOFT_RIGHT
                KeyEvent.CALL -> javax.microedition.lcdui.Canvas.KEY_SEND
                KeyEvent.OPTION -> javax.microedition.lcdui.Canvas.KEY_SOFT_LEFT
                KeyEvent.KEY_STAR -> javax.microedition.lcdui.Canvas.KEY_STAR
                KeyEvent.KEY_POUND -> javax.microedition.lcdui.Canvas.KEY_POUND
                KeyEvent.UP -> javax.microedition.lcdui.Canvas.KEY_UP
                KeyEvent.DOWN -> javax.microedition.lcdui.Canvas.KEY_DOWN
                KeyEvent.LEFT -> javax.microedition.lcdui.Canvas.KEY_LEFT
                KeyEvent.RIGHT -> javax.microedition.lcdui.Canvas.KEY_RIGHT
                KeyEvent.CENTER -> javax.microedition.lcdui.Canvas.KEY_FIRE
                KeyEvent.KEY_0 -> javax.microedition.lcdui.Canvas.KEY_NUM0
                KeyEvent.KEY_1 -> javax.microedition.lcdui.Canvas.KEY_NUM1
                KeyEvent.KEY_2 -> javax.microedition.lcdui.Canvas.KEY_NUM2
                KeyEvent.KEY_3 -> javax.microedition.lcdui.Canvas.KEY_NUM3
                KeyEvent.KEY_4 -> javax.microedition.lcdui.Canvas.KEY_NUM4
                KeyEvent.KEY_5 -> javax.microedition.lcdui.Canvas.KEY_NUM5
                KeyEvent.KEY_6 -> javax.microedition.lcdui.Canvas.KEY_NUM6
                KeyEvent.KEY_7 -> javax.microedition.lcdui.Canvas.KEY_NUM7
                KeyEvent.KEY_8 -> javax.microedition.lcdui.Canvas.KEY_NUM8
                KeyEvent.KEY_9 -> javax.microedition.lcdui.Canvas.KEY_NUM9
                else -> keyToCode(key)
            }
        }

        fun keyToCode(key: KeyEvent): Int {
            return when (key) {
                KeyEvent.KEY_0 -> android.view.KeyEvent.KEYCODE_0
                KeyEvent.KEY_1 -> android.view.KeyEvent.KEYCODE_1
                KeyEvent.KEY_2 -> android.view.KeyEvent.KEYCODE_2
                KeyEvent.KEY_3 -> android.view.KeyEvent.KEYCODE_3
                KeyEvent.KEY_4 -> android.view.KeyEvent.KEYCODE_4
                KeyEvent.KEY_5 -> android.view.KeyEvent.KEYCODE_5
                KeyEvent.KEY_6 -> android.view.KeyEvent.KEYCODE_6
                KeyEvent.KEY_7 -> android.view.KeyEvent.KEYCODE_7
                KeyEvent.KEY_8 -> android.view.KeyEvent.KEYCODE_8
                KeyEvent.KEY_9 -> android.view.KeyEvent.KEYCODE_9
                KeyEvent.KEY_STAR -> android.view.KeyEvent.KEYCODE_STAR
                KeyEvent.KEY_POUND -> android.view.KeyEvent.KEYCODE_POUND
                KeyEvent.BACK -> android.view.KeyEvent.KEYCODE_BACK
                KeyEvent.CALL -> android.view.KeyEvent.KEYCODE_CALL
                KeyEvent.OPTION -> android.view.KeyEvent.KEYCODE_MENU
                KeyEvent.UP -> android.view.KeyEvent.KEYCODE_DPAD_UP
                KeyEvent.DOWN -> android.view.KeyEvent.KEYCODE_DPAD_DOWN
                KeyEvent.LEFT -> android.view.KeyEvent.KEYCODE_DPAD_LEFT
                KeyEvent.RIGHT -> android.view.KeyEvent.KEYCODE_DPAD_RIGHT
                KeyEvent.CENTER -> android.view.KeyEvent.KEYCODE_DPAD_CENTER
                else -> android.view.KeyEvent.KEYCODE_UNKNOWN
            }
        }
    }

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

    fun onKeyUp(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (!isActive(event)) {
            return false
        }
        val key = findKeyByCode(keyCode)
        val repeatCount = event?.repeatCount ?: 0
        return onKeyUp(key, repeatCount) || selfListener?.onKeyUp(key, repeatCount) ?: false
    }

    fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (!isActive(event)) {
            return false
        }
        val key = findKeyByCode(keyCode)
        val repeatCount = event?.repeatCount ?: 0
        return onKeyDown(key, repeatCount) || selfListener?.onKeyDown(key, repeatCount) ?: false
    }

    fun onKeyLongPress(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (!isActive(event)) {
            return false
        }
        val key = findKeyByCode(keyCode)
        val repeatCount = event?.repeatCount ?: 0
        return onKeyLongPress(key, repeatCount) || selfListener?.onKeyLongPress(
            key,
            repeatCount
        ) ?: false
    }


    override fun onKeyDown(event: KeyEvent, repeatCount: Int): Boolean {
        listenerList.forEach {
            if (it.onKeyDown(event, repeatCount)) {
                return true
            }
        }
        return selfListener?.onKeyDown(event, repeatCount) ?: false
    }

    override fun onKeyUp(event: KeyEvent, repeatCount: Int): Boolean {
        listenerList.forEach {
            if (it.onKeyUp(event, repeatCount)) {
                return true
            }
        }
        return selfListener?.onKeyUp(event, repeatCount) ?: false
    }

    override fun onKeyLongPress(event: KeyEvent, repeatCount: Int): Boolean {
        listenerList.forEach {
            if (it.onKeyLongPress(event, repeatCount)) {
                return true
            }
        }
        return selfListener?.onKeyLongPress(event, repeatCount) ?: false
    }

    fun clear() {
        listenerList.clear()
    }

}