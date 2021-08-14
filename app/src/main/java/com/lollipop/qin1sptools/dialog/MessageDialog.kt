package com.lollipop.qin1sptools.dialog

import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.ViewGroup
import com.lollipop.qin1sptools.activity.base.BaseActivity
import com.lollipop.qin1sptools.databinding.DialogMessageBinding
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.event.KeyEventProvider
import com.lollipop.qin1sptools.utils.visibleOrGone
import com.lollipop.qin1sptools.utils.withThis

/**
 * @author lollipop
 * @date 2021/8/10 22:32
 */
class MessageDialog private constructor(private val option: Option) : BaseDialog(option) {

    companion object {
        fun build(activity: BaseActivity, run: Option.() -> Unit): MessageDialog {
            return MessageDialog(
                Option(
                    activity.window.decorView as ViewGroup,
                    activity
                ).apply(run)
            )
        }
    }

    private val binding: DialogMessageBinding by option.container.withThis(true)

    override fun onBindContent(): View {
        binding.messageView.visibleOrGone(option.message.isNotEmpty()) {
            text = option.message
            movementMethod = ScrollingMovementMethod.getInstance()
        }
        return binding.root
    }

    override fun onKeyEvent(event: KeyEvent) {
        when (event) {
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
                super.onKeyEvent(event)
            }
        }
    }

    class Option(
        container: ViewGroup,
        keyEventProvider: KeyEventProvider,
    ) : BaseDialog.Option(
        container,
        keyEventProvider,
    ) {
        var message: CharSequence = ""

        fun setMessage(resId: Int) {
            this.message = getString(resId)
        }

    }

}