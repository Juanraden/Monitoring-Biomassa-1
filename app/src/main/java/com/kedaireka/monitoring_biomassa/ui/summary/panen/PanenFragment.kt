package com.kedaireka.monitoring_biomassa.ui.summary.panen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Transformations
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedaireka.monitoring_biomassa.adapter.HeaderButtonAdapter
import com.kedaireka.monitoring_biomassa.adapter.PanenListAdapter
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.databinding.FragmentPanenBinding
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetPanen
import com.kedaireka.monitoring_biomassa.viewmodel.BiotaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.PanenViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PanenFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val panenViewModel by activityViewModels<PanenViewModel>()

    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private lateinit var binding: FragmentPanenBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPanenBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchBiotaHistory()

        setupPanenList()

        setupObserver()

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun fetchBiotaHistory() {
        if (kerambaViewModel.loadedKerambaId.value != null) {
            biotaViewModel.fetchBiotaHistory(kerambaViewModel.loadedKerambaId.value!!)
        }
    }


    private fun fetchPanen() {
        if (kerambaViewModel.loadedKerambaId.value != null) {
            panenViewModel.fetchPanen(kerambaViewModel.loadedKerambaId.value!!)
        }
    }

    private fun setupObserver() {
        biotaViewModel.requestGetHistoryResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {}
                is NetworkResult.Loading -> {
                    if (!binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = true
                    }
                }
                is NetworkResult.Loaded -> {
                    fetchPanen()
                }
                is NetworkResult.Error -> {
                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.panenList.visibility = View.VISIBLE
                }
            }
        })

        panenViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {}
                is NetworkResult.Loading -> {
                    if (!binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = true
                    }
                }
                is NetworkResult.Loaded -> {
                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.panenList.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        panenViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.panenList.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setupPanenList() {
        val panenHeaderAdapter = HeaderButtonAdapter {
            if (childFragmentManager.findFragmentByTag("BottomSheetPanen") == null) {

                val bundle = Bundle()

                val kerambaId = kerambaViewModel.loadedKerambaId.value!!

                bundle.putInt("keramba_id", kerambaId)

                val bottomSheetPanen = BottomSheetPanen()

                bottomSheetPanen.arguments = bundle

                bottomSheetPanen.show(childFragmentManager, "BottomSheetPanen")
            }
        }

        val panenListAdapter = PanenListAdapter()

        val concatAdapter = ConcatAdapter(panenHeaderAdapter, panenListAdapter)

        binding.panenList.adapter = concatAdapter

        Transformations.switchMap(kerambaViewModel.loadedKerambaId) { kerambaId ->
            panenViewModel.getlistPanen(kerambaId)
        }.observe(viewLifecycleOwner, {
            panenListAdapter.submitList(it)

            if (panenListAdapter.itemCount > 0) {
                binding.panenList.smoothScrollToPosition(0)
            }
        })


    }

    override fun onRefresh() {
        fetchBiotaHistory()
    }
}