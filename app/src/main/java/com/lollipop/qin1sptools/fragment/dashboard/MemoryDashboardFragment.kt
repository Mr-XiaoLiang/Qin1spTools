package com.lollipop.qin1sptools.fragment.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lollipop.qin1sptools.databinding.FragmentDashboardMemoryBinding
import com.lollipop.qin1sptools.utils.lazyBind

/**
 * @author lollipop
 * @date 2021/9/16 21:39
 */
class MemoryDashboardFragment: Fragment() {

    private val binding: FragmentDashboardMemoryBinding by lazyBind()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}