package com.lollipop.qin1sptools.menu

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.databinding.ActivityGridMenuV2Binding
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.utils.lazyBind

class GridMenuManagerV2(
    activity: Activity,
    gridItemListProvider: () -> List<GridItem>,
    onGridItemClickCallback: (item: GridItem, index: Int) -> Unit,
    onGridItemInfoClickCallback: (item: GridItem?, index: Int) -> Unit
) : GridMenu(activity, gridItemListProvider, onGridItemClickCallback, onGridItemInfoClickCallback) {

    private val binding: ActivityGridMenuV2Binding by activity.lazyBind()

    override val rootView: ViewBinding
        get() {
            return binding
        }

    private val dataList = ArrayList<GridItem>()

    private val adapter = PageAdapter(dataList)

    private var selectedPosition = 0

    override fun onCreate() {
        binding.contentGroup.adapter = adapter
        binding.contentGroup.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.contentGroup.getChildAt(0)?.let {
            if (it is RecyclerView) {
                val padding = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    50F,
                    activity.resources.displayMetrics
                ).toInt()
                it.setPadding(padding, 0, padding, 0)
                it.clipToPadding = false
            }
        }
        binding.contentGroup.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    onItemSelected(position)
                }
            }
        )
    }

    private fun onItemSelected(position: Int) {
        if (position < 0) {
            activity.setTitle(R.string.app_name)
        } else {
            activity.title = dataList[position].label
        }
        selectedPosition = position
        updateIndex()
    }

    @SuppressLint("SetTextI18n")
    private fun updateIndex() {
        binding.indexView.text = "${selectedPosition + 1}/${dataList.size}"
    }

    override fun onKeyUp(event: KeyEvent, repeatCount: Int): Boolean {
        when (event) {
            KeyEvent.LEFT, KeyEvent.UP, KeyEvent.KEY_4, KeyEvent.KEY_2 -> {
                if (dataList.isNotEmpty() && selectedPosition > 0) {
                    binding.contentGroup.currentItem = selectedPosition - 1
                }
            }
            KeyEvent.RIGHT, KeyEvent.DOWN, KeyEvent.KEY_6, KeyEvent.KEY_8 -> {
                if (dataList.isNotEmpty() && selectedPosition < dataList.size - 1) {
                    binding.contentGroup.currentItem = selectedPosition + 1
                }
            }
            KeyEvent.CENTER, KeyEvent.KEY_5, KeyEvent.CALL -> {
                val position = selectedPosition
                if (position in dataList.indices) {
                    onGridItemClick(dataList[position], position)
                }
            }
            KeyEvent.KEY_STAR -> {
                val position = selectedPosition
                if (position in dataList.indices) {
                    onGridItemInfoClick(dataList[position], position)
                }
            }
            else -> {
                return false
            }
        }
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun notifyDataSetChanged() {
        dataList.clear()
        dataList.addAll(gridItemList)
        adapter.notifyDataSetChanged()
        resetCurrentSelected()
        updateIndex()
    }

    override fun resetCurrentSelected() {
        selectedPosition = if (dataList.isEmpty()) {
            -1
        } else {
            0
        }
        binding.contentGroup.currentItem = selectedPosition
    }

    override fun onDestroy() {
    }

    private class PageAdapter(
        private val data: List<GridItem>
    ) : RecyclerView.Adapter<PageHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
            return PageHolder.create(parent.context)
        }

        override fun onBindViewHolder(holder: PageHolder, position: Int) {
            holder.bind(data[position])
        }

        override fun getItemCount(): Int {
            return data.size
        }
    }

    private class PageHolder private constructor(
        group: FrameLayout,
        private val iconView: IconView
    ) : RecyclerView.ViewHolder(group) {
        companion object {
            fun create(context: Context): PageHolder {
                val groupView = FrameLayout(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
                val layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ).apply {
                    gravity = Gravity.CENTER
                }
                val iconView = IconView(context)
                groupView.addView(iconView, layoutParams)
                return PageHolder(groupView, iconView)
            }
        }

        init {
            iconView.scaleType = ImageView.ScaleType.FIT_CENTER
        }

        fun bind(info: GridItem) {
            iconView.setImageDrawable(info.icon)
        }

    }

    private class IconView(context: Context) : AppCompatImageView(context) {

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            setMeasuredDimension(measuredHeight, measuredHeight)
        }

    }

}