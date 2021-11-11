package com.kedaireka.monitoring_biomassa.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.kedaireka.monitoring_biomassa.adapter.KerambaListAdapter
import com.kedaireka.monitoring_biomassa.adapter.OnClickListener
import com.kedaireka.monitoring_biomassa.databinding.FragmentHomeBinding
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.kedaireka.monitoring_biomassa.R


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var navController: NavController

    private val kerambaViewModel by viewModels<KerambaViewModel>()

    private lateinit var kerambaListAdapter: KerambaListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this.viewLifecycleOwner

        navController = findNavController()

        setupNavigation()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kerambaListAdapter =KerambaListAdapter(
            OnClickListener { navController.navigate(HomeFragmentDirections.actionHomeFragmentToSummaryFragment()) }
        )

        setupKerambaList()

        setupToolbarSearch()
    }

    private fun setupKerambaList(){
        binding.kerambaList.adapter = kerambaListAdapter

        kerambaViewModel.allKeramba.observe(viewLifecycleOwner, {
            it.let {
                kerambaListAdapter.setData(it)
            }

            binding.loadingSpinner.visibility = View.GONE
        })

        kerambaViewModel.querySearch.observe(viewLifecycleOwner, {
            kerambaListAdapter.filter.filter(it)
        })
    }


    private fun setupToolbarSearch(){
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

    private fun setupNavigation(){

        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolbarFragment.setupWithNavController(navController, appBarConfiguration)

        binding.toolbarFragment.setNavigationOnClickListener {
            navController.navigateUp(appBarConfiguration)
        }
    }
}