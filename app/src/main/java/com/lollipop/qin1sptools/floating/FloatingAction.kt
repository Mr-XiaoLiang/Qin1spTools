package com.lollipop.qin1sptools.floating

import android.content.Context
import android.widget.ImageView

interface FloatingAction {

    fun loadIcon(imageView: ImageView)

    fun invoke(context: Context)

}