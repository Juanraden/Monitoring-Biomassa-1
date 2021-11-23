package com.kedaireka.monitoring_biomassa.ui.summary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Transformations
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.adapter.BiotaHistoryListAdapter
import com.kedaireka.monitoring_biomassa.databinding.FragmentBiotaHistoryBinding
import com.kedaireka.monitoring_biomassa.viewmodel.BiotaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BiotaHistoryFragment : Fragment() {
    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val biotaViewModel by viewModels<BiotaViewModel>()

    private lateinit var binding: FragmentBiotaHistoryBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBiotaHistoryBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        setupNavigation()

        setupBiotaHistoryList()
    }

    private fun setupBiotaHistoryList() {
        val biotaHistoryListAdapter = BiotaHistoryListAdapter()

        binding.biotaHistoryList.adapter = biotaHistoryListAdapter

        Transformations.switchMap(kerambaViewModel.loadedKerambaid){ kerambaid ->
            biotaViewModel.getAllBiotaHistory(kerambaid)
        }.observe(viewLifecycleOwner, {
            biotaHistoryListAdapter.submitList(it)

            binding.loadingSpinner.visibility = View.GONE
        })
    }

    private fun setupNavigation() {

        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.homeFragment, R.id.settingsFragment))

        binding.toolbarFragment.setupWithNavController(navController, appBarConfiguration)

        binding.toolbarFragment.setNavigationOnClickListener {
            navController.navigateUp(appBarConfiguration)
        }
    }
}