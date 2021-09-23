package com.lollipop.qin1sptools.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet

/**
 * @author lollipop
 * @date 2021/9/23 21:26
 */
class GridSelectedFrameView(
    context: Context, attr: AttributeSet?, defStyle: Int
) : androidx.appcompat.widget.AppCompatImageView(context, attr, defStyle) {

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context) : this(context, null)

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
            TODO("Not yet implemented")
        }

        override fun draw(canvas: Canvas) {
            TODO("Not yet implemented")
        }

        override fun setAlpha(alpha: Int) {
            TODO("Not yet implemented")
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            TODO("Not yet implemented")
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }
    }

}