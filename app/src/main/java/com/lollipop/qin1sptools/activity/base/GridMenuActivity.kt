package com.lollipop.qin1sptools.activity.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.databinding.ActivityGridMenuBinding
import com.lollipop.qin1sptools.databinding.ItemGridMenuBinding
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.utils.lazyBind
import com.lollipop.qin1sptools.utils.withThis
import com.lollipop.qin1sptools.view.NineGridsChild
import com.lollipop.qin1sptools.view.NineGridsLayout
import com.lollipop.qin1sptools.view.PagedLayout
import kotlin.math.min

/**
 * @author lollipop
 * @date 2021/7/18 18:12
 * 宫格菜单的Activity
 */
open class GridMenuActivity : FeatureBarActivity() {

    companion object {
        private const val DEFAULT_ITEM_POSITION = -1
    }

    private val binding: ActivityGridMenuBinding by lazyBind()

    protected val gridItemList = ArrayList<GridItem>()

    private var selectedItemIndex = DEFAULT_ITEM_POSITION
        set(value) {
            field = value
            onSelectedItemChanged(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding)
        initView()
    }

    private fun initView() {
        binding.indicatorView.bindToPagedLayout(binding.pagedLayout)
        notifyDataSetChanged()
    }

    protected fun notifyDataSetChanged() {
        val pagedLayout = binding.pagedLayout
        pagedLayout.currentItem = 0
        val itemSize = gridItemList.size
        val pageCount = if (itemSize % 9 == 0) {
            itemSize / 9
        } else {
            (itemSize / 9) + 1
        }
        checkPageSize(pageCount)

        var pageViewIndex = 0
        for (pageIndex in 0 until pageCount) {
            pageViewIndex = findNextGridPage(pageViewIndex, pagedLayout)
            if (pageViewIndex < 0) {
                break
            }
            val pageView = pagedLayout.getChildAt(pageViewIndex)
            if (pageView !is NineGridsLayout) {
                break
            }

            pageView.pageIndex = pageIndex
            bindGridItem(pageView, gridItemList, pageIndex * 9)

            pageViewIndex++
        }
        pagedLayout.reset()
    }

    protected open fun onGridItemClick(item: GridItem, index: Int) {}

    private fun bindGridItem(pageView: NineGridsLayout, itemList: List<GridItem>, offset: Int) {
        val itemCount = min(min(offset + 9, itemList.size) - offset, 9)
        val space = resources.getDimensionPixelSize(R.dimen.grid_menu_space)
        pageView.setPadding(space, space, space, space)
        pageView.childSpace = space
        val needRemovedList = ArrayList<View>()
        for (index in 0 until pageView.childCount) {
            pageView.getChildAt(index)?.let {
                if (it !is GridHolder) {
                    needRemovedList.add(it)
                }
            }
        }
        needRemovedList.forEach {
            pageView.removeView(it)
        }
        while (pageView.childCount > 0 && pageView.childCount > itemCount) {
            pageView.removeViewAt(0)
        }
        while (pageView.childCount < itemCount) {
            pageView.addView(GridHolder(this))
        }
        for (index in 0 until itemCount) {
            pageView.getChildAt(index)?.let { child ->
                if (child is GridHolder) {
                    child.bind(itemList[index + offset])
                }
            }
        }
        pageView.notifyChildIndexChanged()
    }

    private fun checkPageSize(count: Int) {
        val pagedLayout = binding.pagedLayout
        var pageCount = 0
        for (index in 0 until pagedLayout.childCount) {
            pagedLayout.getChildAt(index)?.let {
                if (it is NineGridsLayout) {
                    pageCount++
                }
            }
        }
        // 如果数量一致，那么就不用继续处理了
        if (pageCount == count) {
            return
        }
        // 数量多了，选择性的删除
        while (pagedLayout.childCount > 0 && pagedLayout.childCount > count) {
            if (!removeLastPage(pagedLayout)) {
                break
            }
        }
        // 如果数量少了，就补上
        while (pagedLayout.childCount < count && count > 0) {
            pagedLayout.addView(NineGridsLayout(this))
        }

    }

    private fun removeLastPage(pagedLayout: PagedLayout): Boolean {
        for (index in pagedLayout.childCount - 1 downTo 0) {
            pagedLayout.getChildAt(index)?.let {
                if (it is NineGridsLayout) {
                    pagedLayout.removeView(it)
                    return true
                }
            }
        }
        return false
    }

