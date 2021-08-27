package com.lollipop.qin1sptools.guide

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.activity.base.BaseActivity
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.event.KeyEventListener
import com.lollipop.qin1sptools.event.KeyEventProvider
import com.lollipop.qin1sptools.event.SimpleKeyEventListener
import com.lollipop.qin1sptools.utils.dp2px
import com.lollipop.qin1sptools.utils.getColor
import javax.microedition.util.LinkedList

/**
 * @author lollipop
 * @date 2021/8/25 22:15
 */
class Guide private constructor(private val option: Option) {

    companion object {

        private const val VIEW_INSETS_PADDING_DP = 10

        fun create(activity: BaseActivity): Builder {
            return Builder(activity, activity)
        }
    }

    private val guideTextView: TextView by lazy {
        TextView(option.activity).apply {
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
        }
    }

    private val guideSketchView: ImageView by lazy {
        ImageView(option.activity).apply {
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
    }

    private val checkButton: ImageView by lazy {
        ImageView(option.activity).apply {
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setImageResource(R.drawable.featurebar_ok)
        }
    }

    private val guideView: LinearLayout by lazy {
        LinearLayout(option.activity).apply {
            orientation = LinearLayout.VERTICAL
            val padding = VIEW_INSETS_PADDING_DP.dp2px().toInt()
            setPadding(padding, padding, padding, padding)
            addView(guideTextView, createLayoutParams())
            addView(guideSketchView, createLayoutParams().apply {
                setMargins(0, padding, 0, 0)
            })

            val checkImageButtonSize =
                context.resources.getDimensionPixelSize(R.dimen.feature_bar_size)
            addView(checkButton,
                LinearLayout.LayoutParams(checkImageButtonSize, checkImageButtonSize).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                }
            )
            setBackgroundColor(getColor(R.color.dialogBackground))
        }
    }

    private val keyEventListener: KeyEventListener by lazy {
        SimpleKeyEventListener {
            onClick {
                if (it == KeyEvent.CENTER) {
                    nextStep()
                }
            }
        }
    }

    private fun show() {
        if (option.stepList.isEmpty) {
            dismiss()
            return
        }
        if (!initView()) {
            return
        }
        initListener()
        nextStep()
    }

    private fun initListener() {
        option.keyEventProvider.addKeyEventListener(keyEventListener)
    }

    private fun initView(): Boolean {
        val guideContainer = option.activity.window.decorView
        if (guideContainer !is ViewGroup) {
            return false
        }
        guideView.parent?.let {
            if (it != guideContainer) {
                if (it is ViewManager) {
                    it.removeView(guideView)
                } else {
                    return false
                }
            } else {
                return true
            }
        }
        guideContainer.addView(
            guideView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return true
    }

    private fun nextStep() {
        if (option.stepList.isEmpty) {
            dismiss()
            return
        }
        val step = option.stepList.removeFirst()
        guideTextView.text = step.msg
        guideSketchView.setImageResource(step.sketchId)
    }

    private fun dismiss() {
        option.keyEventProvider.removeKeyEventListener(keyEventListener)
        guideView.parent?.let {
            if (it is ViewManager) {
                it.removeView(guideView)
            }
        }
    }

    private fun createLayoutParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0
        ).apply {
            weight = 1F
        }
    }

    private class Option(
        val activity: Activity,
        val keyEventProvider: KeyEventProvider,
        val stepList: LinkedList<Step>
    )

    class Builder(
        private val activity: Activity,
        private val keyEventProvider: KeyEventProvider
    ) {

        private val stepList = LinkedList<Step>()

//        fun next(msgId: Int, keyEvent: KeyEvent): Builder {
//            return next(Step(activity.getString(msgId), sketchId))
//        }

        fun next(msgId: Int, sketchId: Int): Builder {
            return next(Step(activity.getString(msgId), sketchId))
        }

        fun next(step: Step): Builder {
            stepList.addLast(step)
            return this
        }

        fun show() {
            if (stepList.isEmpty) {
                return
            }
            Guide(
                Option(
                    activity,
                    keyEventProvider,
                    stepList
                )
            ).show()
        }

    }

}