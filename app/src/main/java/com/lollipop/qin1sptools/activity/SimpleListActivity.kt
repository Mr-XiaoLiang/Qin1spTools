package com.lollipop.qin1sptools.activity

import android.os.Bundle
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.databinding.ActivitySimpleListBinding
import com.lollipop.qin1sptools.databinding.ItemTextBinding
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.list.SimpleTextAdapter
import com.lollipop.qin1sptools.utils.bind
import com.lollipop.qin1sptools.utils.lazyBind
import com.lollipop.qin1sptools.utils.range
import com.lollipop.qin1sptools.utils.visibleOrGone

/**
 * @author lollipop
 * @date 2021/7/24 14:39
 * 简易列表
 */
open class SimpleListActivity : FeatureBarActivity() {

    private val listBinding: ActivitySimpleListBinding by lazyBind()

    private val simpleListData = ArrayList<String>()

    protected var selectedIndex = 0
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(listBinding)
        initView()
    }

    private fun initView() {
        listBinding.recyclerView.itemAnimator?.let { animator ->
            animator.changeDuration = 150
            animator.addDuration = 150
            animator.moveDuration = 150
            animator.removeDuration = 150
        }
        listBinding.recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        listBinding.recyclerView.adapter = ListAdapter(simpleListData, ::getSelectedPosition)
        listBinding.recyclerView.adapter?.notifyDataSetChanged()
    }

    protected fun setData(data: List<String>) {
        simpleListData.clear()
        simpleListData.addAll(data)
        listBinding.recyclerView.adapter?.notifyDataSetChanged()
        selectedIndex = if (simpleListData.isEmpty()) {
            -1
        } else {
            0
        }
        listBinding.emptyView.visibleOrGone(simpleListData.isEmpty())
        onSelectedIndexChanged()
    }

    protected fun addData(data: List<String>) {
        val lastCount = simpleListData.size
        simpleListData.addAll(data)
        listBinding.recyclerView.adapter?.notifyItemRangeInserted(lastCount, data.size)
        listBinding.emptyView.visibleOrGone(simpleListData.isEmpty())
    }

    protected fun setError(msg: String) {
        listBinding.errorMsgView.visibleOrGone(true) {
            text = msg
        }
        listBinding.recyclerView.visibleOrGone(false)
    }

    protected fun clearError() {
        listBinding.errorMsgView.visibleOrGone(false)
        listBinding.recyclerView.visibleOrGone(true) {
            adapter?.notifyDataSetChanged()
        }
    }

    protected open fun onSelectedIndexChanged() {

    }

    protected fun selectedTo(position: Int) {
        if (simpleListData.isEmpty()) {
            selectedIndex - 1
            return
        }
        selectedIndex = position.range(0, simpleListData.size - 1)
        listBinding.recyclerView.scrollToPosition(selectedIndex)
    }

    protected fun selectNext(): Boolean {
        if (selectedIndex < simpleListData.size - 1) {
            val lastIndex = selectedIndex
            selectedIndex++
            listBinding.recyclerView.adapter?.notifyItemRangeChanged(lastIndex, 2)
            listBinding.recyclerView.scrollToPosition(selectedIndex)
            onSelectedIndexChanged()
            return true
        }
        return false
    }

    protected fun selectLast(): Boolean {
        if (selectedIndex > 0) {
            selectedIndex--
            listBinding.recyclerView.adapter?.notifyItemRangeChanged(selectedIndex, 2)
            listBinding.recyclerView.scrollToPosition(selectedIndex)
            onSelectedIndexChanged()
            return true
        }
        return false
    }

    private fun getSelectedPosition(): Int {
        return selectedIndex
    }

    override fun onKeyUp(event: KeyEvent): Boolean {
        when (event) {
            KeyEvent.UP -> {
                return selectLast()
            }
            KeyEvent.DOWN -> {
                return selectNext()
            }
            else -> {

            }
        }
        return super.onKeyUp(event)
    }

    private class ListAdapter(
        private val data: List<String>,
        private val selectedProvider: () -> Int
    ) : SimpleTextAdapter() {

        override fun getText(position: Int): String {
            return data[position]
        }

        override val selectedPosition: Int
            get() = selectedProvider()

        override fun getItemCount(): Int {
            return data.size
        }

    }

}