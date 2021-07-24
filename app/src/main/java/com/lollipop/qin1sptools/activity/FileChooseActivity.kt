package com.lollipop.qin1sptools.activity

import android.os.Bundle
import com.lollipop.qin1sptools.utils.FeatureIcon

/**
 * 文件选择的Activity
 * 可以按照要求选中一个文件夹或者某个文件
 * @author Lollipop
 * @date 2021/07/23
 */
class FileChooseActivity : SimpleListActivity() {

    override val baseFeatureIconArray = arrayOf(
        FeatureIcon.ADD,
        FeatureIcon.SELECT,
        FeatureIcon.BACK
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {

    }

    private fun initData() {

    }

}