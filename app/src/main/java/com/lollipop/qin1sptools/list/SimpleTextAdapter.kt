package com.lollipop.qin1sptools.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * @author lollipop
 * @date 2021/7/25 18:47
 */
abstract class SimpleTextAdapter : RecyclerView.Adapter<SimpleTextItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleTextItemHolder {
        return SimpleTextItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: SimpleTextItemHolder, position: Int) {
        holder.bind(getText(position), selectedPosition == position)
    }

    abstract fun getText(position: Int): String

    abstract val selectedPosition: Int

}