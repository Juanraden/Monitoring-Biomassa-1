package com.kedaireka.monitoring_biomassa.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kedaireka.monitoring_biomassa.ui.summary.InfoFragment
import com.kedaireka.monitoring_biomassa.ui.summary.PakanFragment
import com.kedaireka.monitoring_biomassa.ui.summary.PanenFragment
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class SummaryFragmentTabAdapter @Inject constructor(fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int =  3

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> InfoFragment()
            1 -> PakanFragment()
            else  -> PanenFragment()
        }
    }
}