package com.lollipop.qin1sptools.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.utils.dp2px
import com.lollipop.qin1sptools.utils.task

/**
 * @author lollipop
 * @date 2021/8/1 22:36
 */
class ContentLoadingProgressBar(
    context: Context, attr: AttributeSet?, defStyle: Int
) : androidx.appcompat.widget.AppCompatImageView(context, attr, defStyle) {

    companion object {
        private const val DURATION = 1000L
        private const val SHOWN_DELAY = 500L
        private const val MIN_KEEP = 500L
    }

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context) : this(context, null)

    private val progressDrawable = ProgressDrawable()

    private var progress = 0F

    private var startTime = 0L

    private var postShown = false

    private val showTask = task {
        visibility = VISIBLE
        startTime = System.currentTimeMillis()
        postShown = false
        postInvalidateOnAnimation()
    }

    private val hideTask = task {
        visibility = GONE
        postInvalidateOnAnimation()
    }

    init {
        setImageDrawable(progressDrawable)
        attr?.let { attrs ->
            val typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.ContentLoadingProgressBar)
            color =
                typedArray.getColor(R.styleable.ContentLoadingProgressBar_loadingColor, Color.WHITE)
            strokeWidth = typedArray.getDimensionPixelSize(
                R.styleable.ContentLoadingProgressBar_progressBarWidth,
                2F.dp2px().toInt()
            ).toFloat()
            typedArray.recycle()
        }
        visibility = if (isInEditMode) {
            VISIBLE
        } else {
            GONE
        }
    }

    var color: Int
        get() {
            return progressDrawable.color
        }
        set(value) {
            progressDrawable.color = value
        }

    var strokeWidth: Float
        get() {
            return progressDrawable.strokeWidth
        }
        set(value) {
            progressDrawable.strokeWidth = value
        }

    fun show() {
        if (visibility == VISIBLE || postShown) {
            return
        }
        if (!postShown) {
            postShown = true
            showTask.cancel()
            hideTask.cancel()
            showTask.delay(SHOWN_DELAY)
        }
    }

    fun hide() {
        postShown = false
        if (visibility != VISIBLE) {
            showTask.cancel()
            hideTask.cancel()
            return
        }
        val now = System.currentTimeMillis()
        val time = now - startTime
        if (time < MIN_KEEP) {
            hideTask.delay(MIN_KEEP - time)
        } else {
            hideTask.run()
        }
    }

    override fun computeScroll() {
        super.computeScroll()
        val now = System.currentTimeMillis()
        val time = (now - startTime) % DURATION
        progress = time * 1F / DURATION
        progressDrawable.progress = (progress * 360).toInt()
        if (visibility == VISIBLE) {
            postInvalidateOnAnimation()
        }
    }

    private class ProgressDrawable : Drawable() {

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = Color.WHITE
            style = Paint.Style.STROKE
        }

        var color: Int
            get() {
                return paint.color
            }
            set(value) {
                paint.color = value
            }

        var strokeWidth: Float
            get() {
                return paint.strokeWidth
            }
            set(value) {
                paint.strokeWidth = value
            }

        var progress = 0
            set(value) {
                field = value % 360
                invalidateSelf()
            }

        private val boundsF = RectF()

        override fun draw(canvas: Canvas) {
            canvas.drawArc(boundsF, progress.toFloat(), 270F, false, paint)
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            boundsF.set(bounds)
            invalidateSelf()
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