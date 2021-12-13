package com.kedaireka.monitoring_biomassa.ui.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Transformations
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.kedaireka.monitoring_biomassa.adapter.BiotaListAdapter
import com.kedaireka.monitoring_biomassa.adapter.HeaderButtonAdapter
import com.kedaireka.monitoring_biomassa.databinding.FragmentBiotaBinding
import com.kedaireka.monitoring_biomassa.ui.action.BottomSheetActionBiota
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetBiota
import com.kedaireka.monitoring_biomassa.viewmodel.BiotaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BiotaFragment : Fragment() {

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private lateinit var binding: FragmentBiotaBinding

    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBiotaBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBiotaList()
    }

    private fun setupBiotaList() {
        val bundle = Bundle()
        Transformations.switchMap(kerambaViewModel.loadedKerambaId) { keramba_id ->

            bundle.putInt("keramba_id", keramba_id)
            bundle.putInt("biota_id", 0)

            biotaViewModel.getAllBiota(keramba_id)

        }.observe(viewLifecycleOwner, { listBiota ->
            val biotaHeaderAdapter = HeaderButtonAdapter {
                if (childFragmentManager.findFragmentByTag("BottomSheetBiota") == null) {

                    val bottomSheetBiota = BottomSheetBiota()

                    bottomSheetBiota.arguments = bundle

                    bottomSheetBiota.show(childFragmentManager, "BottomSheetBiota")
                }
            }

            val biotaListAdapter = BiotaListAdapter(
                { biota ->
                    navController.navigate(SummaryFragmentDirections.actionSummaryFragmentToBiotaTabFragment())

                    biotaViewModel.setBiotaId(biota.biota_id)
                },

                //long click listener
                {
                    if (childFragmentManager.findFragmentByTag("BottomSheetAdd") == null && childFragmentManager.findFragmentByTag(
                            "BottomSheetActionBiota"
                        ) == null
                    ) {

                        val bottomSheetAction = BottomSheetActionBiota()

                        val args = Bundle()

                        args.putInt("keramba_id", it.keramba_id)

                        args.putInt("biota_id", it.biota_id)

                        bottomSheetAction.arguments = args

                        bottomSheetAction.show(childFragmentManager, "BottomSheetActionBiota")
                    }
                    true
                })

            val concatAdapter = ConcatAdapter(biotaHeaderAdapter, biotaListAdapter)

            binding.biotaList.adapter = concatAdapter

            biotaListAdapter.submitList(listBiota)

            binding.loadingSpinner.visibility = View.GONE
        })
    }
}