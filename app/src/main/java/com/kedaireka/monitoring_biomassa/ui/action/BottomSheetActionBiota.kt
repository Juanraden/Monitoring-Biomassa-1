package com.kedaireka.monitoring_biomassa.ui.action

import android.os.Bundle
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetBiota

class BottomSheetActionBiota: BottomSheetAction() {
    override fun showBottomSheetEdit() {
        super.showBottomSheetEdit()

        if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetBiota") == null) {
            if (this.arguments != null) {
                val bottomSheetBiota = BottomSheetBiota()

                val bundle = Bundle()

                val kerambaId = arguments!!.getInt("keramba_id")

                val biotaId = arguments!!.getInt("biota_id")

                bundle.putInt("keramba_id", kerambaId)

                bundle.putInt("biota_id", biotaId)

                bottomSheetBiota.arguments = bundle

                bottomSheetBiota.show(requireActivity().supportFragmentManager, "BottomSheetBiota")
            }

            this@BottomSheetActionBiota.dismiss()
        }
    }
}