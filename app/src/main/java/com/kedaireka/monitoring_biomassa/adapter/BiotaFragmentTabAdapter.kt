package com.kedaireka.monitoring_biomassa.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kedaireka.monitoring_biomassa.ui.summary.BiotaDataFragment
import com.kedaireka.monitoring_biomassa.ui.summary.BiotaInfoFragment

class BiotaFragmentTabAdapter constructor(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BiotaInfoFragment()
            else -> BiotaDataFragment()
        }
    }
}