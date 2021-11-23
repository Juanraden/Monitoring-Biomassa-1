package com.kedaireka.monitoring_biomassa.ui.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Transformations
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.adapter.BiotaFragmentTabAdapter
import com.kedaireka.monitoring_biomassa.databinding.FragmentBiotaTabBinding
import com.kedaireka.monitoring_biomassa.viewmodel.BiotaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BiotaTabFragment : Fragment() {
    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private lateinit var binding: FragmentBiotaTabBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBiotaTabBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()

        setupTabLayout()
    }

    private fun setupTabLayout() {
        val adapter = BiotaFragmentTabAdapter(this)

        with(binding){
            pager.adapter = adapter

            TabLayoutMediator(tabLayout, pager){tab, position->
                when(position){
                    0 -> tab.text = getString(R.string.info)
                    1 -> tab.text = getString(R.string.data)
                }
            }.attach()
        }
    }
    private fun setupNavigation() {
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.homeFragment, R.id.settingsFragment))

        binding.toolbarFragment.setupWithNavController(navController, appBarConfiguration)

        binding.toolbarFragment.setNavigationOnClickListener {
            navController.navigateUp(appBarConfiguration)
        }

        Transformations.switchMap(biotaViewModel.loadedBiotaid){ biotaid ->
            biotaViewModel.loadBiotaData(biotaid)
        }.observe(viewLifecycleOwner, {biota->
            binding.toolbarFragment.title = biota.jenis_biota
        })
    }
}