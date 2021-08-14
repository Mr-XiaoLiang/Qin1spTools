package com.lollipop.qin1sptools.activity.base

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.qin1sptools.databinding.ActivitySimpleListBinding
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.list.SimpleTextAdapter
import com.lollipop.qin1sptools.utils.SimpleListHelper
import com.lollipop.qin1sptools.utils.lazyBind
import com.lollipop.qin1sptools.utils.visibleOrGone

/**
 * @author lollipop
 * @date 2021/7/24 14:39
 * 简易列表
 */
open class SimpleListActivity : FeatureBarActivity() {

    private val listBinding: ActivitySimpleListBinding by lazyBind()

    private val simpleListData = ArrayList<String>()

    protected val selectedIndex: Int
        get() {
            return simpleListHelper.selectedPosition
        }

    private val simpleListHelper = SimpleListHelper(
        {listBinding.recyclerView},
        { simpleListData.size },
        ::onSelectedIndexChanged
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(listBinding)
        initView()
    }

    private fun initView() {
        simpleListHelper.resetAnimation()
        listBinding.recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        listBinding.recyclerView.adapter = ListAdapter(simpleListData, ::getSelectedPosition)
        listBinding.recyclerView.adapter?.notifyDataSetChanged()
    }

    protected fun setData(data: List<String>) {
        simpleListData.clear()
        simpleListData.addAll(data)
        listBinding.recyclerView.adapter?.notifyDataSetChanged()
        simpleListHelper.resetSelected()
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
        simpleListHelper.selectedTo(position)
    }

    protected fun selectNext(): Boolean {
        return simpleListHelper.selectNext()
    }

    protected fun selectLast(): Boolean {
        return simpleListHelper.selectLast()
    }

    private fun getSelectedPosition(): Int {
        return simpleListHelper.selectedPosition
    }

    override fun onKeyDown(event: KeyEvent, repeatCount: Int): Boolean {
        val result = when (event) {
            KeyEvent.UP -> {
                selectLast()
            }
            KeyEvent.DOWN -> {
                selectNext()
            }
            else -> {
                false
            }
        }
        if (result) {
            return true
        }
        return super.onKeyDown(event, repeatCount)
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