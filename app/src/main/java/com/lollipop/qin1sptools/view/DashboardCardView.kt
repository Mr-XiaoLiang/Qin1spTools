package com.lollipop.qin1sptools.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

/**
 * @author lollipop
 * @date 2021/9/15 21:48
 */
class DashboardCardView(context: Context, attr: AttributeSet?, style: Int) :
    RoundTileView(context, attr, style) {

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context) : this(context, null)

    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)

    }

}