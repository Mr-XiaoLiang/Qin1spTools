package com.lollipop.qin1sptools.dialog

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.qin1sptools.activity.base.BaseActivity
import com.lollipop.qin1sptools.databinding.DialogOptionBinding
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.event.KeyEventProvider
import com.lollipop.qin1sptools.list.SimpleTextAdapter
import com.lollipop.qin1sptools.utils.SimpleListHelper
import com.lollipop.qin1sptools.utils.lazyBind

/**
 * @author lollipop
 * @date 2021/8/14 20:17
 */
class OptionDialog private constructor(private val option: Option) : BaseDialog(option) {

    companion object {
        fun build(activity: BaseActivity, run: Option.() -> Unit): OptionDialog {
            return OptionDialog(
                Option(
                    activity.window.decorView as ViewGroup,
                    activity
                ).apply(run)
            )
        }
    }

    private val binding: DialogOptionBinding by option.container.lazyBind(true)

    private val dataList = ArrayList<Item>()

    private val simpleListHelper = SimpleListHelper(
        {binding.optionListView},
        {dataList.size},
        ::onSelectedPositionChanged
    )

    override fun onBindContent(): View {
        binding.optionListView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        dataList.clear()
        dataList.addAll(option.dataList)

        val optionAdapter = OptionAdapter(dataList) {
            simpleListHelper.selectedPosition
        }
        binding.optionListView.adapter = optionAdapter

        simpleListHelper.resetSelected()
        simpleListHelper.resetAnimation()

        optionAdapter.notifyDataSetChanged()
        return binding.root
    }

    private fun onSelectedPositionChanged() {}

    override fun onKeyEvent(event: KeyEvent) {
        when (event) {
            KeyEvent.UP -> {
                simpleListHelper.selectLast()
            }
            KeyEvent.DOWN -> {
                simpleListHelper.selectNext()
            }
            else -> {
                super.onKeyEvent(event)
            }
        }
    }

    private class OptionAdapter(
        private val data: List<Item>,
        private val getSelected: () -> Int
    ) : SimpleTextAdapter() {

        override fun getText(position: Int): String {
            return data[position].name.toString()
        }

        override val selectedPosition: Int
            get() {
                return getSelected()
            }

        override fun getItemCount(): Int {
            return data.size
        }
    }

    class Option(
        container: ViewGroup,
        keyEventProvider: KeyEventProvider,
    ) : BaseDialog.Option(
        container,
        keyEventProvider
    ) {

        val dataList: MutableList<Item> = ArrayList()

        fun add(item: Item) {
            dataList.add(item)
        }

    }

    class Item(
        val name: CharSequence,
        val id: Int,
        val tag: Any = id
    )

}