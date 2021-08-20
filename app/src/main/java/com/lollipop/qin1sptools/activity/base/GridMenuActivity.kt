package com.lollipop.qin1sptools.activity.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.databinding.ActivityGridMenuBinding
import com.lollipop.qin1sptools.databinding.ItemGridMenuBinding
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.utils.lazyBind
import com.lollipop.qin1sptools.utils.withThis
import com.lollipop.qin1sptools.view.NineGridsChild
import com.lollipop.qin1sptools.view.NineGridsLayout
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

    private val viewRecycler = ViewRecycler()

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
        val itemSize = gridItemList.size
        val pageCount = if (itemSize % 9 == 0) {
            itemSize / 9
        } else {
            (itemSize / 9) + 1
        }

        for (index in 0 until pagedLayout.childCount) {
            val page = pagedLayout.getChildAt(index)
            if (page is NineGridsLayout) {
                viewRecycler.recycle(page)
            }
        }
        viewRecycler.recycle(pagedLayout)

        for (pageIndex in 0 until pageCount) {
            val pageView: NineGridsLayout = viewRecycler.find {
                NineGridsLayout(pagedLayout.context)
            }
            pagedLayout.addView(pageView)
            pageView.pageIndex = pageIndex
            bindGridItem(pageView, gridItemList, pageIndex * 9)
        }
        pagedLayout.reset()
    }

    protected open fun onGridItemClick(item: GridItem, index: Int) {}

    protected open fun onGridItemInfoClick(item: GridItem?, index: Int) {}

    private fun bindGridItem(pageView: NineGridsLayout, itemList: List<GridItem>, offset: Int) {
        val itemCount = min(min(offset + 9, itemList.size) - offset, 9)
        val space = resources.getDimensionPixelSize(R.dimen.grid_menu_space)
        pageView.setPadding(space, space, space, space)
        pageView.childSpace = space
        viewRecycler.recycle(pageView)
        for (index in 0 until itemCount) {
            val itemHolder = viewRecycler.find {
                GridHolder(pageView.context)
            }
            itemHolder.bind(itemList[index + offset])
            pageView.addView(itemHolder)
        }
        pageView.notifyChildIndexChanged()
    }

    override fun onKeyUp(event: KeyEvent, repeatCount: Int): Boolean {
        when (event) {
            KeyEvent.CENTER, KeyEvent.CALL -> {
                val position = if (selectedItemIndex == DEFAULT_ITEM_POSITION) {
                    5
                } else {
                    selectedItemIndex
                }
                onNumberClick(position)
            }
            KeyEvent.LEFT, KeyEvent.UP -> {
                // 翻页后重置选中序号
                selectedItemIndex = DEFAULT_ITEM_POSITION
                binding.pagedLayout.currentPage()?.resetSelectedFlag()
                binding.pagedLayout.lastPage()?.resetSelectedFlag()
            }
            KeyEvent.RIGHT, KeyEvent.DOWN -> {
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
            KeyEvent.KEY_STAR -> {
                onStarClick()
            }
            else -> {
                return super.onKeyUp(event, repeatCount)
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

    private fun onStarClick() {
        val pageView = findCurrentGridPage()
        if (pageView == null) {
            onGridItemInfoClick(null, -1)
            return
        }
        if (selectedItemIndex < 0) {
            onGridItemInfoClick(null, -1)
            return
        }
        val itemIndex = getItemIndexByPosition(selectedItemIndex)
        if (itemIndex < 0) {
            onGridItemInfoClick(null, -1)
            return
        }
        if (pageView.selectedChild >= 0 && pageView.selectedChild == selectedItemIndex - 1) {
            onGridItemInfoClick(gridItemList[itemIndex], itemIndex)
        } else {
            onGridItemInfoClick(null, -1)
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

    override fun onDestroy() {
        super.onDestroy()
        viewRecycler.destroy()
    }

    data class GridItem(
        val id: Int,
        val icon: Drawable,
        val label: CharSequence
    )

    private class GridHolder(context: Context) : FrameLayout(context), NineGridsChild {

        private val binding: ItemGridMenuBinding by withThis(true)

        init {
            binding.shapeGroup.setRoundShapeDp(10)
        }

        @SuppressLint("SetTextI18n")
        override fun setGridIndex(index: Int) {
            binding.positionView.text = "${index + 1}"
        }

        fun bind(item: GridItem) {
            binding.iconView.setImageDrawable(item.icon)
        }

    }

    private class ViewRecycler {
        val viewPool = ArrayList<View>()

        fun recycle(viewGroup: ViewGroup) {
            // 暂时放弃回收View
//            val childCount = viewGroup.childCount
//            for (index in 0 until childCount) {
//                viewPool.add(viewGroup.getChildAt(index))
//            }
            viewGroup.removeAllViewsInLayout()
        }

        inline fun <reified T: View> find(viewCreate: () -> T): T {
            for (view in viewPool) {
                if (view is T) {
                    viewPool.remove(view)
                    return view
                }
            }
            return viewCreate()
        }

        fun destroy() {
            viewPool.clear()
        }

    }

}