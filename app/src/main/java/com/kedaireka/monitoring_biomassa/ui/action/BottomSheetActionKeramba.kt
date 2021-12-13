package com.kedaireka.monitoring_biomassa.ui.action

import android.app.AlertDialog
import android.os.Bundle
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetKeramba

class BottomSheetActionKeramba: BottomSheetAction(){
    override fun showBottomSheetEdit() {
        super.showBottomSheetEdit()

        if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetKeramba") == null) {
            if (this.arguments != null) {
                val bottomSheetKeramba = BottomSheetKeramba()

                val bundle = Bundle()

                val kerambaId = arguments!!.getInt("keramba_id")

                bundle.putInt("keramba_id", kerambaId)

                bottomSheetKeramba.arguments = bundle

                bottomSheetKeramba.show(requireActivity().supportFragmentManager, "BottomSheetKeramba")
            }

            this@BottomSheetActionKeramba.dismiss()
        }
    }

    override fun showAlertDialogDelete() {
        super.showAlertDialogDelete()

        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Konfirmasi Hapus")

        builder.setMessage("Apa anda yakin untuk menghapus keramba ini?")

        builder.setPositiveButton("Ya"){dialog, id-> }

        builder.setNegativeButton("Batal"){dialog,id->}

        builder.show()

        this@BottomSheetActionKeramba.dismiss()
    }
}