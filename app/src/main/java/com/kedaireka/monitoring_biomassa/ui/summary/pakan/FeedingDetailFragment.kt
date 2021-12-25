package com.kedaireka.monitoring_biomassa.ui.summary.pakan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.adapter.FeedingDetailListAdapter
import com.kedaireka.monitoring_biomassa.adapter.HeaderButtonAdapter
import com.kedaireka.monitoring_biomassa.databinding.FragmentFeedingDetailBinding
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetFeedingDetail
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetPakan
import com.kedaireka.monitoring_biomassa.viewmodel.FeedingDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedingDetailFragment : Fragment() {

    private lateinit var binding: FragmentFeedingDetailBinding

    private lateinit var navController: NavController

    private val args by navArgs<FeedingDetailFragmentArgs>()

    private val feedingDetailViewModel by viewModels<FeedingDetailViewModel>()

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

        setupObserver()

        setupFeedingDetailList()
    }

    private fun setupFeedingDetailList() {
        val feedingDetailHeaderAdapter = HeaderButtonAdapter {
            if (childFragmentManager.findFragmentByTag("BottomSheetFeedingDetail") == null) {
                val bottomSheetFeedingDetail = BottomSheetFeedingDetail()

                val bundle = Bundle()

                bottomSheetFeedingDetail.arguments = bundle

                bottomSheetFeedingDetail.show(childFragmentManager, "BottomSheetFeedingDetail")
            }
        }

        val feedingDetailListAdapter = FeedingDetailListAdapter { true }

        val concatAdapter = ConcatAdapter(feedingDetailHeaderAdapter, feedingDetailListAdapter)

        binding.feedingDetailList.adapter = concatAdapter

        feedingDetailViewModel.getAllFeedingDetailAndPakan(args.feedingId)
            .observe(viewLifecycleOwner, {
                feedingDetailListAdapter.submitList(it)

                binding.feedingDetailList.visibility = View.VISIBLE
            })
    }

    private fun setupObserver() {
        feedingDetailViewModel.loadKerambaData(args.kerambaId).observe(viewLifecycleOwner, {
            binding.namaKerambaTv.text = it.nama_keramba

            binding.toolbarFragment.title = "Pakan ${it.nama_keramba}"
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