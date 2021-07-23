package com.lollipop.qin1sptools.activity

import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.lollipop.qin1sptools.R
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