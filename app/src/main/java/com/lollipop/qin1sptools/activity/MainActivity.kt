package com.lollipop.qin1sptools.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.activity.base.GridMenuActivity
import com.lollipop.qin1sptools.boot.AccessibilityService
import com.lollipop.qin1sptools.dialog.OptionDialog
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.menu.GridMenu
import com.lollipop.qin1sptools.utils.FeatureIcon

class MainActivity : GridMenuActivity() {

    override val baseFeatureIconArray = arrayOf(
        FeatureIcon.NONE,
        FeatureIcon.NONE,
        FeatureIcon.BACK
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    private fun initData() {
        gridItemList.clear()
        for (i in 0 until 39) {
            val drawable = ContextCompat.getDrawable(this, R.mipmap.ic_launcher) ?: break
            gridItemList.add(
                GridMenu.GridItem(
                    i,
                    drawable,
                    "Label$i"
                )
            )
        }
        notifyDataSetChanged()
    }

    override fun onGridItemClick(item: GridMenu.GridItem, index: Int) {

    }

    override fun onKeyDown(event: KeyEvent, repeatCount: Int): Boolean {
        if (event == KeyEvent.OPTION) {
            OptionDialog.build(this) {
                setTitle(R.string.menu)
                dataList.clear()
                for (index in 0 until 5) {
                    dataList.add(OptionDialog.Item("Item $index", index))
                }
                setLeftButton(R.string.ok) {
                    if (it is OptionDialog) {
                        showToast("selected: ${it.selectedPosition}")
                    }
                    it.dismiss()
                }
                setRightButton(R.string.exit) {
                    it.dismiss()
                }
            }.show()
            return true
        }

        if (event == KeyEvent.KEY_5) {
        }

        if (event == KeyEvent.KEY_4) {
            startService(Intent(this, AccessibilityService::class.java))
        }
        return super.onKeyDown(event, repeatCount)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (FileChooseActivity.isResult(requestCode)) {
            val file = FileChooseActivity.getResultFile(resultCode, data)
            Toast.makeText(this, "$file", Toast.LENGTH_SHORT).show()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}