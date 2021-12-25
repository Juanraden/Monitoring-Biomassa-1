package com.kedaireka.monitoring_biomassa.ui.summary.biota

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.ConcatAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedaireka.monitoring_biomassa.adapter.HeaderButtonAdapter
import com.kedaireka.monitoring_biomassa.adapter.PengukuranListAdapter
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.databinding.FragmentBiotaDataBinding
import com.kedaireka.monitoring_biomassa.ui.action.BottomSheetActionPengukuran
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetPengukuran
import com.kedaireka.monitoring_biomassa.viewmodel.BiotaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.PengukuranViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BiotaDataFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentBiotaDataBinding

    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private val pengukuranViewModel by activityViewModels<PengukuranViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBiotaDataBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPengukuranList()

        setupObserver()

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setupObserver() {
        pengukuranViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
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

                    binding.pengukuranList.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        pengukuranViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.pengukuranList.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setupPengukuranList() {

        val bundle = Bundle()

        Transformations.switchMap(biotaViewModel.loadedBiotaId) { biota_id ->
            biotaViewModel.loadBiotaData(biota_id)
        }.observe(viewLifecycleOwner, { biota ->

            bundle.putInt("biota_id", biota.biota_id)
            bundle.putInt("keramba_id", biota.keramba_id)
        })

        Transformations.switchMap(biotaViewModel.loadedBiotaId) { biota_id ->
            pengukuranViewModel.getAllBiotaData(biota_id)
        }.observe(viewLifecycleOwner, { list ->

            val headerButtonAdapter = HeaderButtonAdapter {
                if (childFragmentManager.findFragmentByTag("BottomSheetPengukuran") == null) {
                    val bottomSheetPengukuran = BottomSheetPengukuran()

                    bottomSheetPengukuran.arguments = bundle

                    bottomSheetPengukuran.show(childFragmentManager, "BottomSheetPengukuran")
                }
            }

            val pengukuranListAdapter = PengukuranListAdapter {
                if (childFragmentManager.findFragmentByTag("BottomSheetAdd") == null && childFragmentManager.findFragmentByTag(
                        "BottomSheetActionPengukuran"
                    ) == null
                ) {
                    val args = Bundle()

                    args.putInt("pengukuran_id", it.pengukuran_id)

                    val bottomSheetAction = BottomSheetActionPengukuran()

                    bottomSheetAction.arguments = args

                    bottomSheetAction.show(childFragmentManager, "BottomSheetActionPengukuran")
                }
                true
            }

            val concatAdapter = ConcatAdapter(headerButtonAdapter, pengukuranListAdapter)

            binding.pengukuranList.adapter = concatAdapter

            pengukuranListAdapter.submitList(list)
        })
    }

    override fun onRefresh() {
        if (biotaViewModel.loadedBiotaId.value != null) {
            pengukuranViewModel.fetchPengukuran(biotaViewModel.loadedBiotaId.value!!)
        }
    }
}