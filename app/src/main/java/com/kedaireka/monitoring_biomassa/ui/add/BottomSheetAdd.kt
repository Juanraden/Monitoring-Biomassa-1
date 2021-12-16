package com.kedaireka.monitoring_biomassa.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.databinding.BottomSheetAddBinding

class BottomSheetAdd : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetAddBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetAddBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheetAdd = this@BottomSheetAdd

        binding.cancelBtn.setOnClickListener { dismiss() }
    }

    fun launchBottomSheet(view: View) {
        when (view.id) {
            R.id.add_keramba_btn -> {
                if (childFragmentManager.findFragmentByTag("BottomSheetKeramba") == null) {
                    BottomSheetKeramba().show(childFragmentManager, "BottomSheetKeramba")
                }
            }
            R.id.add_pakan_btn -> {
                if (childFragmentManager.findFragmentByTag("BottomSheetPakan") == null) {
                    BottomSheetPakan().show(childFragmentManager, "BottomSheetPakan")
                }
            }
            R.id.add_biota_btn -> {
                if (childFragmentManager.findFragmentByTag("BottomSheetBiota") == null) {
                    BottomSheetBiota().show(childFragmentManager, "BottomSheetBiota")
                }
            }
        }
    }
}