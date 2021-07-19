package com.lollipop.qin1sptools.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.lollipop.qin1sptools.utils.log

/**
 * @author lollipop
 * @date 2021/7/18 17:18
 */
class PagedLayout(context: Context, attributeSet: AttributeSet?, defStyle: Int) :
    ViewGroup(context, attributeSet, defStyle) {

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context) : this(context, null)

    var currentItem: Int = 0
        set(value) {
            field = value
            offsetChildLocation()
        }

    private var pageOffset = 0

    private var onPageChangedListener: OnPageChangedListener? = null

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        layoutChild()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY)
        val childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
        for (index in 0 until childCount) {
            getChildAt(index)?.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }
        setMeasuredDimension(widthSize, heightSize)
    }

    private fun layoutChild() {
        val widthSize = width
        val heightSize = height
        val xOffset = pageOffset
        for (index in 0 until childCount) {
            val childTop = 0
            val childLeft = index * widthSize + xOffset
            getChildAt(index)?.layout(
                childLeft,
                childTop,
                childLeft + widthSize,
                childTop + heightSize
            )
        }
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        val xOffset = scrollX + direction
        if (xOffset < 0) {
            return false
        }
        if (xOffset > (childCount - 1) * width) {
            return false
        }
        return true
    }

    private fun offsetChildLocation() {
        pageOffset = currentItem * width * -1

        layoutChild()

        this.onPageChangedListener?.onPageChanged(currentItem, childCount)
    }

    fun onPageChanged(listener: OnPageChangedListener?) {
        this.onPageChangedListener = listener
    }

    fun reset() {
        currentItem = 0
    }

    fun currentPage(): View? {
        if (currentItem < 0 || childCount < 1 || currentItem >= childCount) {
            return null
        }
        return getChildAt(currentItem)
    }

    fun nextPage(): View? {
        val position = currentItem
        if (position < childCount - 1) {
            currentItem = position + 1
        }
        return currentPage()
    }

    fun lastPage(): View? {
        val position = currentItem
        if (position > 0) {
            currentItem = position - 1
        }
        return currentPage()
    }

    fun interface OnPageChangedListener {
        fun onPageChanged(index: Int, count: Int)
    }

}