package com.kedaireka.monitoring_biomassa.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.adapter.KerambaListAdapter
import com.kedaireka.monitoring_biomassa.databinding.FragmentHomeBinding
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var navController: NavController

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private lateinit var kerambaListAdapter: KerambaListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this.viewLifecycleOwner

        navController = findNavController()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()

        kerambaListAdapter = KerambaListAdapter {
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToSummaryFragment())

            kerambaViewModel.setKerambaId(it.kerambaid)
        }

        setupKerambaList()

        setupQuerySearch()

        setupToolbarSearch()
    }

    private fun setupKerambaList() {
        binding.kerambaList.adapter = kerambaListAdapter

        kerambaViewModel.getAllKeramba().observe(viewLifecycleOwner, {
            it.let {
                kerambaListAdapter.setData(it)
            }

            binding.loadingSpinner.visibility = View.GONE

            val pendingQuery = kerambaViewModel.querySearch.value

            if (pendingQuery != null){
                kerambaListAdapter.filter.filter(pendingQuery)
            }
        })
    }

    private fun setupQuerySearch() {
        kerambaViewModel.querySearch.observe(viewLifecycleOwner, {query->
                kerambaListAdapter.filter.filter(query)
        })
    }


    private fun setupToolbarSearch() {
        binding.toolbarFragment.inflateMenu(R.menu.search_menu)

        val search = binding.toolbarFragment.menu.findItem(R.id.appSearchBar)

        val searchView = search.actionView as SearchView

        searchView.queryHint = "Nama Keramba"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                kerambaViewModel.setQuerySearch(newText)
                return true
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
}