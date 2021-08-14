package com.lollipop.qin1sptools.utils

import androidx.recyclerview.widget.RecyclerView

/**
 * @author lollipop
 * @date 2021/8/14 21:30
 */
class SimpleListHelper(
    private val viewProvider: () -> RecyclerView,
    private val dataCountProvider: () -> Int,
    private val onSelectedIndexChanged: () -> Unit
) {

    var selectedPosition = 0
        private set

    fun resetSelected() {
        selectedPosition = if (dataCountProvider() < 1) {
            -1
        } else {
            0
        }
    }

    fun resetAnimation(
        changeDuration: Long = 150L,
        addDuration: Long = 150L,
        moveDuration: Long = 150L,
        removeDuration: Long = 150L,
    ) {
        viewProvider().itemAnimator?.let { animator ->
            animator.changeDuration = changeDuration
            animator.addDuration = addDuration
            animator.moveDuration = moveDuration
            animator.removeDuration = removeDuration
        }
    }

    fun selectNext(): Boolean {
        val maxCount = dataCountProvider()
        if (selectedPosition < maxCount - 1) {
            val lastIndex = selectedPosition
            selectedPosition++
            val recyclerView = viewProvider()
            recyclerView.adapter?.notifyItemRangeChanged(lastIndex, 2)
            recyclerView.scrollToPosition(selectedPosition)
            onSelectedIndexChanged()
            return true
        }
        return false
    }

    fun selectLast(): Boolean {
        if (selectedPosition > 0) {
            selectedPosition--
            val recyclerView = viewProvider()
            recyclerView.adapter?.notifyItemRangeChanged(selectedPosition, 2)
            recyclerView.scrollToPosition(selectedPosition)
            onSelectedIndexChanged()
            return true
        }
        return false
    }

    fun selectedTo(position: Int) {
        val dataCount = dataCountProvider()
        if (dataCount < 1) {
            selectedPosition = -1
            return
        }
        selectedPosition = position.range(0, dataCount - 1)
        viewProvider().scrollToPosition(selectedPosition)
    }

}