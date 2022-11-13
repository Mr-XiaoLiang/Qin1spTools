package com.lollipop.qin1sptools.activity.base

import android.os.Bundle
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.menu.GridMenu
import com.lollipop.qin1sptools.menu.GridMenuManagerV1

/**
 * @author lollipop
 * @date 2021/7/18 18:12
 * 宫格菜单的Activity
 */
open class GridMenuActivity : FeatureBarActivity() {

    protected val gridItemList = ArrayList<GridMenu.GridItem>()

    private val gridMenu: GridMenu by lazy {
        createMenuManger()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gridMenu.onCreate()
        setContentView(gridMenu.rootView)
    }

    private fun createMenuManger(): GridMenu {
        return GridMenuManagerV1(this, ::gridItemList, ::onGridItemClick, ::onGridItemInfoClick)
    }

    override fun onKeyUp(event: KeyEvent, repeatCount: Int): Boolean {
        return gridMenu.onKeyUp(event, repeatCount) || super.onKeyUp(event, repeatCount)
    }

    protected fun notifyDataSetChanged() {
        gridMenu.notifyDataSetChanged()
    }

    protected fun resetCurrentSelected() {
        gridMenu.resetCurrentSelected()
    }

    protected open fun onGridItemClick(item: GridMenu.GridItem, index: Int) {

    }

    protected open fun onGridItemInfoClick(item: GridMenu.GridItem?, index: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        gridMenu.onDestroy()
    }

}