    private fun findNextGridPage(offset: Int, pagedLayout: PagedLayout): Int {
        for (index in offset until pagedLayout.childCount) {
            pagedLayout.getChildAt(index)?.let {
                if (it is NineGridsLayout) {
                    return index
                }
            }
        }
        return -1
    }

    override fun onKeyUp(event: KeyEvent): Boolean {
        when (event) {
            KeyEvent.CENTER, KeyEvent.CALL -> {
                val position = if (selectedItemIndex == DEFAULT_ITEM_POSITION) {
                    5
                } else {
                    selectedItemIndex
                }
                onNumberClick(position)
            }
            KeyEvent.LEFT, KeyEvent.UP, KeyEvent.KEY_STAR -> {
                // 翻页后重置选中序号
                selectedItemIndex = DEFAULT_ITEM_POSITION
                binding.pagedLayout.currentPage()?.resetSelectedFlag()
                binding.pagedLayout.lastPage()?.resetSelectedFlag()
            }
            KeyEvent.RIGHT, KeyEvent.DOWN, KeyEvent.KEY_POUND -> {
                // 翻页后重置选中序号
                selectedItemIndex = DEFAULT_ITEM_POSITION
                binding.pagedLayout.currentPage()?.resetSelectedFlag()
                binding.pagedLayout.nextPage()?.resetSelectedFlag()
            }
            KeyEvent.KEY_1 -> {
                onNumberClick(1)
            }
            KeyEvent.KEY_2 -> {
                onNumberClick(2)
            }
            KeyEvent.KEY_3 -> {
                onNumberClick(3)
            }
            KeyEvent.KEY_4 -> {
                onNumberClick(4)
            }
            KeyEvent.KEY_5 -> {
                onNumberClick(5)
            }
            KeyEvent.KEY_6 -> {
                onNumberClick(6)
            }
            KeyEvent.KEY_7 -> {
                onNumberClick(7)
            }
            KeyEvent.KEY_8 -> {
                onNumberClick(8)
            }
            KeyEvent.KEY_9 -> {
                onNumberClick(9)
            }
            else -> {
                return super.onKeyUp(event)
            }
        }
        return true
    }

    private fun View.resetSelectedFlag() {
        this.let {
            if (it is NineGridsLayout) {
                it.selectedChild = DEFAULT_ITEM_POSITION
            }
        }
    }

    private fun onSelectedItemChanged(position: Int) {
        val itemIndex = getItemIndexByPosition(position)
        if (itemIndex < 0) {
            setTitle(R.string.app_name)
        } else {
            title = gridItemList[itemIndex].label
        }
    }

    private fun onNumberClick(position: Int) {
        val pageView = findCurrentGridPage() ?: return
        val itemIndex = getItemIndexByPosition(position)
        if (itemIndex < 0) {
            return
        }
        if (pageView.selectedChild >= 0 && pageView.selectedChild == position - 1) {
            onGridItemClick(gridItemList[itemIndex], itemIndex)
        } else {
            selectedItemIndex = position
            pageView.selectedChild = position - 1
        }
    }

    private fun getItemIndexByPosition(position: Int): Int {
        if (position < 1) {
            return -1
        }
        val pageView = findCurrentGridPage() ?: return -1
        if (pageView.childCount < position) {
            return -1
        }
        val dataPageIndex = pageView.pageIndex
        val itemIndex = dataPageIndex * 9 + position - 1
        if (itemIndex < 0 || itemIndex >= gridItemList.size) {
            return -1
        }
        return itemIndex
    }

    private fun findCurrentGridPage(): NineGridsLayout? {
        val pageView = binding.pagedLayout.currentPage()
        if (pageView is NineGridsLayout) {
            return pageView
        }
        return null
    }

    data class GridItem(
        val id: Int,
        val icon: Drawable,
        val label: CharSequence
    )

    private class GridHolder(context: Context) : FrameLayout(context), NineGridsChild {

        private val binding: ItemGridMenuBinding by withThis(true)

        @SuppressLint("SetTextI18n")
        override fun setGridIndex(index: Int) {
            binding.positionView.text = "${index + 1}"
        }

        fun bind(item: GridItem) {
            binding.iconView.setImageDrawable(item.icon)
        }

    }

}