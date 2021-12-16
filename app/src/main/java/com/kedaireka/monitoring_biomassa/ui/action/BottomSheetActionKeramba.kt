package com.kedaireka.monitoring_biomassa.ui.action

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetKeramba
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetActionKeramba : BottomSheetAction() {
    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    override fun showBottomSheetEdit() {
        super.showBottomSheetEdit()

        if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetKeramba") == null) {
            if (this.arguments != null) {
                val bottomSheetKeramba = BottomSheetKeramba()

                val bundle = Bundle()

                val kerambaId = arguments!!.getInt("keramba_id")

                bundle.putInt("keramba_id", kerambaId)

                bottomSheetKeramba.arguments = bundle

                bottomSheetKeramba.show(
                    requireActivity().supportFragmentManager,
                    "BottomSheetKeramba"
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kerambaViewModel.requestDeleteResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Loading -> {

                }
                is NetworkResult.Loaded -> {

                    if (this.arguments != null) {

                        val kerambaId = arguments!!.getInt("keramba_id")

                        kerambaViewModel.deleteLocalKeramba(kerambaId)
                    }

                    kerambaViewModel.doneDeleteRequest()

                    this@BottomSheetActionKeramba.dismiss()
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        kerambaViewModel.doneToastException()
                    }
                }
            }
        })
    }

    override fun showAlertDialogDelete() {
        super.showAlertDialogDelete()

        if (this.arguments != null) {

            val kerambaId = arguments!!.getInt("keramba_id")

            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Konfirmasi Hapus")

            builder.setMessage("Apa anda yakin untuk menghapus keramba ini?")

            builder.setPositiveButton("Ya") { _, _ ->

                kerambaViewModel.deleteKeramba(kerambaId)
            }

            builder.setNegativeButton("Batal") { _, _ -> }

            builder.show()
        }
    }
}