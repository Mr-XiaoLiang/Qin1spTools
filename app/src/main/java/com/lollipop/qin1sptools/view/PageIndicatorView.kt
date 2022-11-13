package com.lollipop.qin1sptools.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.lollipop.qin1sptools.R

/**
 * @author lollipop
 * @date 2021/7/18 17:40
 */
class PageIndicatorView(context: Context, attributeSet: AttributeSet?, defStyle: Int) :
    AppCompatImageView(context, attributeSet, defStyle), PagedLayout.OnPageChangedListener {

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context) : this(context, null)

    private val indicatorDrawable = IndicatorDrawable()

    private var pointCount = 0

    private var selectedIndex = 0

    var color: Int
        get() {
            return indicatorDrawable.color
        }
        set(value) {
            indicatorDrawable.color = value
        }

    init {
        setImageDrawable(indicatorDrawable)

        attributeSet?.let { attrs ->
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PageIndicatorView)
            color = typedArray.getColor(R.styleable.PageIndicatorView_color, Color.WHITE)
            typedArray.recycle()
        }

        if (isInEditMode) {
            onPageChanged(2, 5)
        }
    }

    fun notifyPageChanged() {
        indicatorDrawable.updateIndicator(selectedIndex, pointCount)
    }

    fun bindToPagedLayout(layout: PagedLayout) {
        layout.onPageChanged(this)
    }

    override fun onPageChanged(index: Int, count: Int) {
        selectedIndex = index
        pointCount = count
        notifyPageChanged()
    }

    private class IndicatorDrawable : Drawable() {

        private val paint = Paint().apply {
            isAntiAlias = false
            isDither = false
            style = Paint.Style.FILL
        }
        private var currentIndex = 0
        private var pointCount = 0
        private var left = 0
        private var top = 0
        private var pointWidth = 0

        var color: Int
            get() {
                return paint.color
            }
            set(value) {
                paint.color = value
            }

        fun updateIndicator(index: Int, count: Int) {
            currentIndex = index
            pointCount = count
            updateIndicator()
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            updateIndicator()
        }

        private fun updateIndicator() {
            if (bounds.isEmpty) {
                return
            }
            top = bounds.top
            val height = bounds.height()
            val width = bounds.width()
            val stepCount = pointCount * 2
            pointWidth = if (stepCount * height > width) {
                width / stepCount
            } else {
                height
            }
            left = (width - (pointWidth * stepCount)) / 2
            invalidateSelf()
        }

        override fun draw(canvas: Canvas) {
            if (pointCount < 1) {
                return
            }
            var pointLeft = left
            val pointTop = top
            val pWidth = pointWidth
            for (index in 0 until pointCount) {
                val width = if (index == currentIndex) { pWidth * 2 } else { pWidth }
                canvas.drawRect(
                    pointLeft.toFloat(),
                    pointTop.toFloat(),
                    (pointLeft + width).toFloat(),
                    (pointTop + pWidth).toFloat(),
                    paint
                )
                pointLeft += width
                pointLeft += pWidth
            }
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

    }

}