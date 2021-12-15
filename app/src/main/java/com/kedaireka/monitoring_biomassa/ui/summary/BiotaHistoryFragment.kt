package com.kedaireka.monitoring_biomassa.ui.summary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Transformations
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.adapter.BiotaHistoryListAdapter
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.databinding.FragmentBiotaHistoryBinding
import com.kedaireka.monitoring_biomassa.viewmodel.BiotaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BiotaHistoryFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
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

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setupBiotaHistoryList() {
        val biotaHistoryListAdapter = BiotaHistoryListAdapter()

        binding.biotaHistoryList.adapter = biotaHistoryListAdapter

        Transformations.switchMap(kerambaViewModel.loadedKerambaId) { keramba_id ->
            biotaViewModel.fetchBiotaHistory(keramba_id)

            biotaViewModel.getAllBiotaHistory(keramba_id)
        }.observe(viewLifecycleOwner, {
            biotaHistoryListAdapter.submitList(it)
        })

        biotaViewModel.requestGetHistoryResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    if (!binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = true
                    }
                }
                is NetworkResult.Loaded -> {
                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.biotaHistoryList.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        kerambaViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.biotaHistoryList.visibility = View.VISIBLE
                }
            }
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

    override fun onRefresh() {
        if (kerambaViewModel.loadedKerambaId.value != null) {
            biotaViewModel.fetchBiotaHistory(kerambaViewModel.loadedKerambaId.value!!)
        }
    }
}