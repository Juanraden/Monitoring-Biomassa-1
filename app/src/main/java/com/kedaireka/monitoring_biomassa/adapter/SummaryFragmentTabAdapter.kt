package com.kedaireka.monitoring_biomassa.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kedaireka.monitoring_biomassa.ui.summary.BiotaFragment
import com.kedaireka.monitoring_biomassa.ui.summary.InfoFragment
import com.kedaireka.monitoring_biomassa.ui.summary.PakanFragment
import com.kedaireka.monitoring_biomassa.ui.summary.PanenFragment
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject


class SummaryFragmentTabAdapter constructor(fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int =  4

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> InfoFragment()
            1 -> BiotaFragment()
            2 -> PakanFragment()
            else -> PanenFragment()
        }
    }
}