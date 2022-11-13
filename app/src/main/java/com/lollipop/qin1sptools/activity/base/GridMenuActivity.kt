package com.lollipop.qin1sptools.activity.base

import android.os.Bundle
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.dialog.OptionDialog
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.menu.GridMenu
import com.lollipop.qin1sptools.menu.GridMenuManagerV1
import com.lollipop.qin1sptools.menu.GridMenuManagerV2
import com.lollipop.qin1sptools.utils.get
import com.lollipop.qin1sptools.utils.set

/**
 * @author lollipop
 * @date 2021/7/18 18:12
 * 宫格菜单的Activity
 */
open class GridMenuActivity : FeatureBarActivity() {

    companion object {
        private const val KEY_MENU_MODE = "KEY_MENU_MODE"

        const val MENU_MODE_V1 = 101

        const val MENU_MODE_V2 = 102
    }

    protected val gridItemList = ArrayList<GridMenu.GridItem>()

    private val gridMenu: GridMenu by lazy {
        createMenuManger()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gridMenu.onCreate()
        setContentView(gridMenu.rootView)
    }

    protected fun getMenuMode(): MenuMode {
        val modeCode = get(KEY_MENU_MODE, MenuMode.V2.code)
        return MenuMode.values().find { it.code == modeCode } ?: MenuMode.V2
    }

    private fun createMenuManger(): GridMenu {
        val modeCode = get(KEY_MENU_MODE, MenuMode.V2.code)
        val mode = getMenuMode()
        return when (mode) {
            MenuMode.V1 -> {
                GridMenuManagerV1(this, ::gridItemList, ::onGridItemClick, ::onGridItemInfoClick)
            }
            MenuMode.V2 -> {
                GridMenuManagerV2(this, ::gridItemList, ::onGridItemClick, ::onGridItemInfoClick)
            }
        }
    }

    protected fun setMenuMode(modeCode: Int) {
        val mode = MenuMode.values().find { it.code == modeCode } ?: return
        setMenuMode(mode)
    }

    protected fun setMenuMode(mode: MenuMode) {
        set(KEY_MENU_MODE, mode.code)
    }

    protected fun addMenuModeOption(option: OptionDialog.Option) {
        val currentMode = getMenuMode()
        MenuMode.values().forEach {
            if (it != currentMode) {
                option.add(OptionDialog.Item(getString(it.modeName), it.code))
            }
        }
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

    protected enum class MenuMode(val code: Int, val modeName: Int) {
        V1(MENU_MODE_V1, R.string.menu_mode_v1),
        V2(MENU_MODE_V2, R.string.menu_mode_v2);
    }

}