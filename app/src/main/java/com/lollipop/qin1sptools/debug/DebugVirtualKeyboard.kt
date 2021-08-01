package com.lollipop.qin1sptools.debug

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import com.lollipop.qin1sptools.databinding.DebugVirtualKeyboardBinding
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.event.KeyEventListener
import com.lollipop.qin1sptools.utils.lazyBind
import com.lollipop.qin1sptools.utils.task

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
//        setOnClickListener(ClickWrapper(keyEventListener, event))
//        setOnLongClickListener(LongClickWrapper(keyEventListener, event))
        setOnTouchListener(TouchWrapper(keyEventListener, event))
    }

    private class ClickWrapper(
        private val keyEventListener: KeyEventListener,
        private val event: KeyEvent
    ) : View.OnClickListener {
        override fun onClick(v: View?) {
            keyEventListener.onKeyDown(event)
            keyEventListener.onKeyUp(event)
        }
    }

    private class TouchWrapper(
        private val keyEventListener: KeyEventListener,
        private val keyEvent: KeyEvent
    ) : View.OnTouchListener {

        private var actionId = -1

        private var isTouched = false

        private val longPressTask = task {
            if (keyEventListener.onKeyLongPress(keyEvent)) {
                if (isTouched) {
                    isTouched = false
                    keyEventListener.onKeyUp(keyEvent)
                }
            }
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            v ?: return false
            event ?: return false
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    actionId = event.getPointerId(0)
                    isTouched = true
                    keyEventListener.onKeyDown(keyEvent)
                    longPressTask.delay(1000L)
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    if (!isTouched) {
                        return false
                    }
                    if (event.findPointerIndex(actionId) < 0) {
                        onTouchUp()
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    onTouchUp()
                }
                MotionEvent.ACTION_MOVE -> {
                    onTouchMove(v, event)
                }
                MotionEvent.ACTION_UP -> {
                    onTouchUp()
                }
            }
            return isTouched
        }

        private fun onTouchUp() {
            longPressTask.cancel()
            if (!isTouched) {
                return
            }
            keyEventListener.onKeyUp(keyEvent)
            isTouched = false
        }

        private fun onTouchMove(view: View, event: MotionEvent) {
            if (!isTouched) {
                return
            }
            val index = event.findPointerIndex(actionId)
            if (index < 0) {
                isTouched = false
                keyEventListener.onKeyUp(keyEvent)
                return
            }
            val x = event.getX(index)
            val y = event.getY(index)
            if (x < 0 || x > view.width) {
                isTouched = false
                keyEventListener.onKeyUp(keyEvent)
                return
            }
            if (y < 0 || y > view.height) {
                isTouched = false
                keyEventListener.onKeyUp(keyEvent)
                return
            }
        }

    }

    private class LongClickWrapper(
        private val keyEventListener: KeyEventListener,
        private val event: KeyEvent
    ) : View.OnLongClickListener {
        override fun onLongClick(v: View?): Boolean {
            return keyEventListener.onKeyLongPress(event)
        }
    }

}