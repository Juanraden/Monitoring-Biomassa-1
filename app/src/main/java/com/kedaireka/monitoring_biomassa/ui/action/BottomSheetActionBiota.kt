package com.kedaireka.monitoring_biomassa.ui.action

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetBiota
import com.kedaireka.monitoring_biomassa.viewmodel.BiotaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetActionBiota : BottomSheetAction() {
    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        biotaViewModel.requestDeleteResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Loading -> {

                }
                is NetworkResult.Loaded -> {

                    if (this.arguments != null) {

                        val biotaId = arguments!!.getInt("biota_id")

                        biotaViewModel.deleteLocalBiota(biotaId)
                    }

                    biotaViewModel.doneDeleteRequest()

                    this@BottomSheetActionBiota.dismiss()
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

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

    override fun showAlertDialogDelete() {
        super.showAlertDialogDelete()

        if (this.arguments != null) {

            val biotaId = arguments!!.getInt("biota_id")

            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Konfirmasi Hapus")

            builder.setMessage("Apa anda yakin untuk menghapus biota ini?")

            builder.setPositiveButton("Ya") { _, _ ->

                biotaViewModel.deleteBiota(biotaId)
            }

            builder.setNegativeButton("Batal") { _, _ -> }

            builder.show()
        }
    }
}