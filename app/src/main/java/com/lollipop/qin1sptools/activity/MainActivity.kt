package com.lollipop.qin1sptools.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.activity.base.GridMenuActivity
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
        showToast("${item.label}")
        FileChooseActivity.start(this)
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