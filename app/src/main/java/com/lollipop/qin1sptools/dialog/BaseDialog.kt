package com.lollipop.qin1sptools.dialog

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.databinding.DialogBaseBinding
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.event.KeyEventListener
import com.lollipop.qin1sptools.event.KeyEventProvider
import com.lollipop.qin1sptools.utils.lazyBind
import com.lollipop.qin1sptools.utils.visibleOrGone

/**
 * @author lollipop
 * @date 2021/8/14 20:17
 */
abstract class BaseDialog constructor(private val option: Option) {

    private val binding: DialogBaseBinding by option.container.lazyBind(true)

    private var enterToDismiss = false

    private var lastDownEvent: KeyEvent = KeyEvent.UNKNOWN

    protected val context: Context
        get() {
            return option.container.context
        }

    private val onKeyEventListener = object : KeyEventListener {
        override fun onKeyDown(event: KeyEvent, repeatCount: Int): Boolean {
            if (repeatCount == 0) {
                lastDownEvent = event
            }
            return true
        }

        override fun onKeyUp(event: KeyEvent, repeatCount: Int): Boolean {
            if (event != lastDownEvent) {
                return true
            }
            option.container.post {
                onKeyEvent(event)
            }
            return true
        }
    }

    val isShow: Boolean
        get() {
            return binding.root.isShown
        }

    fun show() {
        bindButton()
        bindKeyEvent()
        bindContent()
        option.container.addView(
            binding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        binding.root.isInvisible = true
        binding.root.post {
            binding.root.isVisible = true
            binding.backgroundView.alpha = 0F
            binding.dialogView.translationY = binding.dialogView.height.toFloat()
            viewAnimate(binding.backgroundView) {
                cancel()
                alpha(1F)
                start()
            }
            viewAnimate(binding.dialogView) {
                cancel()
                translationY(0F)
                start()
            }
        }
    }

    private fun bindContent() {
        binding.titleView.text = option.title.defaultValue {
            binding.titleView.context.getString(R.string.alert)
        }
        binding.contentGroup.removeAllViews()
        binding.contentGroup.addView(
            onBindContent(),
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    abstract fun onBindContent(): View

    private fun bindButton() {
        if (option.isEmptyCallback()) {
            enterToDismiss = true
            binding.leftButton.visibleOrGone(true) {
                setText(R.string.confirm)
            }
            binding.rightButton.visibleOrGone(false)
        } else {
            enterToDismiss = false
            binding.leftButton.visibleOrGone(option.isLeftButtonActive()) {
                text = option.leftButtonName
            }
            binding.rightButton.visibleOrGone(option.isRightButtonActive()) {
                text = option.rightButtonName
            }
        }
        binding.leftButton.setOnClickListener {
            option.onLeftClick?.onClick(this)
        }
        binding.rightButton.setOnClickListener {
            option.onRightClick?.onClick(this)
        }
    }

    private fun bindKeyEvent() {
        option.keyEventProvider.addKeyEventListener(onKeyEventListener)
    }

    protected open fun onKeyEvent(event: KeyEvent) {
        when (event) {
            KeyEvent.CENTER -> if (enterToDismiss) {
                dismiss()
            }
            KeyEvent.OPTION -> if (option.isLeftButtonActive()) {
                option.onLeftClick?.onClick(this)
            }
            KeyEvent.BACK -> if (option.isRightButtonActive()) {
                option.onRightClick?.onClick(this)
            }
            else -> {
            }
        }
    }

    fun dismiss() {
        option.keyEventProvider.removeKeyEventListener(onKeyEventListener)
        option.onDismissListener?.onDismiss()
        viewAnimate(binding.backgroundView) {
            cancel()
            alpha(0F)
            start()
        }
        viewAnimate(binding.dialogView) {
            cancel()
            translationY(binding.dialogView.height.toFloat())
            setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator?) {
                    super.onAnimationCancel(animation)
                    setListener(null)
                }

                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    setListener(null)
                    removeView()
                }
            })
            start()
        }
    }

    private fun viewAnimate(view: View, run: ViewPropertyAnimator.() -> Unit) {
        with(view.animate()) {
            duration = 150
            run()
        }
    }

    private fun removeView() {
        option.container.removeView(binding.root)
    }

    protected fun CharSequence.defaultValue(run: () -> CharSequence): CharSequence {
        if (this.isEmpty()) {
            return run()
        }
        return this
    }

    fun interface OnClickListener {
        fun onClick(dialog: BaseDialog)
    }

    fun interface OnDismissListener {
        fun onDismiss()
    }

    open class Option(
        val container: ViewGroup,
        val keyEventProvider: KeyEventProvider,
    ) {

        var onLeftClick: OnClickListener? = null
            private set
        var onRightClick: OnClickListener? = null
            private set
        var leftButtonName: CharSequence = ""
            private set
        var rightButtonName: CharSequence = ""
            private set
        var title: CharSequence = ""
        var onDismissListener: OnDismissListener? = null
            private set

        fun setLeftButton(name: CharSequence, listener: OnClickListener) {
            leftButtonName = name
            onLeftClick = listener
        }

        fun setLeftButton(resId: Int, listener: OnClickListener) {
            setLeftButton(getString(resId), listener)
        }

        fun setRightButton(name: CharSequence, listener: OnClickListener) {
            rightButtonName = name
            onRightClick = listener
        }

        fun setRightButton(resId: Int, listener: OnClickListener) {
            setRightButton(getString(resId), listener)
        }

        fun setTitle(resId: Int) {
            title = getString(resId)
        }

        protected fun getString(resId: Int): String {
            return container.context.getString(resId)
        }

        fun isEmptyCallback(): Boolean {
            return !isLeftButtonActive() && !isRightButtonActive()
        }

        fun isLeftButtonActive(): Boolean {
            return leftButtonName.isNotEmpty() && onLeftClick != null
        }

        fun isRightButtonActive(): Boolean {
            return rightButtonName.isNotEmpty() && onRightClick != null
        }

        fun onDismiss(listener: OnDismissListener) {
            this.onDismissListener = listener
        }
    }

}