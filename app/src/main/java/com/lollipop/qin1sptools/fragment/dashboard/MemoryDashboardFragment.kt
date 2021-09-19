package com.lollipop.qin1sptools.fragment.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.databinding.FragmentDashboardMemoryBinding
import com.lollipop.qin1sptools.task.ApplicationManager
import com.lollipop.qin1sptools.utils.lazyBind
import java.text.DecimalFormat

/**
 * @author lollipop
 * @date 2021/9/16 21:39
 */
class MemoryDashboardFragment : Fragment() {

    private val binding: FragmentDashboardMemoryBinding by lazyBind()

    companion object {
        private val UNIT_ARRAY = arrayOf("B", "KB", "MB", "GB", "TB")
        private const val RADIX = 1024L
    }

    private val decimalFormat by lazy {
        DecimalFormat("#0.00")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val c = context
        if (c != null) {
            val memoryInfo = ApplicationManager.getMemoryInfo(c)
            updateMemInfo(memoryInfo.totalMem, memoryInfo.availMem, memoryInfo.threshold)
        } else {
            updateMemInfo(-1, -1, 0)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateMemInfo(totalMem: Long, availMem: Long, threshold: Long) {
        if (totalMem <= 0 || availMem <= 0) {
            binding.availMemProgressBar.apply {
                max = 100
                progress = 100
            }
            binding.thresholdProgressBar.apply {
                max = 100
                progress = 100
            }
            binding.memValueView.text = getString(R.string.memory_error)
            return
        }
        binding.availMemProgressBar.apply {
            max = 100
            progress = (availMem * 1F / totalMem * 100).toInt()
        }
        binding.thresholdProgressBar.apply {
            max = 100
            progress = (threshold * 1F / totalMem * 100).toInt()
        }
        binding.memValueView.text = getString(
            R.string.avail_memory,
            getMemName(availMem),
            getMemName(totalMem)
        )
    }

    private fun getMemName(value: Long): String {
        var unit = 0
        var num = value.toFloat()
        while (num > RADIX) {
            if (unit >= UNIT_ARRAY.size - 1) {
                break
            }
            num /= RADIX
            unit++
        }
        return decimalFormat.format(num) + UNIT_ARRAY[unit]
    }

}