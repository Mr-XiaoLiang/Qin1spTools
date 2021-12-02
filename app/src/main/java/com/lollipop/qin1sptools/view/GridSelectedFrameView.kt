package com.lollipop.qin1sptools.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.lollipop.qin1sptools.R
import kotlin.math.max
import kotlin.math.min

/**
 * @author lollipop
 * @date 2021/9/23 21:26
 */
class GridSelectedFrameView(
    context: Context, attr: AttributeSet?, defStyle: Int
) : androidx.appcompat.widget.AppCompatImageView(context, attr, defStyle) {

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context) : this(context, null)

    private val selectedFrameDrawable = SelectedFrameDrawable()

    var color: Int
        get() {
            return selectedFrameDrawable.color
        }
        set(value) {
            selectedFrameDrawable.color = value
        }

    var radius: Int
        get() {
            return selectedFrameDrawable.radius
        }
        set(value) {
            selectedFrameDrawable.radius = value
        }

    var strokeWidth: Float
        get() {
            return selectedFrameDrawable.strokeWidth
        }
        set(value) {
            selectedFrameDrawable.strokeWidth = value
        }

    var isShow: Boolean
        get() {
            return selectedFrameDrawable.isShow
        }
        set(value) {
            selectedFrameDrawable.isShow = value
        }

    init {
        setImageDrawable(selectedFrameDrawable)
        attr?.let { attrs ->
            val typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.GridSelectedFrameView)
            color = typedArray.getColor(
                R.styleable.GridSelectedFrameView_gsfColor,
                Color.GRAY
            )
            radius = typedArray.getDimensionPixelSize(
                R.styleable.GridSelectedFrameView_gsfRadius,
                0
            )
            strokeWidth = typedArray.getDimensionPixelSize(
                R.styleable.GridSelectedFrameView_gsfStrokeWidth,
                1
            ).toFloat()
            typedArray.recycle()
        }
    }

    private class SelectedFrameDrawable : Drawable() {

        private val paint = Paint().apply {
            isDither = true
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        var color: Int
            get() {
                return paint.color
            }
            set(value) {
                paint.color = value
            }

        var radius: Int = 0
            set(value) {
                field = value
                checkPath()
            }

        var strokeWidth: Float
            set(value) {
                paint.strokeWidth = value
            }
            get() {
                return paint.strokeWidth
            }

        var isShow: Boolean = true
            set(value) {
                field = value
                invalidateSelf()
            }

        private val framePath = Path()

        override fun onBoundsChange(bounds: Rect?) {
            super.onBoundsChange(bounds)
            checkPath()
        }

        private fun checkPath() {
            framePath.reset()
            if (bounds.isEmpty) {
                return
            }
            val offset = strokeWidth / 2
            val radiusHalf = radius / 2
            val left = bounds.left + offset
            val top = bounds.top + offset
            val right = bounds.right - strokeWidth
            val bottom = bounds.bottom - strokeWidth
            framePath.moveTo(left, max(bounds.exactCenterY(), top + radius))
            framePath.lineTo(left, top + radius)
            framePath.cubicTo(
                left, top + radiusHalf,
                left + radiusHalf, top,
                left + radius, top
            )
            framePath.lineTo(max(left + radius, bounds.exactCenterX()), top)

            framePath.moveTo(right, min(bottom - radius, bounds.exactCenterY()))
            framePath.lineTo(right, bottom - radius)
            framePath.cubicTo(
                right, bottom - radiusHalf,
                right - radiusHalf, bottom,
                right - radius, bottom
            )
            framePath.lineTo(min(bounds.exactCenterX(), right - radius), bottom)
        }

        override fun draw(canvas: Canvas) {
            if (isShow) {
                canvas.drawPath(framePath, paint)
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