package com.lollipop.qin1sptools.debug

import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import com.lollipop.qin1sptools.databinding.DebugVirtualKeyboardBinding
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.event.KeyEventListener
import com.lollipop.qin1sptools.utils.lazyBind

/**
 * @author lollipop
 * @date 2021/7/27 21:44
 */
class DebugVirtualKeyboard(
    private val rootGroup: ViewGroup,
    private val keyEventListener: KeyEventListener
) {

    private val keyboardBinding: DebugVirtualKeyboardBinding by rootGroup.lazyBind(true)

    fun show() {
        if (keyboardBinding.root.parent == rootGroup) {
            return
        }
        keyboardBinding.root.parent?.let {
            if (it is ViewManager) {
                it.removeView(keyboardBinding.root)
            }
        }
        rootGroup.addView(
            keyboardBinding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        keyboardBinding.backBtn.bindClickEvent(KeyEvent.BACK)
        keyboardBinding.centerBtn.bindClickEvent(KeyEvent.CENTER)
        keyboardBinding.downBtn.bindClickEvent(KeyEvent.DOWN)
        keyboardBinding.leftBtn.bindClickEvent(KeyEvent.LEFT)
        keyboardBinding.rightBtn.bindClickEvent(KeyEvent.RIGHT)
        keyboardBinding.upBtn.bindClickEvent(KeyEvent.UP)
        keyboardBinding.optionBtn.bindClickEvent(KeyEvent.OPTION)
        keyboardBinding.num0Btn.bindClickEvent(KeyEvent.KEY_0)
        keyboardBinding.num1Btn.bindClickEvent(KeyEvent.KEY_1)
        keyboardBinding.num2Btn.bindClickEvent(KeyEvent.KEY_2)
        keyboardBinding.num3Btn.bindClickEvent(KeyEvent.KEY_3)
        keyboardBinding.num4Btn.bindClickEvent(KeyEvent.KEY_4)
        keyboardBinding.num5Btn.bindClickEvent(KeyEvent.KEY_5)
        keyboardBinding.num6Btn.bindClickEvent(KeyEvent.KEY_6)
        keyboardBinding.num7Btn.bindClickEvent(KeyEvent.KEY_7)
        keyboardBinding.num8Btn.bindClickEvent(KeyEvent.KEY_8)
        keyboardBinding.num9Btn.bindClickEvent(KeyEvent.KEY_9)
        keyboardBinding.starBtn.bindClickEvent(KeyEvent.KEY_STAR)
        keyboardBinding.poundBtn.bindClickEvent(KeyEvent.KEY_POUND)

        keyboardBinding.closeBtn.setOnClickListener {
            if (keyboardBinding.keyboardContentView.visibility == View.VISIBLE) {
                keyboardBinding.keyboardContentView.visibility = View.INVISIBLE
            } else {
                keyboardBinding.keyboardContentView.visibility = View.VISIBLE
            }
        }
    }

    private fun View.bindClickEvent(event: KeyEvent) {
        setOnClickListener(ClickWrapper(keyEventListener, event))
        setOnLongClickListener(LongClickWrapper(keyEventListener, event))
    }

    private class ClickWrapper(
        private val keyEventListener: KeyEventListener,
        private val event: KeyEvent
    ): View.OnClickListener {
        override fun onClick(v: View?) {
            keyEventListener.onKeyDown(event)
            keyEventListener.onKeyUp(event)
        }
    }

    private class LongClickWrapper(
        private val keyEventListener: KeyEventListener,
        private val event: KeyEvent
    ): View.OnLongClickListener {
        override fun onLongClick(v: View?): Boolean {
            return keyEventListener.onKeyLongPress(event)
        }
    }

}