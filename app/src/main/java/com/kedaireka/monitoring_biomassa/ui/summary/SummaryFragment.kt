package com.kedaireka.monitoring_biomassa.ui.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.adapter.SummaryFragmentTabAdapter
import com.kedaireka.monitoring_biomassa.databinding.FragmentSummaryBinding
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SummaryFragment : Fragment() {
    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private lateinit var binding: FragmentSummaryBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSummaryBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()

        setupTabLayout()
    }

    private fun setupTabLayout(){
        val tabAdapter = SummaryFragmentTabAdapter(this)

        with(binding) {
            pager.adapter = tabAdapter

            TabLayoutMediator(tabLayout, pager){tab, position->
                when(position){
                    0 -> tab.text = getString(R.string.info)
                    1 -> tab.text = getString(R.string.biota)
                    2 -> tab.text = getString(R.string.pakan)
                    3 -> tab.text = getString(R.string.panen)
                }
            }.attach()
        }
    }

    private fun setupNavigation(){
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.addFragment,R.id.settingsFragment))

        binding.toolbarFragment.setupWithNavController(navController, appBarConfiguration)

        binding.toolbarFragment.setNavigationOnClickListener {
            navController.navigateUp(appBarConfiguration)
        }

        kerambaViewModel.loadedKerambaid.observe(viewLifecycleOwner, {id->
            kerambaViewModel.loadKerambaData(id).observe(viewLifecycleOwner, {keramba->
                binding.toolbarFragment.title = keramba.nama_keramba
            })
        })
    }
}