package com.kedaireka.monitoring_biomassa.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kedaireka.monitoring_biomassa.ui.KerambaFragment
import com.kedaireka.monitoring_biomassa.ui.PakanFragment

class HomeFragmentTabAdapter constructor(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int =  2

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> KerambaFragment()
            else -> PakanFragment()
        }
    }
}