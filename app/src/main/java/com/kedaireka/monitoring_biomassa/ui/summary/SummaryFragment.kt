package com.kedaireka.monitoring_biomassa.ui.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.adapter.SummaryFragmentTabAdapter
import com.kedaireka.monitoring_biomassa.databinding.FragmentSummaryBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject as Inject


@AndroidEntryPoint
class SummaryFragment : Fragment() {

    private lateinit var binding: FragmentSummaryBinding

    @Inject lateinit var tabAdapter: SummaryFragmentTabAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSummaryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabLayout()
    }

    private fun setupTabLayout(){
        with(binding) {
            pager.adapter = tabAdapter

            TabLayoutMediator(tabLayout, pager){tab, position->
                when(position){
                    0 -> tab.text = getString(R.string.info)
                    1 -> tab.text = getString(R.string.pakan)
                    2 -> tab.text = getString(R.string.panen)
                }
            }.attach()
        }
    }
}