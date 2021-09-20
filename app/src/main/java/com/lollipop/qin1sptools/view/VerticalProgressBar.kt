package com.lollipop.qin1sptools.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.lollipop.qin1sptools.R

/**
 * @author lollipop
 * @date 2021/9/20 18:54
 */
class VerticalProgressBar(
    context: Context, attr: AttributeSet?, defStyle: Int
) : androidx.appcompat.widget.AppCompatImageView(context, attr, defStyle) {

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context) : this(context, null)

    private val progressDrawable = ProgressDrawable()

    var vpBackgroundColor: Int
        set(value) {
            progressDrawable.vpBackgroundColor = value
        }
        get() {
            return progressDrawable.vpBackgroundColor
        }

    var vpProgressColor: Int
        set(value) {
            progressDrawable.vpProgressColor = value
        }
        get() {
            return progressDrawable.vpProgressColor
        }

    var max: Int
        set(value) {
            progressDrawable.max = value
        }
        get() {
            return progressDrawable.max
        }

    var progress: Int
        set(value) {
            progressDrawable.progress = value
        }
        get() {
            return progressDrawable.progress
        }

    init {
        setImageDrawable(progressDrawable)
        attr?.let { attrs ->
            val typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.VerticalProgressBar)
            vpBackgroundColor = typedArray.getColor(
                R.styleable.VerticalProgressBar_vpBackgroundColor,
                Color.GRAY
            )
            vpProgressColor = typedArray.getColor(
                R.styleable.VerticalProgressBar_vpProgressColor,
                Color.WHITE
            )
            max = typedArray.getInt(
                R.styleable.VerticalProgressBar_max,
                100
            )
            progress = typedArray.getInt(
                R.styleable.VerticalProgressBar_progress,
                0
            )
            typedArray.recycle()
        }
    }

    private class ProgressDrawable : Drawable() {

        private val paint = Paint().apply {
            isDither = true
            isAntiAlias = true
        }

        var vpBackgroundColor: Int = Color.GRAY
            set(value) {
                field = value
                invalidateSelf()
            }

        var vpProgressColor: Int = Color.WHITE
            set(value) {
                field = value
                invalidateSelf()
            }

        var max = 100
            set(value) {
                field = value
                invalidateSelf()
            }

        var progress = 0
            set(value) {
                field = value
                invalidateSelf()
            }

        override fun draw(canvas: Canvas) {
            paint.color = vpBackgroundColor
            canvas.drawRect(bounds, paint)
            paint.color = vpProgressColor
            val progressValue = progress * 1F / max
            val top = bounds.height() * (1 - progressValue)
            canvas.drawRect(
                bounds.left.toFloat(),
                top,
                bounds.right.toFloat(),
                bounds.bottom.toFloat(),
                paint
            )
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