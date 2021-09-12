package com.lollipop.qin1sptools.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.ViewGroup
import com.lollipop.qin1sptools.R

/**
 * @author lollipop
 * @date 2021/9/9 21:18
 */
class TileGroup(context: Context, attr: AttributeSet?, style: Int) :
    ViewGroup(context, attr, style) {

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context) : this(context, null)

    var spaceWidth: Int = 0
        set(value) {
            field = value
            requestLayout()
        }

    var spanXCount = 5
        set(value) {
            field = value
            requestLayout()
        }

    var spanYCount = 6
        set(value) {
            field = value
            requestLayout()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val heightSize = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        measureChild(widthSize, heightSize)
        setMeasuredDimension(
            widthSize,
            heightSize
        )
    }

    private fun measureChild(widthSize: Int, heightSize: Int) {
        val stepX = (widthSize - spaceWidth) / spanXCount
        val stepY = (heightSize - spaceWidth) / spanYCount
        val blockX = stepX - spaceWidth
        val blockY = stepY - spaceWidth
        for (index in 0 until childCount) {
            getChildAt(index)?.let { child ->
                TODO("Not yet implemented")
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        TODO("Not yet implemented")
    }

    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        if (p == null) {
            return false
        }
        if (p is TileLayoutParams) {
            return true
        }
        return false
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return TileLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return TileLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        p ?: return TileLayoutParams(context, null)
        return TileLayoutParams(p)
    }

    class TileLayoutParams : LayoutParams {

        companion object {
            const val SPAN_FULL = -1
        }

        constructor(context: Context, attr: AttributeSet?) : super(context, attr) {
            if (attr != null) {
                val a = context.obtainStyledAttributes(attr, R.styleable.TileGroup_Layout)
                spanX = a.getInt(R.styleable.TileGroup_Layout_tileSpanX, SPAN_FULL)
                spanY = a.getInt(R.styleable.TileGroup_Layout_tileSpanX, SPAN_FULL)
                a.recycle()
            }
        }
        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: LayoutParams) : super(source) {
            if (source is TileLayoutParams) {
                this.spanX = source.spanX
                this.spanY = source.spanY
            }
        }

        var spanX: Int = SPAN_FULL
        var spanY: Int = SPAN_FULL

    }

}