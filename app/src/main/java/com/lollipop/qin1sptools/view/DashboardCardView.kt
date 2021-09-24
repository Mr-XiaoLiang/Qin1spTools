package com.lollipop.qin1sptools.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import com.lollipop.qin1sptools.R
import kotlin.math.min

/**
 * @author lollipop
 * @date 2021/9/15 21:48
 */
class DashboardCardView(context: Context, attr: AttributeSet?, style: Int) :
    RoundTileView(context, attr, style), TileGroup.TileMark {

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context) : this(context, null)

    override var markValue: String = ""
        set(value) {
            field = value
            invalidate()
        }

    private val markPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            isDither = true
        }
    }

    var markColor: Int = Color.WHITE
        set(value) {
            field = value
            invalidate()
        }

    var markBackgroundColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            invalidate()
        }

    var markTextSize: Float
        get() {
            return markPaint.textSize
        }
        set(value) {
            markPaint.textSize = value
            invalidate()
        }

    private val markPadding = Rect()

    private val markBackgroundPath = Path()

    private val markBounds = RectF()

    init {
        if (attr != null) {
            val a = context.obtainStyledAttributes(attr, R.styleable.DashboardCardView)
            val textSize = a.getDimensionPixelSize(
                R.styleable.DashboardCardView_markTextSize, 0
            )
            if (textSize == 0) {
                setMarkTextSize(12F)
            } else {
                markTextSize = textSize.toFloat()
            }
            markBackgroundColor = a.getColor(
                R.styleable.DashboardCardView_markBackgroundColor, Color.TRANSPARENT
            )
            markColor = a.getColor(
                R.styleable.DashboardCardView_markTextColor, Color.WHITE
            )
            markValue = a.getString(
                R.styleable.DashboardCardView_markTextValue
            ) ?: ""

            val allPadding = a.getDimensionPixelSize(
                R.styleable.DashboardCardView_markPadding, 0
            )
            markPadding.left = a.getDimensionPixelSize(
                R.styleable.DashboardCardView_markPaddingLeft, allPadding
            )
            markPadding.top = a.getDimensionPixelSize(
                R.styleable.DashboardCardView_markPaddingTop, allPadding
            )
            markPadding.right = a.getDimensionPixelSize(
                R.styleable.DashboardCardView_markPaddingRight, allPadding
            )
            markPadding.bottom = a.getDimensionPixelSize(
                R.styleable.DashboardCardView_markPaddingBottom, allPadding
            )
            a.recycle()
        }
    }

    fun setMarkTextSize(size: Float, unit: Int = TypedValue.COMPLEX_UNIT_SP) {
        markTextSize = TypedValue.applyDimension(unit, size, resources.displayMetrics)
    }

    fun setMarkPadding(callback: Rect.() -> Unit) {
        callback(markPadding)
        invalidate()
    }

    protected override fun onCornerChanged() {
        super.onCornerChanged()
        checkMarkBackgroundPath()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        checkMarkBackgroundPath()
    }

    private fun checkMarkBackgroundPath() {
        markBackgroundPath.reset()
        if (markBackgroundColor == Color.TRANSPARENT) {
            return
        }
        if (markValue.isEmpty()) {
            return
        }
        val widthSize = width
        val heightSize = height
        if (widthSize < 1 || heightSize < 1) {
            return
        }
        val textWidth = markPaint.measureText(markValue) + markPadding.left + markPadding.right
        val textHeight = markPaint.textSize + markPadding.top + markPadding.bottom
        val backgroundWidth = min(textWidth.toInt(), widthSize)
        val backgroundHeight = min(textHeight.toInt(), heightSize)
        val markRight = widthSize.toFloat()
        val markBottom = heightSize.toFloat()
        markBounds.set(
            markRight - backgroundWidth,
            markBottom - backgroundHeight,
            markRight,
            markBottom
        )
        markBackgroundPath.addRoundRect(
            markBounds,
            getMarkRadii(),
            Path.Direction.CW
        )
    }

    private fun getMarkRadii(): FloatArray {
        val cornerRadii = getCornerRadii(false)
        // 右上角
        cornerRadii[2] = 0F
        cornerRadii[3] = 0F
        // 左下角
        cornerRadii[6] = 0F
        cornerRadii[7] = 0F
        return cornerRadii
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?.let {
            drawText(it)
        }
    }

    private fun drawText(canvas: Canvas) {
        if (markValue.isEmpty()) {
            return
        }
        if (markBackgroundColor != Color.TRANSPARENT) {
            markPaint.color = markBackgroundColor
            markPaint.style = Paint.Style.FILL
            canvas.drawPath(markBackgroundPath, markPaint)
        }
        markPaint.color = markColor
        markPaint.textAlign = Paint.Align.CENTER
        val fontMetrics = markPaint.fontMetrics
        val fontYOffset = ((fontMetrics.bottom - fontMetrics.top) / 2) - fontMetrics.bottom
        canvas.drawText(
            markValue,
            markBounds.centerX(),
            markBounds.centerY() + fontYOffset,
            markPaint
        )
    }

}