package com.lollipop.qin1sptools.list

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.databinding.ItemTextBinding
import com.lollipop.qin1sptools.utils.bind

/**
 * @author lollipop
 * @date 2021/7/25 18:46
 */
class SimpleTextItemHolder(private val binding: ItemTextBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): SimpleTextItemHolder {
            return SimpleTextItemHolder(parent.bind(true))
        }
    }

    private val selectedColor =
        ContextCompat.getColor(itemView.context, R.color.itemSelectedBackground)
    private val defaultColor =
        ContextCompat.getColor(itemView.context, R.color.itemDefaultBackground)

    fun bind(text: String, isSelected: Boolean) {
        binding.root.setBackgroundColor(
            if (isSelected) {
                selectedColor
            } else {
                defaultColor
            }
        )
        binding.textView.text = text
    }
}