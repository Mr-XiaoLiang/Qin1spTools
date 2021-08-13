package com.lollipop.qin1sptools.dialog

import android.text.method.ScrollingMovementMethod
import android.view.ViewGroup
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.activity.base.BaseActivity
import com.lollipop.qin1sptools.databinding.DialogMessageBinding
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.event.KeyEventListener
import com.lollipop.qin1sptools.event.KeyEventProvider
import com.lollipop.qin1sptools.utils.visibleOrGone
import com.lollipop.qin1sptools.utils.withThis
import java.lang.RuntimeException

/**
 * @author lollipop
 * @date 2021/8/10 22:32
 */
class MessageDialog private constructor(private val option: Option) {

    companion object {
        fun create(activity: BaseActivity): Builder {
            return Builder(activity.window.decorView as ViewGroup).setKeyEventProvider(activity)
        }
    }

    private val binding: DialogMessageBinding by option.container.withThis(true)

    private var enterToDismiss = false

    private var lastDownEvent: KeyEvent = KeyEvent.UNKNOWN

    private val onKeyEventListener = object : KeyEventListener {

        override fun onKeyDown(event: KeyEvent, repeatCount: Int): Boolean {
            if (repeatCount == 0) {
                lastDownEvent = event
            }
            return true
        }

        override fun onKeyUp(event: KeyEvent): Boolean {
            if (event != lastDownEvent) {
                return true
            }
            option.container.post {
                onKeyEvent(event)
            }
            return true
        }
    }

    private fun show() {
        bindContent()
        bindButton()
        bindKeyEvent()
    }

    private fun bindContent() {
        binding.titleView.text = option.title.defaultValue {
            binding.titleView.context.getString(R.string.alert)
        }
        binding.messageView.visibleOrGone(option.message.isNotEmpty()) {
            text = option.message
            movementMethod = ScrollingMovementMethod.getInstance()
        }
    }

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

    private fun onKeyEvent(event: KeyEvent) {
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
            KeyEvent.UP -> {
                if (binding.messageView.canScrollVertically(-1)) {
                    binding.messageView.scrollBy(0, binding.messageView.lineHeight * -1)
                }
            }
            KeyEvent.DOWN -> {
                if (binding.messageView.canScrollVertically(1)) {
                    binding.messageView.scrollBy(0, binding.messageView.lineHeight)
                }
            }
            else -> {
            }
        }
    }

    fun dismiss() {
        option.keyEventProvider.removeKeyEventListener(onKeyEventListener)
        option.container.removeView(binding.root)
        option.onDismissListener?.onDismiss()
    }

    private fun CharSequence.defaultValue(run: () -> CharSequence): CharSequence {
        if (this.isEmpty()) {
            return run()
        }
        return this
    }

    class Builder(private val container: ViewGroup) {

        private var keyEventProvider: KeyEventProvider? = null
        private var onLeftClick: OnClickListener? = null
        private var onRightClick: OnClickListener? = null
        private var leftButtonName: CharSequence = ""
        private var rightButtonName: CharSequence = ""
        private var title: CharSequence = ""
        private var message: CharSequence = ""
        private var onDismissListener: OnDismissListener? = null

        fun setKeyEventProvider(provider: KeyEventProvider): Builder {
            this.keyEventProvider = provider
            return this
        }

        fun setLeftButton(name: CharSequence, listener: OnClickListener): Builder {
            leftButtonName = name
            onLeftClick = listener
            return this
        }

        fun setLeftButton(resId: Int, listener: OnClickListener): Builder {
            return setLeftButton(getString(resId), listener)
        }

        fun setRightButton(name: CharSequence, listener: OnClickListener): Builder {
            rightButtonName = name
            onRightClick = listener
            return this
        }

        fun setRightButton(resId: Int, listener: OnClickListener): Builder {
            return setRightButton(getString(resId), listener)
        }

        fun setTitle(value: CharSequence): Builder {
            this.title = value
            return this
        }

        fun setTitle(resId: Int): Builder {
            return setTitle(getString(resId))
        }

        fun setMessage(value: CharSequence): Builder {
            this.message = value
            return this
        }

        fun setMessage(resId: Int): Builder {
            return setMessage(getString(resId))
        }

        private fun getString(resId: Int): String {
            return container.context.getString(resId)
        }

        fun onDismiss(listener: OnDismissListener): Builder {
            this.onDismissListener = listener
            return this
        }

        fun show(): MessageDialog {
            val provider = keyEventProvider ?: throw RuntimeException("KeyEventProvider is null")
            return MessageDialog(
                Option(
                    container,
                    provider,
                    onLeftClick,
                    onRightClick,
                    leftButtonName,
                    rightButtonName,
                    title,
                    message,
                    onDismissListener
                )
            ).apply {
                show()
            }
        }

    }

    fun interface OnClickListener {
        fun onClick(dialog: MessageDialog)
    }

    fun interface OnDismissListener {
        fun onDismiss()
    }

    private class Option(
        val container: ViewGroup,
        val keyEventProvider: KeyEventProvider,
        val onLeftClick: OnClickListener?,
        val onRightClick: OnClickListener?,
        val leftButtonName: CharSequence,
        val rightButtonName: CharSequence,
        val title: CharSequence,
        val message: CharSequence,
        val onDismissListener: OnDismissListener?
    ) {
        fun isEmptyCallback(): Boolean {
            return !isLeftButtonActive() && !isRightButtonActive()
        }

        fun isLeftButtonActive(): Boolean {
            return leftButtonName.isNotEmpty() && onLeftClick != null
        }

        fun isRightButtonActive(): Boolean {
            return rightButtonName.isNotEmpty() && onRightClick != null
        }
    }

}