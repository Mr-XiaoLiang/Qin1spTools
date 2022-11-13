package com.lollipop.qin1sptools.menu

import android.app.Activity
import android.graphics.drawable.Drawable
import androidx.viewbinding.ViewBinding
import com.lollipop.qin1sptools.event.KeyEvent

abstract class GridMenu(
    protected val activity: Activity,
    private val gridItemListProvider: () -> List<GridItem>?,
    private val onGridItemClickCallback: (item: GridItem, index: Int) -> Unit,
    private val onGridItemInfoClickCallback: (item: GridItem?, index: Int) -> Unit
) {

    protected val gridItemList: List<GridItem>
        get() {
            return gridItemListProvider() ?: emptyList()
        }

    abstract val rootView: ViewBinding

    abstract fun onCreate()

    abstract fun onDestroy()

    abstract fun onKeyUp(event: KeyEvent, repeatCount: Int): Boolean

    abstract fun notifyDataSetChanged()

    abstract fun resetCurrentSelected()

    protected fun onGridItemClick(item: GridItem, index: Int) {
        onGridItemClickCallback(item, index)
    }

    protected fun onGridItemInfoClick(item: GridItem?, index: Int) {
        onGridItemInfoClickCallback(item, index)
    }

    data class GridItem(
        val id: Int,
        val icon: Drawable,
        val label: CharSequence
    )

}