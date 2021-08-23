package com.lollipop.qin1sptools.event

import android.util.SparseBooleanArray
import androidx.core.util.remove
import java.lang.ref.WeakReference

/**
 * @author lollipop
 * @date 2021/8/15 22:38
 */
class KeyEventRepeatGroup : KeyEventRepeatProvider {

    private val listenerGroup =
        HashMap<KeyEvent, ArrayList<WeakReference<KeyEventRepeatListener>>>()

    private val consumedEventList = SparseBooleanArray()

    private fun consumeEvent(event: KeyEvent) {
        consumedEventList.put(event.ordinal, true)
    }

    private fun recycleEvent(event: KeyEvent) {
        consumedEventList.remove(event.ordinal, true)
    }

    private fun isConsumed(event: KeyEvent): Boolean {
        return consumedEventList.get(event.ordinal, false)
    }

    override fun addKeyEventRepeatListener(
        listener: KeyEventRepeatListener,
        vararg keyEvents: KeyEvent
    ) {
        val keyArray = if (keyEvents.isEmpty()) {
            KeyEvent.values()
        } else {
            keyEvents
        }
        keyArray.forEach { keyEvent ->
            val arrayList = listenerGroup[keyEvent] ?: ArrayList()
            arrayList.add(WeakReference(listener))
            listenerGroup[keyEvent] = arrayList
        }
    }

    override fun removeKeyEventRepeatListener(
        listener: KeyEventRepeatListener,
        vararg keyEvents: KeyEvent
    ) {
        val keyArray = if (keyEvents.isEmpty()) {
            KeyEvent.values()
        } else {
            keyEvents
        }
        keyArray.forEach { keyEvent ->
            val arrayList = listenerGroup[keyEvent] ?: return
            val iterator = arrayList.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                val l = next.get()
                if (l == null || l == listener) {
                    iterator.remove()
                }
            }
        }
    }

    fun onKeyDown(keyEvent: KeyEvent, repeatCount: Int): Boolean {
        val listenerList = listenerGroup[keyEvent] ?: return false
        val removedList = ArrayList<WeakReference<KeyEventRepeatListener>>()
        var intercept = false
        for (reference in listenerList) {
            val listener = reference.get()
            if (listener == null) {
                removedList.add(reference)
            } else if (listener.repeatCount == repeatCount && listener.onKeyLongPress(keyEvent)) {
                intercept = true
            }
        }
        listenerList.removeAll(removedList)
        if (intercept) {
            consumeEvent(keyEvent)
        }
        return intercept || isConsumed(keyEvent)
    }

    fun onKeyUp(keyEvent: KeyEvent): Boolean {
        val consumed = isConsumed(keyEvent)
        recycleEvent(keyEvent)
        return consumed
    }

    fun clear() {
        listenerGroup.clear()
    }

}

fun interface KeyEventRepeatListener {

    companion object {
        const val LONG_PRESS_THRESHOLD = 5
    }

    val repeatCount: Int
        get() {
            return LONG_PRESS_THRESHOLD
        }

    fun onKeyLongPress(keyEvent: KeyEvent): Boolean

}

interface KeyEventRepeatProvider {

    fun addKeyEventRepeatListener(listener: KeyEventRepeatListener, vararg keyEvents: KeyEvent)

    fun removeKeyEventRepeatListener(listener: KeyEventRepeatListener, vararg keyEvents: KeyEvent)

    fun addKeyEventRepeatListener(
        keyEvent: KeyEvent,
        listener: KeyEventRepeatListener,
    ) {
        addKeyEventRepeatListener(listener, keyEvent)
    }

    fun removeKeyEventRepeatListener(
        keyEvent: KeyEvent,
        listener: KeyEventRepeatListener,
    ) {
        removeKeyEventRepeatListener(keyEvent, listener)
    }

}