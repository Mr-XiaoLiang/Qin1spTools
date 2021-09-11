package com.lollipop.qin1sptools.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * @author lollipop
 * @date 2021/9/9 21:18
 */
class TileGroup(context: Context, attr: AttributeSet?, style: Int): ViewGroup(context, attr, style) {

    constructor(context: Context, attr: AttributeSet?): this(context, attr, 0)
    constructor(context: Context): this(context, null)

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
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        TODO("Not yet implemented")
    }

    class TileLayoutParams: LayoutParams {

        companion object {
            const val SPAN_FULL = -1
        }

        constructor(context: Context, attr: AttributeSet?): super(context, attr)
        constructor(width: Int, height: Int): super(width, height)
        constructor(source: LayoutParams): super(source) {
            if (source is TileLayoutParams) {
                this.spanX = source.spanX
                this.spanY = source.spanY
            }
        }

        var spanX: Int = 1
        var spanY: Int = 1

    }

}