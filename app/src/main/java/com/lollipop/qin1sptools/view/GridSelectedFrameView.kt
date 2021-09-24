package com.lollipop.qin1sptools.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
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

    init {
        setImageDrawable(selectedFrameDrawable)
    }

    private class SelectedFrameDrawable : Drawable() {

        private val paint = Paint().apply {
            isDither = true
            isAntiAlias = true
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
            val right = bounds.right - offset
            val bottom = bounds.bottom - offset
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
            canvas.drawPath(framePath, paint)
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