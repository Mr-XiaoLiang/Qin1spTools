package com.lollipop.qin1sptools.view

import android.content.Context
import android.util.AttributeSet

/**
 * @author lollipop
 * @date 2021/9/20 19:50
 */
class VerticalTextView(
    context: Context, attr: AttributeSet?, defStyle: Int
): androidx.appcompat.widget.AppCompatTextView(context, attr, defStyle) {

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context) : this(context, null)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
    }

}