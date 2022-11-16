package com.lollipop.qin1sptools.floating

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import kotlin.math.abs

class FloatingPanelAnimationHelper(
    private val panelRoot: View,
    private val bottomSheetPanel: View,
    private val backgroundView: View,
    private val onClosed: () -> Unit
) : ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

    companion object {
        private const val DURATION = 200L

        private const val PROGRESS_OPENED = 1F
        private const val PROGRESS_CLOSED = 0F

        private const val THRESHOLD_OPENED = 0.998F

        private const val THRESHOLD_CLOSED = 0.002F
    }

    private var progress = 0F

    private val valueAnimator = ValueAnimator().apply {
        addUpdateListener(this@FloatingPanelAnimationHelper)
        addListener(this@FloatingPanelAnimationHelper)
    }

    val isOpened: Boolean
        get() {
            return progress > THRESHOLD_OPENED
        }

    val isClosed: Boolean
        get() {
            return progress < THRESHOLD_CLOSED
        }

    fun open(animation: Boolean = true) {
        doAnimation(true, animation)
    }

    fun close(animation: Boolean = true) {
        doAnimation(false, animation)
    }

    private fun doAnimation(isOpen: Boolean, animation: Boolean) {
        valueAnimator.cancel()
        val end = if (isOpen) {
            PROGRESS_OPENED
        } else {
            PROGRESS_CLOSED
        }
        if (!animation) {
            progress = end
            onProgressChanged()
            onAnimationEnd()
            return
        }
        valueAnimator.duration = getDuration(end)
        valueAnimator.setFloatValues(progress, end)
        valueAnimator.start()
    }

    private fun getDuration(end: Float): Long {
        val offset = abs(progress - end) * 1F
        val max = abs(PROGRESS_OPENED - PROGRESS_CLOSED)
        return (offset / max * DURATION).toLong()
    }

    private fun onProgressChanged() {
        val pro = progress
        backgroundView.alpha = pro
        bottomSheetPanel.translationY = (1 - pro) * bottomSheetPanel.height
    }

    private fun onAnimationStart() {
        panelRoot.isVisible = true
        bottomSheetPanel.isVisible = true
        backgroundView.isVisible = true
    }

    private fun onAnimationEnd() {
        // 结束的时候，需要把不必要的View隐藏了
        if (progress < THRESHOLD_CLOSED) {
            panelRoot.isInvisible = true
            bottomSheetPanel.isInvisible = true
            backgroundView.isInvisible = true
            onClosed()
        }
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        if (animation === valueAnimator) {
            val animatedValue = animation.animatedValue
            if (animatedValue is Float) {
                progress = animatedValue
                onProgressChanged()
            }
        }
    }

    override fun onAnimationStart(animation: Animator) {
        if (animation === valueAnimator) {
            onAnimationStart()
        }
    }

    override fun onAnimationEnd(animation: Animator) {
        if (animation === valueAnimator) {
            onAnimationEnd()
        }
    }

    override fun onAnimationCancel(animation: Animator) {
    }

    override fun onAnimationRepeat(animation: Animator) {
    }

}