package com.lollipop.qin1sptools.activity

import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.lollipop.qin1sptools.R

class MainActivity : GridMenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFeatureButtons(0, R.drawable.featurebar_select, R.drawable.featurebar_back)
        initData()
    }

    private fun initData() {
        gridItemList.clear()
        for (i in 0 until 39) {
            val drawable = ContextCompat.getDrawable(this, R.mipmap.ic_launcher) ?: break
            gridItemList.add(
                GridItem(
                    i,
                    drawable,
                    "Label$i"
                )
            )
        }
        notifyDataSetChanged()
    }

    override fun onGridItemClick(item: GridItem, index: Int) {
        Toast.makeText(this, "${item.label}", Toast.LENGTH_SHORT).show()
    }

}