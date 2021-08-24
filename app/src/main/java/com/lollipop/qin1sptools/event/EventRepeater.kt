package com.lollipop.qin1sptools.event

import android.util.SparseArray
import com.lollipop.qin1sptools.utils.CommonUtil
import com.lollipop.qin1sptools.utils.log

/**
 * @author lollipop
 * @date 2021/8/1 20:48
 */
class EventRepeater(private val callback: Callback) {

    companion object {
        private val REPEAT_INTERVALS = longArrayOf(200, 400, 128, 128, 128, 128, 128)
        private const val MORE_REPEAT_INTERVAL = 80L
    }

    private val repeatTaskMap = SparseArray<RepeatTask>()

    fun onKeyDown(keyEvent: KeyEvent) {
        val repeatTask = repeatTaskMap[keyEvent.ordinal] ?: RepeatTask(keyEvent, this, callback)
        repeatTaskMap.put(keyEvent.ordinal, repeatTask)
        repeatTask.start()
    }

    fun onKeyUp(keyEvent: KeyEvent) {
        repeatTaskMap[keyEvent.ordinal]?.stop()
    }

    fun interface Callback {
        fun onRepeat(repeater: EventRepeater, event: KeyEvent, repeatCount: Int)
    }

    fun destroy() {
        val size = repeatTaskMap.size()
        for (index in 0 until size) {
            val value = repeatTaskMap.valueAt(index)
            value.stop()
        }
        repeatTaskMap.clear()
    }

    private class RepeatTask(
        private val keyEvent: KeyEvent,
        private val repeater: EventRepeater,
        private val callback: Callback
    ) : Runnable {

        private var repeatCount = 0

        override fun run() {
            log(keyEvent, repeatCount - 1)
            callback.onRepeat(repeater, keyEvent, repeatCount - 1)
            next()
        }

        fun reset() {
            stop()
            repeatCount = 0
        }

        fun start() {
            reset()
            next()
        }

        private fun next() {
            if (repeatCount < 0) {
                repeatCount = 0
            }
            val delay: Long = if (repeatCount >= REPEAT_INTERVALS.size) {
                MORE_REPEAT_INTERVAL
            } else {
                REPEAT_INTERVALS[repeatCount]
            }
            repeatCount++
            CommonUtil.delay(delay, this)
        }

        fun stop() {
            CommonUtil.remove(this)
        }

    }

}