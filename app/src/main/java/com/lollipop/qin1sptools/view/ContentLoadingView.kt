package com.lollipop.qin1sptools.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.utils.dp2px
import com.lollipop.qin1sptools.utils.task

/**
 * @author lollipop
 * @date 2021/8/1 22:36
 */
class ContentLoadingView(
    context: Context, attr: AttributeSet?, defStyle: Int
) : androidx.appcompat.widget.AppCompatImageView(context, attr, defStyle) {

    companion object {
        private const val UPDATE_SPACE = 500L
        private const val SHOWN_DELAY = 500L
        private const val MIN_KEEP = 500L
    }

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context) : this(context, null)

    private var startTime = 0L

    private var postShown = false

    private var hourglassStatus = false

    init {
        updateImage()
    }

    private val showTask = task {
        visibility = VISIBLE
        startTime = System.currentTimeMillis()
        postShown = false
        postInvalidateOnAnimation()
    }

    private val hideTask = task {
        visibility = GONE
        updateTask.cancel()
        showTask.cancel()
        postInvalidateOnAnimation()
    }

    private val updateTask = task {
        hourglassStatus = !hourglassStatus
        updateImage()
        postUpdate()
    }

    init {
        visibility = if (isInEditMode) {
            VISIBLE
        } else {
            GONE
        }
    }

    private fun updateImage() {
        setImageResource(
            if (hourglassStatus) {
                R.drawable.ic_baseline_hourglass_bottom_24
            } else {
                R.drawable.ic_baseline_hourglass_top_24
            }
        )
    }

    fun show() {
        if (visibility == VISIBLE || postShown) {
            return
        }
        if (!postShown) {
            postShown = true
            showTask.cancel()
            hideTask.cancel()
            showTask.delay(SHOWN_DELAY)
        }
        postUpdate()
    }

    fun hide() {
        postShown = false
        if (visibility != VISIBLE) {
            showTask.cancel()
            hideTask.cancel()
            updateTask.cancel()
            return
        }
        val now = System.currentTimeMillis()
        val time = now - startTime
        if (time < MIN_KEEP) {
            hideTask.delay(MIN_KEEP - time)
        } else {
            hideTask.run()
        }
    }

    private fun postUpdate() {
        updateTask.delay(UPDATE_SPACE)
    }

}