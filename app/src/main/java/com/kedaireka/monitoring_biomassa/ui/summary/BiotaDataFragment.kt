package com.kedaireka.monitoring_biomassa.ui.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.ConcatAdapter
import com.kedaireka.monitoring_biomassa.adapter.HeaderButtonAdapter
import com.kedaireka.monitoring_biomassa.adapter.PengukuranListAdapter
import com.kedaireka.monitoring_biomassa.databinding.FragmentBiotaDataBinding
import com.kedaireka.monitoring_biomassa.ui.BottomSheetAction
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetPengukuran
import com.kedaireka.monitoring_biomassa.viewmodel.BiotaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.PengukuranViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BiotaDataFragment : Fragment() {
    private lateinit var binding: FragmentBiotaDataBinding

    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private val pengukuranViewModel by activityViewModels<PengukuranViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBiotaDataBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPengukuranList()
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
            pengukuranViewModel.getAll(biota_id)
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
                        "BottomSheetAction"
                    ) == null
                ) {

                    val bottomSheetAction = BottomSheetAction()

                    bottomSheetAction.show(childFragmentManager, "BottomSheetAction")
                }
                true
            }

            val concatAdapter = ConcatAdapter(headerButtonAdapter, pengukuranListAdapter)

            binding.pengukuranList.adapter = concatAdapter

            pengukuranListAdapter.submitList(list)

            binding.loadingSpinner.visibility = View.GONE
        })
    }
}