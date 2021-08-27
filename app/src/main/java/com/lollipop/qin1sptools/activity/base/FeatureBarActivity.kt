package com.lollipop.qin1sptools.activity.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import androidx.viewbinding.ViewBinding
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.databinding.ActivityFeatureBarBinding
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.event.KeyEventListener
import com.lollipop.qin1sptools.guide.Guide
import com.lollipop.qin1sptools.utils.*

/**
 * @author lollipop
 * @date 2021/7/17 18:05
 */
open class FeatureBarActivity : BaseActivity() {

    companion object {
        const val FEATURE_LEFT = 0
        const val FEATURE_CENTER = 1
        const val FEATURE_RIGHT = 2
    }

    private val featureBinding: ActivityFeatureBarBinding by lazyBind()

    private val loadingKeyFilter = LoadingKeyFilter()

    protected val viewRoot: View
        get() {
            return featureBinding.viewRoot
        }

    protected var isLoading: Boolean
        private set(value) {
            loadingKeyFilter.isLoading = value
        }
        get() {
            return loadingKeyFilter.isLoading
        }

    protected open val baseFeatureIconArray = arrayOf(
        FeatureIcon.NONE,
        FeatureIcon.NONE,
        FeatureIcon.BACK
    )

    private val toastHelper by lazy {
        ViewToastHelper(featureBinding.toastView) { view, value ->
            view.text = value
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addKeyEventListener(loadingKeyFilter)
        setFeatureButtons()
        endLoading()
        featureBinding.titleView.setOnClickListener {
            showVirtualKeyboard()
        }
    }

    override fun setContentView(binding: ViewBinding) {
        super.setContentView(featureBinding.root)
        binding.root.parent?.let { parent ->
            if (parent != featureBinding.contentGroup && parent is ViewManager) {
                parent.removeView(binding.root)
            }
        }
        featureBinding.contentGroup.addView(
            binding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        featureBinding.titleView.text = title
    }

    protected fun startLoading() {
        isLoading = true
        featureBinding.contentLoadingGroup.visibleOrGone(true)
        featureBinding.contentLoadingProgressBar.isIndeterminate = true
    }

    protected fun endLoading() {
        isLoading = false
        featureBinding.contentLoadingGroup.visibleOrGone(false)
        featureBinding.contentLoadingProgressBar.isIndeterminate = false
    }

    protected fun setLeftFeatureButton(icon: FeatureIcon) {
        featureBinding.leftOptionBtn.setImageResource(icon.resId)
    }

    protected fun setRightFeatureButton(icon: FeatureIcon) {
        featureBinding.rightOptionBtn.setImageResource(icon.resId)
    }

    protected fun setCenterFeatureButton(icon: FeatureIcon) {
        featureBinding.centerOptionBtn.setImageResource(icon.resId)
    }

    protected fun setFeatureButtons(
        left: FeatureIcon? = null,
        center: FeatureIcon? = null,
        right: FeatureIcon? = null
    ) {
        setLeftFeatureButton(findFeatureIcon(left, FEATURE_LEFT))
        setCenterFeatureButton(findFeatureIcon(center, FEATURE_CENTER))
        setRightFeatureButton(findFeatureIcon(right, FEATURE_RIGHT))
    }

    private fun findFeatureIcon(icon: FeatureIcon?, index: Int): FeatureIcon {
        if (icon != null) {
            return icon
        }
        if (baseFeatureIconArray.size > index) {
            return baseFeatureIconArray[index]
        }
        return FeatureIcon.NONE
    }

    override fun onKeyUp(event: KeyEvent, repeatCount: Int): Boolean {
        when (event) {
            KeyEvent.OPTION -> {
                if (onLeftFeatureButtonClick()) {
                    return true
                }
            }

            KeyEvent.BACK -> {
                if (onRightFeatureButtonClick()) {
                    return true
                }
            }

            KeyEvent.CENTER -> {
                if (onCenterFeatureButtonClick()) {
                    return true
                }
            }

            KeyEvent.KEY_POUND -> {
                showGuide()
            }

            else -> {
            }
        }
        return super.onKeyUp(event, repeatCount)
    }

    protected open fun onLeftFeatureButtonClick(): Boolean {
        return false
    }

    protected open fun onCenterFeatureButtonClick(): Boolean {
        return false
    }

    protected open fun onRightFeatureButtonClick(): Boolean {
        onBackPressed()
        return true
    }

    protected fun showToast(value: Int) {
        toastHelper.show(value)
    }

    protected fun showToast(value: String) {
        toastHelper.show(value)
    }

    override fun buildGuide(builder: Guide.Builder) {
        builder.next(KeyEvent.KEY_POUND, R.string.guide_show)
        super.buildGuide(builder)
    }

    private class LoadingKeyFilter: KeyEventListener {

        var isLoading = false

        override fun onKeyDown(event: KeyEvent, repeatCount: Int): Boolean {
            return isLoading
        }

        override fun onKeyUp(event: KeyEvent, repeatCount: Int): Boolean {
            return isLoading
        }
    }

}