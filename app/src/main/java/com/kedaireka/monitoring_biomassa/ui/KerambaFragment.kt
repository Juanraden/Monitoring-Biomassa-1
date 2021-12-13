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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedaireka.monitoring_biomassa.adapter.KerambaListAdapter
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.databinding.FragmentKerambaBinding
import com.kedaireka.monitoring_biomassa.ui.action.BottomSheetActionKeramba
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KerambaFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentKerambaBinding

    private lateinit var navController: NavController

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private lateinit var kerambaListAdapter: KerambaListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentKerambaBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this.viewLifecycleOwner

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kerambaListAdapter = KerambaListAdapter(
            // clickListener
            {
                navController.navigate(HomeFragmentDirections.actionHomeFragmentToSummaryFragment())

                kerambaViewModel.setKerambaId(it.keramba_id)
            },

            // longClickListener
            {
                if (childFragmentManager.findFragmentByTag("BottomSheetAdd") == null && childFragmentManager.findFragmentByTag(
                        "BottomSheetActionKeramba"
                    ) == null
                ) {
                    val bundle = Bundle()

                    bundle.putInt("keramba_id", it.keramba_id)

                    val bottomSheetAction = BottomSheetActionKeramba()

                    bottomSheetAction.arguments = bundle

                    bottomSheetAction.show(childFragmentManager, "BottomSheetActionKeramba")
                }
                true
            }
        )

        setupKerambaList()

        setupQuerySearch()
    }

    private fun setupKerambaList() {
        binding.kerambaList.adapter = kerambaListAdapter

        kerambaViewModel.getAllKeramba().observe(viewLifecycleOwner, {
            it.let {
                kerambaListAdapter.setData(it)
            }

            val pendingQuery = kerambaViewModel.querySearch.value

            if (pendingQuery != null) {
                kerambaListAdapter.filter.filter(pendingQuery)
            }
        })

        kerambaViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
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

                    binding.kerambaList.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        kerambaViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
            }
        })

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setupQuerySearch() {
        kerambaViewModel.querySearch.observe(viewLifecycleOwner, { query ->
            kerambaListAdapter.filter.filter(query)
        })
    }

    override fun onRefresh() {
        kerambaViewModel.fetchKeramba()
    }
}