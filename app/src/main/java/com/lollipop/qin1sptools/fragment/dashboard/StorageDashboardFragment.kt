package com.lollipop.qin1sptools.fragment.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.databinding.FragmentDashboardStorageBinding
import com.lollipop.qin1sptools.task.ApplicationManager
import com.lollipop.qin1sptools.utils.CommonUtil
import com.lollipop.qin1sptools.utils.lazyBind
import com.lollipop.qin1sptools.utils.visibleOrGone
import com.lollipop.qin1sptools.view.VerticalProgressBar

class StorageDashboardFragment : BaseDashboardFragment() {

    private val binding: FragmentDashboardStorageBinding by lazyBind()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onRefresh() {
        updateInfo(ApplicationManager.getStorageInfo())
    }

    private fun updateInfo(storageInfo: ApplicationManager.StorageInfo) {
        storageInfo.run {
            binding.externalGroup.visibleOrGone(externalMounted) {
                updateProgress(
                    binding.externalProgressBar,
                    binding.externalValueView,
                    externalAvailable,
                    externalTotal
                )
            }
            updateProgress(
                binding.internalProgressBar,
                binding.internalValueView,
                internalAvailable,
                internalTotal
            )
        }
    }

    private fun updateProgress(
        progressBar: VerticalProgressBar,
        valueView: TextView,
        available: Long,
        total: Long,
    ) {
        progressBar.max = 100
        progressBar.progress = (available * 1F / total * 100).toInt()
        valueView.text = getString(
            R.string.avail_memory,
            getMemName(available),
            getMemName(total)
        )
    }

    private fun getMemName(value: Long): String {
        return CommonUtil.formatFileSize(value)
    }

}