package com.lollipop.qin1sptools.activity

import android.os.Bundle
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.databinding.ActivityFileChooseBinding
import com.lollipop.qin1sptools.utils.lazyBind

/**
 * 文件选择的Activity
 * 可以按照要求选中一个文件夹或者某个文件
 * @author Lollipop
 * @date 2021/07/23
 */
class FileChooseActivity : FeatureBarActivity() {

    private val binding: ActivityFileChooseBinding by lazyBind()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding)
        initView()
        initData()
    }

    private fun initView() {
        setCenterFeatureButton(R.drawable.featurebar_select)
    }

    private fun initData() {

    }

}