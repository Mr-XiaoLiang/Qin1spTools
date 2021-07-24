package com.lollipop.qin1sptools.activity

import android.os.Bundle
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.databinding.ActivitySimpleListBinding
import com.lollipop.qin1sptools.databinding.ItemTextBinding
import com.lollipop.qin1sptools.utils.bind
import com.lollipop.qin1sptools.utils.lazyBind

/**
 * @author lollipop
 * @date 2021/7/24 14:39
 * 简易列表
 */
open class SimpleListActivity : FeatureBarActivity() {

    private val listBinding: ActivitySimpleListBinding by lazyBind()

    private val simpleListData = ArrayList<String>()

    protected var selectedIndex = 0
        private set(value) {
            field = value
            onSelectedIndexChanged(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(listBinding)
        initView()
    }

    private fun initView() {
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
    }

    protected fun addData(data: List<String>) {
        val lastCount = simpleListData.size
        simpleListData.addAll(data)
        listBinding.recyclerView.adapter?.notifyItemRangeInserted(lastCount, data.size)
    }

    protected open fun onSelectedIndexChanged(index: Int) {

    }

    private fun getSelectedPosition(): Int {
        return selectedIndex
    }

    private class ListAdapter(
        private val data: List<String>,
        private val selectedProvider: () -> Int
    ) : RecyclerView.Adapter<ItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            return ItemHolder.create(parent)
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            holder.bind(data[position], selectedProvider() == position)
        }

        override fun getItemCount(): Int {
            return data.size
        }

    }

    private class ItemHolder(private val binding: ItemTextBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun create(parent: ViewGroup): ItemHolder {
                return ItemHolder(parent.bind(true))
            }
        }

        private val selectedColor =
            ContextCompat.getColor(itemView.context, R.color.itemSelectedBackground)
        private val defaultColor =
            ContextCompat.getColor(itemView.context, R.color.itemDefaultBackground)

        fun bind(text: String, isSelected: Boolean) {
            binding.root.setBackgroundColor(
                if (isSelected) {
                    selectedColor
                } else {
                    defaultColor
                }
            )
            binding.textView.text = text
        }

    }

}