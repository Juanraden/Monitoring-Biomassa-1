package com.kedaireka.monitoring_biomassa.ui.summary.pakan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.adapter.FeedingDetailListAdapter
import com.kedaireka.monitoring_biomassa.adapter.HeaderButtonAdapter
import com.kedaireka.monitoring_biomassa.adapter.HeaderCardAdapter
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.databinding.FragmentFeedingDetailBinding
import com.kedaireka.monitoring_biomassa.ui.action.BottomSheetActionFeedingDetail
import com.kedaireka.monitoring_biomassa.ui.action.BottomSheetActionPengukuran
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetFeeding
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetFeedingDetail
import com.kedaireka.monitoring_biomassa.viewmodel.FeedingDetailViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.PakanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedingDetailFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentFeedingDetailBinding

    private lateinit var navController: NavController

    private val args by navArgs<FeedingDetailFragmentArgs>()

    private val feedingDetailViewModel by activityViewModels<FeedingDetailViewModel>()

    private val pakanViewModel by activityViewModels<PakanViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for kerambaIdthis fragment
        binding = FragmentFeedingDetailBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()

        setupFeedingDetailList()

        setupObserver()

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setupFeedingDetailList() {
        val feedingDetailHeaderAdapter = HeaderButtonAdapter {
            if (childFragmentManager.findFragmentByTag("BottomSheetFeedingDetail") == null) {
                val bottomSheetFeedingDetail = BottomSheetFeedingDetail()

                val bundle = Bundle()

                bundle.putInt("feeding_id", args.feedingId)

                bottomSheetFeedingDetail.arguments = bundle

                bottomSheetFeedingDetail.show(childFragmentManager, "BottomSheetFeedingDetail")
            }
        }

        val clickListener: (Int) -> Unit = { feedingId: Int ->
            if (childFragmentManager.findFragmentByTag("BottomSheetFeeding") == null) {

                val bundle = Bundle()

                bundle.putInt("feeding_id", feedingId)
                val bottomSheetFeeding = BottomSheetFeeding()

                bottomSheetFeeding.arguments = bundle

                bottomSheetFeeding.show(childFragmentManager, "BottomSheetFeeding")
            }
        }

        val feedingDetailListAdapter = FeedingDetailListAdapter {
            if (childFragmentManager.findFragmentByTag("BottomSheetAdd") == null && childFragmentManager.findFragmentByTag(
                    "BottomSheetActionFeedingDetail"
                ) == null
            ) {
                val args = Bundle()

                args.putInt("detail_id", it.detail_id)

                val bottomSheetAction = BottomSheetActionFeedingDetail()

                bottomSheetAction.arguments = args

                bottomSheetAction.show(childFragmentManager, "BottomSheetActionFeedingDetail")
            }
            true
        }

        feedingDetailViewModel.loadFeedingData(args.feedingId).observe(viewLifecycleOwner, {

            val headerCardAdapter = HeaderCardAdapter(
                tanggal = it.tanggal_feeding,
                clickListener = { clickListener(args.feedingId) })

            val concatAdapter =
                ConcatAdapter(
                    headerCardAdapter,
                    feedingDetailHeaderAdapter,
                    feedingDetailListAdapter
                )

            binding.feedingDetailList.adapter = concatAdapter
        })


        feedingDetailViewModel.getAllFeedingDetailAndPakan(args.feedingId)
            .observe(viewLifecycleOwner, { list ->
                feedingDetailListAdapter.submitList(list)
            })
    }

    private fun setupObserver() {
        feedingDetailViewModel.loadKerambaData(args.kerambaId).observe(viewLifecycleOwner, {
            binding.toolbarFragment.title = "Pakan ${it.nama_keramba}"
        })

        pakanViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                }
                is NetworkResult.Loading -> {
                    if (!binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = true
                    }
                }
                is NetworkResult.Loaded -> {
                    feedingDetailViewModel.fetchFeedingDetail(args.feedingId)
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        pakanViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.feedingDetailList.visibility = View.VISIBLE
                }
            }
        })

        feedingDetailViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                }
                is NetworkResult.Loading -> {
                    if (!binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = true
                    }
                }
                is NetworkResult.Loaded -> {
                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.feedingDetailList.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        feedingDetailViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.feedingDetailList.visibility = View.VISIBLE
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
        feedingDetailViewModel.fetchFeedingDetail(args.feedingId)
    }
}