package com.lollipop.qin1sptools.utils

import android.view.View
import android.view.ViewManager

/**
 * @author lollipop
 * @date 2021/7/31 15:50
 */
class ViewToastHelper<T : View>(
    private val toastView: T,
    private val viewUpdate: (T, CharSequence) -> Unit) {

    companion object {
        private const val CLOSE_DELAY_SHORT = 2600L
        private const val CLOSE_DELAY_LONG = 3600L
    }

    var autoRemove = false

    private val toastCloseTask = task {
        if (autoRemove) {
            toastView.parent?.let {
                if (it is ViewManager) {
                    it.removeView(toastView)
                }
            }
        } else {
            toastView.visibleOrGone(false)
        }
    }

    fun show(value: CharSequence) {
        show(value, CLOSE_DELAY_SHORT)
    }

    fun showLong(value: CharSequence) {
        show(value, CLOSE_DELAY_LONG)
    }

    fun show(value: Int) {
        show(toastView.context.getString(value))
    }

    fun showLong(value: Int) {
        showLong(toastView.context.getString(value))
    }

    private fun show(value: CharSequence, delay: Long) {
        if (toastView.parent == null) {
            return
        }
        viewUpdate(toastView, value)
        toastView.visibleOrGone(true)
        toastCloseTask.cancel()
        toastCloseTask.delay(delay)
    }

}