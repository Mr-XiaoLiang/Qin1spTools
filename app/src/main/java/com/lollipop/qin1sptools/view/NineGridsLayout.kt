package com.lollipop.qin1sptools.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.lollipop.qin1sptools.R

/**
 * @author lollipop
 * @date 2021/7/18 16:35
 */
class NineGridsLayout(context: Context, attributeSet: AttributeSet?, defStyle: Int) :
    ViewGroup(context, attributeSet, defStyle) {

    var childSpace: Int = 0

    var pageIndex = -1

    var selectedChild: Int = -1
        set(value) {
            field = value
            notifyChildIndexChanged()
        }

    var selectedScale: Float = 1.1F
        set(value) {
            field = value
            notifyChildIndexChanged()
        }

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context) : this(context, null)

    init {
        attributeSet?.let { attrs ->
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NineGridsLayout)
            childSpace = typedArray.getDimensionPixelSize(R.styleable.NineGridsLayout_childSpace, 0)
            selectedScale = typedArray.getFloat(R.styleable.NineGridsLayout_selectedScale, 1.1F)
            typedArray.recycle()
        }
    }

    private fun getChildWidth(width: Int): Int {
        return (width - (childSpace * 2)) / 3
    }

    private fun getChildHeight(height: Int): Int {
        return getChildWidth(height)
    }

    private fun getWidthSize(size: Int): Int {
        return size - paddingLeft - paddingRight
    }

    private fun getHeightSize(size: Int): Int {
        return size - paddingTop - paddingBottom
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = getWidthSize(MeasureSpec.getSize(widthMeasureSpec))
        val heightSize = getHeightSize(MeasureSpec.getSize(heightMeasureSpec))
        val childWidth = getChildWidth(widthSize)
        val childHeight = getChildHeight(heightSize)
        val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY)
        val childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY)
        for (index in 0 until childCount) {
            getChildAt(index)?.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val widthSize = getWidthSize(width)
        val heightSize = getHeightSize(height)
        val childWidth = getChildWidth(widthSize)
        val childHeight = getChildHeight(heightSize)
        val top = paddingTop
        val left = paddingLeft
        val stepX = childWidth + childSpace
        val stepY = childHeight + childSpace
        for (yIndex in 0..2) {
            val childTop = yIndex * stepY + top
            for (xIndex in 0..2) {
                val childLeft = xIndex * stepX + left
                val childIndex = yIndex * 3 + xIndex
                if (childIndex >= childCount) {
                    return
                }
                getChildAt(childIndex)?.layout(
                    childLeft,
                    childTop,
                    childLeft + childWidth,
                    childTop + childHeight
                )
            }
        }
    }

    fun notifyChildIndexChanged() {
        val selected = selectedChild
        val scale = selectedScale
        for (index in 0 until childCount) {
            getChildAt(index)?.let { child ->
                if (child is NineGridsChild) {
                    child.setGridIndex(index)
                    if (!child.setSelected(index == selected, scale)) {
                        setDefaultSelectedStatus(child, index == selected, scale)
                    }
                } else {
                    setDefaultSelectedStatus(child, index == selected, scale)
                }
            }
        }
    }

    private fun setDefaultSelectedStatus(child: View, isSelected: Boolean, scale: Float) {
        child.scaleX = if (isSelected) {
            scale
        } else {
            1F
        }
        child.scaleY = if (isSelected) {
            scale
        } else {
            1F
        }
    }

}