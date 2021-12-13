package com.kedaireka.monitoring_biomassa.ui.action

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kedaireka.monitoring_biomassa.databinding.BottomSheetActionBinding

open class BottomSheetAction: BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetActionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetActionBinding.inflate(inflater)

        binding.bottomSheetAction = this@BottomSheetAction

        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    open fun showBottomSheetEdit(){}

    open fun showAlertDialogDelete(){}
}