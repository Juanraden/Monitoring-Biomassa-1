package com.kedaireka.monitoring_biomassa.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kedaireka.monitoring_biomassa.ui.summary.biota.BiotaFragment
import com.kedaireka.monitoring_biomassa.ui.summary.keramba.InfoFragment
import com.kedaireka.monitoring_biomassa.ui.summary.pakan.FeedingFragment
import com.kedaireka.monitoring_biomassa.ui.summary.panen.PanenFragment


class SummaryFragmentTabAdapter constructor(fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int =  4

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> InfoFragment()
            1 -> BiotaFragment()
            2 -> FeedingFragment()
            else -> PanenFragment()
        }
    }
}