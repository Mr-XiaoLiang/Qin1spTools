package com.lollipop.qin1sptools.view

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lollipop.qin1sptools.utils.dp2px
import com.lollipop.qin1sptools.utils.versionThen

/**
 * @author lollipop
 * @date 2021/8/20 23:04
 */
class ShapeGroup(
    context: Context,
    attributeSet: AttributeSet?,
    defStyle: Int
) : FrameLayout(context, attributeSet, defStyle) {

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context) : this(context, null)

    private var clipPath: Path? = null

    private var isClipOut = false

    private val selfPath = Path()

    private var shapePathProvider: ShapePathProvider? = null

    fun bindShapePathProvider(provider: ShapePathProvider) {
        this.shapePathProvider = provider
    }

    fun setShapePath(path: Path, isClipOut: Boolean) {
        this.clipPath = path
        this.isClipOut = isClipOut
        invalidate()
    }

    fun setRoundShape(radius: Float) {
        bindShapePathProvider(RoundShapePathProvider(radius))
    }

    fun setRoundShapeDp(radius: Int) {
        setRoundShape(radius.dp2px())
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        shapePathProvider?.let { provider ->
            selfPath.reset()
            val out = provider.getShapePath(
                paddingLeft,
                paddingTop,
                width - paddingRight,
                height - paddingBottom,
                selfPath
            )
            setShapePath(selfPath, out)
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        clipCanvas(canvas) {
            super.dispatchDraw(it)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        clipCanvas(canvas) {
            super.onDraw(it)
        }
    }

    private fun clipCanvas(canvas: Canvas?, run: (Canvas?) -> Unit) {
        val path = clipPath
        if (path == null || canvas == null) {
            run(canvas)
            return
        }
        val saveCount = canvas.save()
        if (isClipOut) {
            if (versionThen(Build.VERSION_CODES.O)) {
                canvas.clipOutPath(path)
            } else {
                canvas.clipPath(path, Region.Op.DIFFERENCE)
            }
        } else {
            canvas.clipPath(path)
        }
        run(canvas)
        canvas.restoreToCount(saveCount)
    }

    private class RoundShapePathProvider(private val radius: Float) : ShapePathProvider {

        private val tempRectF = RectF()

        override fun getShapePath(
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            path: Path
        ): Boolean {
            tempRectF.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
            path.addRoundRect(
                tempRectF,
                radius,
                radius,
                Path.Direction.CW
            )
            return false
        }

    }

    fun interface ShapePathProvider {
        /**
         * @return isClipOut
         */
        fun getShapePath(left: Int, top: Int, right: Int, bottom: Int, path: Path): Boolean
    }

}