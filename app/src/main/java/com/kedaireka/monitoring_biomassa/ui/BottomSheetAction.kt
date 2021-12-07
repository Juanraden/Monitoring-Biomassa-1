package com.kedaireka.monitoring_biomassa.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kedaireka.monitoring_biomassa.databinding.BottomSheetActionBinding
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetKeramba
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetPakan

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
        if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetKeramba") == null) {
            if (this.arguments != null) {
                val bottomSheetKeramba = BottomSheetKeramba()

                val bundle = Bundle()

                val kerambaId = arguments!!.getInt("keramba_id")

                bundle.putInt("keramba_id", kerambaId)

                bottomSheetKeramba.arguments = bundle

                bottomSheetKeramba.show(requireActivity().supportFragmentManager, "BottomSheetKeramba")
            }

            this@BottomSheetAction.dismiss()
        }
    }

    fun showBottomSheetEditPakan(){
        if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetPakan") == null) {
            if (this.arguments != null) {
                val bottomSheetPakan = BottomSheetPakan()

                val bundle = Bundle()

                val pakanId = arguments!!.getInt("pakan_id")

                bundle.putInt("pakan_id", pakanId)

                bottomSheetPakan.arguments = bundle

                bottomSheetPakan.show(requireActivity().supportFragmentManager, "BottomSheetPakan")
            }

            this@BottomSheetAction.dismiss()
        }
    }
}