package com.lollipop.qin1sptools.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

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

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val widthSize = width
        val heightSize = height
        for (index in 0 until childCount) {
            val childTop = 0
            val childLeft = index * widthSize
            getChildAt(index)?.layout(
                childLeft,
                childTop,
                childLeft + widthSize,
                childTop + heightSize
            )
        }
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
        scrollTo(currentItem * width, 0)
    }

}