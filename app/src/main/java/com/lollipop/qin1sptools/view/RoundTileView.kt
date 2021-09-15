package com.lollipop.qin1sptools.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lollipop.qin1sptools.R


/**
 * @author lollipop
 * @date 2021/9/14 22:08
 */
open class RoundTileView(context: Context, attr: AttributeSet?, style: Int) :
    FrameLayout(context, attr, style) {

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context) : this(context, null)

    private val corner = Corner()

    private val clipPath = Path()

    private val clipInnerPath = Path()

    var onlyClipChild = false
        set(value) {
            field = value
            invalidate()
        }

    init {
        if (attr != null) {
            val a = context.obtainStyledAttributes(attr, R.styleable.RoundTileView)
            val allSize = a.getDimensionPixelSize(R.styleable.RoundTileView_tileCorner, 0)
            corner.leftTop =
                a.getDimensionPixelSize(R.styleable.RoundTileView_tileCornerLeftTop, allSize)
            corner.rightTop =
                a.getDimensionPixelSize(R.styleable.RoundTileView_tileCornerRightTop, allSize)
            corner.rightBottom =
                a.getDimensionPixelSize(R.styleable.RoundTileView_tileCornerRightBottom, allSize)
            corner.leftBottom =
                a.getDimensionPixelSize(R.styleable.RoundTileView_tileCornerLeftBottom, allSize)
            onlyClipChild = a.getBoolean(R.styleable.RoundTileView_onlyClipChild, false)
            a.recycle()
        }
    }

    fun setTileCorner(size: Int) {
        setTileCorner(size, size, size, size)
    }

    fun setTileCorner(leftTop: Int, rightTop: Int, rightBottom: Int, leftBottom: Int) {
        corner.leftTop = leftTop
        corner.rightTop = rightTop
        corner.rightBottom = rightBottom
        corner.leftBottom = leftBottom
        checkPath()
        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        checkPath()
    }

    override fun draw(canvas: Canvas?) {
        if (onlyClipChild || canvas == null || !corner.enable) {
            super.draw(canvas)
            return
        }
        val saveCount = canvas.save()
        canvas.clipPath(clipPath)
        canvas.drawFilter = PaintFlagsDrawFilter(
            0,
            Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG
        )
        super.draw(canvas)
        canvas.restoreToCount(saveCount)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (canvas == null || !corner.enable) {
            super.dispatchDraw(canvas)
            return
        }
        val saveCount = canvas.save()
        canvas.clipPath(clipInnerPath)
        canvas.drawFilter = PaintFlagsDrawFilter(
            0,
            Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG
        )
        super.dispatchDraw(canvas)
        canvas.restoreToCount(saveCount)
    }

    private fun checkPath() {
        val widthSize = width
        val heightSize = height
        clipPath.reset()
        clipInnerPath.reset()
        if (widthSize < 4 || heightSize < 4) {
            return
        }
        val tempRect = RectF()
        tempRect.set(0F, 0F, widthSize.toFloat(), heightSize.toFloat())
        clipPath.addRoundRect(tempRect, corner.getRadii(), Path.Direction.CW)
        if (needInnerClip()) {
            tempRect.set(
                tempRect.left + paddingLeft,
                tempRect.top + paddingTop,
                tempRect.right - paddingRight,
                tempRect.bottom - paddingBottom
            )
            clipInnerPath.addRoundRect(
                tempRect,
                corner.getRadii(paddingLeft, paddingTop, paddingRight, paddingBottom),
                Path.Direction.CW
            )
        } else {
            clipInnerPath.addPath(clipPath)
        }
    }

    private fun needInnerClip(): Boolean {
        return paddingLeft > 0 || paddingTop > 0 || paddingRight > 0 || paddingBottom > 0
    }

    private data class Corner(
        var leftTop: Int = 0,
        var rightTop: Int = 0,
        var leftBottom: Int = 0,
        var rightBottom: Int = 0,
    ) {

        fun getRadii(
            insetLeft: Int = 0,
            insetTop: Int = 0,
            insetRight: Int = 0,
            insetBottom: Int = 0
        ): FloatArray {
            val lt = leftTop.toFloat()
            val rt = rightTop.toFloat()
            val lb = leftBottom.toFloat()
            val rb = rightBottom.toFloat()
            return floatArrayOf(
                lt - insetLeft, lt - insetTop,
                rt - insetRight, rt - insetTop,
                rb - insetRight, rb - insetBottom,
                lb - insetLeft, lb - insetBottom
            )
        }

        val enable: Boolean
            get() {
                return leftTop > 0 || rightTop > 0 || leftBottom > 0 || rightBottom > 0
            }

    }

}