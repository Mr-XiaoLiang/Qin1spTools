package com.lollipop.qin1sptools.activity

import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.lollipop.qin1sptools.databinding.ActivityFeatureBarBinding
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.list.SimpleTextAdapter
import com.lollipop.qin1sptools.utils.FeatureIcon
import com.lollipop.qin1sptools.utils.lazyBind
import com.lollipop.qin1sptools.utils.visibleOrGone

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

    protected var isLoading = false
        private set

    protected open val baseFeatureIconArray = arrayOf(
        FeatureIcon.NONE,
        FeatureIcon.NONE,
        FeatureIcon.BACK
    )

    private val optionItemList = ArrayList<OptionMenuItem>()

    private var selectedOptionItemIndex = -1

    protected val isOptionMenuShown: Boolean
        get() {
            return featureBinding.optionMenuView.isShown
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFeatureButtons()
        initOptionMenu()
    }

    private fun initOptionMenu() {
        featureBinding.optionMenuView.apply {
            layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
            adapter = OptionLisAdapter(optionItemList, ::getSelectedOptionItemPosition)
        }
    }

    private fun getSelectedOptionItemPosition(): Int {
        return selectedOptionItemIndex
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
        featureBinding.contentLoadingView.show()
    }

    protected fun endLoading() {
        isLoading = false
        featureBinding.contentLoadingView.hide()
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

    override fun onKeyUp(event: KeyEvent): Boolean {
        when (event) {
            KeyEvent.BACK -> {
                if (tryCloseOptionMenu()) {
                    return true
                }
            }

            KeyEvent.CENTER -> {
                if (checkIfSelectedOption()) {
                    return true
                }
            }

            KeyEvent.UP -> {
                if (tryMoveUpSelectedOption()) {
                    return true
                }
            }

            KeyEvent.DOWN -> {
                if (tryMoveDownSelectedOption()) {
                    return true
                }
            }

            else -> {

            }
        }
        if (super.onKeyUp(event)) {
            return true
        }
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
                if (checkIfSelectedOption() || onCenterFeatureButtonClick()) {
                    return true
                }
            }

            KeyEvent.UP -> {
                if (tryMoveUpSelectedOption()) {
                    return true
                }
            }

            KeyEvent.DOWN -> {
                if (tryMoveDownSelectedOption()) {
                    return true
                }
            }

            else -> {

            }
        }
        return false
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

    protected fun showOptionDialog(list: List<OptionMenuItem>) {
        if (list.isEmpty()) {
            changeOptionMenu(false)
            return
        }
        optionItemList.clear()
        optionItemList.addAll(list)
        selectedOptionItemIndex = 0
        changeOptionMenu(true)
    }

    private fun checkIfSelectedOption(): Boolean {
        if (!isOptionMenuShown) {
            return false
        }
        changeOptionMenu(false)
        val index = selectedOptionItemIndex
        if (index < 0 || index >= optionItemList.size) {
            return false
        }
        val optionItem = optionItemList[index]
        optionItemList.clear()
        onOptionItemSelected(optionItem)
        return true
    }

    private fun tryMoveUpSelectedOption(): Boolean {
        if (!isOptionMenuShown) {
            return false
        }
        val index = selectedOptionItemIndex
        if (index < 1 || optionItemList.isEmpty()) {
            return true
        }
        selectedOptionItemIndex--
        featureBinding.optionMenuView.adapter?.notifyItemRangeChanged(selectedOptionItemIndex, 2)
        return true
    }

    private fun tryMoveDownSelectedOption(): Boolean {
        if (!isOptionMenuShown) {
            return false
        }
        val index = selectedOptionItemIndex
        if (optionItemList.isEmpty() || index >= optionItemList.size - 1) {
            return true
        }
        selectedOptionItemIndex++
        featureBinding.optionMenuView.adapter?.notifyItemRangeChanged(index, 2)
        return true
    }

    private fun tryCloseOptionMenu(): Boolean {
        if (!isOptionMenuShown) {
            return false
        }
        changeOptionMenu(false)
        return true
    }

    protected open fun onOptionItemSelected(item: OptionMenuItem) {

    }

    private fun changeOptionMenu(isShow: Boolean) {
        featureBinding.optionMenuView.visibleOrGone(isShow) {
            adapter?.notifyDataSetChanged()
        }
    }

    private class OptionLisAdapter(
        private val data: List<OptionMenuItem>,
        private val selectedProvider: () -> Int
    ) : SimpleTextAdapter() {
        override fun getText(position: Int): String {
            return data[position].label
        }

        override val selectedPosition: Int
            get() = selectedProvider()

        override fun getItemCount(): Int {
            return data.size
        }

    }

    data class OptionMenuItem(
        val label: String,
        val id: Int
    )

}