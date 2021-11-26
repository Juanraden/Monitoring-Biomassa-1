package com.kedaireka.monitoring_biomassa.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.kedaireka.monitoring_biomassa.adapter.KerambaListAdapter
import com.kedaireka.monitoring_biomassa.databinding.FragmentKerambaBinding
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KerambaFragment : Fragment() {
    private lateinit var binding: FragmentKerambaBinding

    private lateinit var navController: NavController

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private lateinit var kerambaListAdapter: KerambaListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentKerambaBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this.viewLifecycleOwner

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kerambaListAdapter = KerambaListAdapter {
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToSummaryFragment())

            kerambaViewModel.setKerambaId(it.keramba_id)
        }

        setupKerambaList()

        setupQuerySearch()
    }

    private fun setupKerambaList() {
        binding.kerambaList.adapter = kerambaListAdapter

        kerambaViewModel.getAllKeramba().observe(viewLifecycleOwner, {
            it.let {
                kerambaListAdapter.setData(it)

                binding.loadingSpinner.visibility = View.GONE
            }

            val pendingQuery = kerambaViewModel.querySearch.value

            if (pendingQuery != null){
                kerambaListAdapter.filter.filter(pendingQuery)
            }
        })

        kerambaViewModel.exception.observe(viewLifecycleOwner, {
            if(it != "") {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()

                kerambaViewModel.doneToastException()
            }
        })
    }

    private fun setupQuerySearch() {
        kerambaViewModel.querySearch.observe(viewLifecycleOwner, {query->
            kerambaListAdapter.filter.filter(query)
        })
    }
}