package com.kedaireka.monitoring_biomassa.ui.summary

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
import com.kedaireka.monitoring_biomassa.adapter.FeedingListAdapter
import com.kedaireka.monitoring_biomassa.adapter.HeaderButtonAdapter
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.databinding.FragmentPakanBinding
import com.kedaireka.monitoring_biomassa.ui.action.BottomSheetActionFeeding
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetFeeding
import com.kedaireka.monitoring_biomassa.viewmodel.FeedingViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PakanFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val feedingViewModel by activityViewModels<FeedingViewModel>()

    private lateinit var binding: FragmentPakanBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPakanBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchFeeding()

        setupFeedingList()

        setupObserver()

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setupObserver() {
        feedingViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
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

                    binding.feedingList.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        feedingViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.feedingList.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setupFeedingList() {
        val bundle = Bundle()

        Transformations.switchMap(kerambaViewModel.loadedKerambaId) { keramba_id ->

            bundle.putInt("keramba_id", keramba_id)
            bundle.putInt("feeding_id", 0)

            feedingViewModel.getAllFeeding(keramba_id)

        }.observe(viewLifecycleOwner, { listFeeding ->
            val feedingHeaderAdapter = HeaderButtonAdapter {
                if (childFragmentManager.findFragmentByTag("BottomSheetFeeding") == null) {

                    val bottomSheetFeeding = BottomSheetFeeding()

                    bottomSheetFeeding.arguments = bundle

                    bottomSheetFeeding.show(childFragmentManager, "BottomSheetFeeding")
                }
            }

            val feedingListAdapter = FeedingListAdapter(
                //onclick listener
                {
                },

                //long click listener
                {
                    if (childFragmentManager.findFragmentByTag("BottomSheetAdd") == null && childFragmentManager.findFragmentByTag(
                            "BottomSheetActionFeeding"
                        ) == null
                    ) {

                        val bottomSheetAction = BottomSheetActionFeeding()

                        val args = Bundle()

                        args.putInt("keramba_id", it.keramba_id)

                        args.putInt("feeding_id", it.feeding_id)

                        bottomSheetAction.arguments = args

                        bottomSheetAction.show(childFragmentManager, "BottomSheetActionFeeding")
                    }
                    true
                }
            )

            val concatAdapter = ConcatAdapter(feedingHeaderAdapter, feedingListAdapter)

            binding.feedingList.adapter = concatAdapter

            feedingListAdapter.submitList(listFeeding)

            if (feedingListAdapter.itemCount > 0) {
                binding.feedingList.smoothScrollToPosition(0)
            }
        })
    }

    private fun fetchFeeding(){
        if (kerambaViewModel.loadedKerambaId.value != null) {
            feedingViewModel.fetchFeeding(kerambaViewModel.loadedKerambaId.value!!)
        }
    }

    override fun onRefresh() {
        fetchFeeding()
    }
}