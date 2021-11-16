package com.kedaireka.monitoring_biomassa.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kedaireka.monitoring_biomassa.databinding.BottomSheetAddBinding

class BottomSheetAdd: BottomSheetDialogFragment() {
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
        binding.cancelBtn.setOnClickListener { dismiss() }

        binding.addKerambaBtn.setOnClickListener { BottomSheetKeramba().show(childFragmentManager, "BottomSheetKeramba") }
    }
}