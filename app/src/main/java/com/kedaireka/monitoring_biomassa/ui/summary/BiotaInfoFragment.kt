package com.kedaireka.monitoring_biomassa.ui.summary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.databinding.FragmentBiotaInfoBinding
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetBiota
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString
import com.kedaireka.monitoring_biomassa.viewmodel.BiotaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BiotaInfoFragment : Fragment() {
    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private lateinit var binding: FragmentBiotaInfoBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBiotaInfoBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFragment()
    }

    private fun setupFragment() {
        biotaViewModel.loadedBiotaid.observe(viewLifecycleOwner, { biotaid ->
            biotaViewModel.loadBiotaData(biotaid).observe(viewLifecycleOwner, { biota ->
                with(binding) {
                    jenisBiotaTv.text = biota.jenis_biota

                    bobotBibitTv.text = biota.bobot.toString()

                    ukuranBibitTv.text = biota.panjang.toString()

                    jumlahBibitTv.text = biota.jumlah_bibit.toString()

                    tanggalTebarTv.text = convertLongToDateString(biota.tanggal_tebar)
                }
            })

            kerambaViewModel.loadedKerambaid.observe(viewLifecycleOwner, { kerambaid ->
                binding.editBtn.setOnClickListener { onEditBiota(kerambaid, biotaid) }
            })
        })
    }

    private fun onEditBiota(kerambaid: Int, biotaid: Int) {
        if (childFragmentManager.findFragmentByTag("BottomSheetBiota") == null) {

            val bundle = Bundle()

            bundle.apply {
                putInt("kerambaid", kerambaid)
                putInt("biotaid", biotaid)
            }

            val bottomSheetBiota = BottomSheetBiota()

            bottomSheetBiota.arguments = bundle

            bottomSheetBiota.show(childFragmentManager, "BottomSheetBiota")
        }
    }
}