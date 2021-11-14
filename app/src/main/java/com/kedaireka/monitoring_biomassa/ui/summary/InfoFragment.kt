package com.kedaireka.monitoring_biomassa.ui.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.databinding.FragmentInfoBinding
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class InfoFragment : Fragment() {
    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private lateinit var binding: FragmentInfoBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInfoBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setupFragment()
    }

    private fun setupFragment() {
        kerambaViewModel.loadedKerambaid.observe(viewLifecycleOwner, { id ->
            kerambaViewModel.loadKerambaData(id).observe(viewLifecycleOwner, { keramba ->
                with(binding) {
                    namaKerambaTv.text = keramba.nama_keramba

                    tanggalInstallTv.text = convertLongToDateString(keramba.tanggal_install)

                    ukuranKerambaTv.text =
                        getString(R.string.meter_kubik, keramba.ukuran.toString())

                    editBtn.setOnClickListener {
                        navController.navigate(
                            SummaryFragmentDirections.actionSummaryFragmentToAddKerambaFragment(
                                keramba.kerambaid
                            )
                        )
                    }
                }
            })

        })
    }

}