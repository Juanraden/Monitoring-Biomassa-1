package com.kedaireka.monitoring_biomassa.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kedaireka.monitoring_biomassa.databinding.BottomSheetActionBinding
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetKeramba

class BottomSheetAction: BottomSheetDialogFragment() {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun showBottomSheetEditKeramba(){
        if (childFragmentManager.findFragmentByTag("BottomSheetKeramba") == null) {
            if (this.arguments != null) {
                val bottomSheetKeramba = BottomSheetKeramba()

                val bundle = Bundle()

                val kerambaId = arguments!!.getInt("keramba_id")

                bundle.putInt("keramba_id", kerambaId)

                bottomSheetKeramba.arguments = bundle

                bottomSheetKeramba.show(childFragmentManager, "BottomSheetKeramba")
            }
        }
    }
}