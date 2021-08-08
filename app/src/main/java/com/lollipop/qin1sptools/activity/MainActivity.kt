package com.lollipop.qin1sptools.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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

    }

    override fun onGridItemClick(item: GridItem, index: Int) {

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