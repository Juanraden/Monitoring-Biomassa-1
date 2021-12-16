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
import androidx.recyclerview.widget.ConcatAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedaireka.monitoring_biomassa.adapter.HeaderButtonAdapter
import com.kedaireka.monitoring_biomassa.adapter.PakanListAdapter
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.databinding.FragmentAddPakanBinding
import com.kedaireka.monitoring_biomassa.ui.action.BottomSheetAction
import com.kedaireka.monitoring_biomassa.ui.action.BottomSheetActionPakan
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetPakan
import com.kedaireka.monitoring_biomassa.viewmodel.PakanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPakanFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private val pakanViewModel by activityViewModels<PakanViewModel>()

    private lateinit var binding: FragmentAddPakanBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentAddPakanBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }

        setupPakanList()

        setupObserver()

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setupObserver() {
        pakanViewModel.init.observe(viewLifecycleOwner, {
            if (it == false) {
                pakanViewModel.startInit()
            }
        })


        pakanViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
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

                    binding.pakanList.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        pakanViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.pakanList.visibility = View.VISIBLE
                }
            }
        })

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setupPakanList() {
        val pakanListAdapter = PakanListAdapter {
            if (childFragmentManager.findFragmentByTag("BottomSheetAdd") == null && childFragmentManager.findFragmentByTag(
                    "BottomSheetActionPakan"
                ) == null
            ) {
                val bundle = Bundle()

                bundle.putInt("pakan_id", it.pakan_id)

                val bottomSheetAction = BottomSheetActionPakan()

                bottomSheetAction.arguments = bundle

                bottomSheetAction.show(childFragmentManager, "BottomSheetActionPakan")
            }
            true
        }

        binding.pakanList.adapter = pakanListAdapter

        pakanViewModel.getAll().observe(viewLifecycleOwner, {
            pakanListAdapter.submitList(it)
        })
    }

    override fun onRefresh() {
        pakanViewModel.fetchPakan()
    }
}