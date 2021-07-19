package com.lollipop.qin1sptools.activity

import android.view.ViewGroup
import android.view.ViewManager
import androidx.viewbinding.ViewBinding
import com.lollipop.qin1sptools.databinding.ActivityFeatureBarBinding
import com.lollipop.qin1sptools.utils.lazyBind

/**
 * @author lollipop
 * @date 2021/7/17 18:05
 */
open class FeatureBarActivity : BaseActivity() {

    private val featureBinding: ActivityFeatureBarBinding by lazyBind()

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

    protected fun setLeftFeatureButton(icon: Int) {
        featureBinding.leftOptionBtn.setImageResource(icon)
    }

    protected fun setRightFeatureButton(icon: Int) {
        featureBinding.rightOptionBtn.setImageResource(icon)
    }

    protected fun setCenterFeatureButton(icon: Int) {
        featureBinding.centerOptionBtn.setImageResource(icon)
    }

    protected fun setFeatureButtons(
        left: Int,
        center: Int,
        right: Int
    ) {
        setLeftFeatureButton(left)
        setCenterFeatureButton(center)
        setRightFeatureButton(right)
    }

}