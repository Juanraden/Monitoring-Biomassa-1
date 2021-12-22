package com.kedaireka.monitoring_biomassa.ui.action

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetPakan
import com.kedaireka.monitoring_biomassa.viewmodel.PakanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetActionPakan : BottomSheetAction() {
    private val pakanViewModel by activityViewModels<PakanViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pakanViewModel.requestDeleteResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Loading -> {

                }
                is NetworkResult.Loaded -> {

                    if (this.arguments != null) {

                        val pakanId = arguments!!.getInt("pakan_id")

                        pakanViewModel.deleteLocalPakan(pakanId)
                    }

                    pakanViewModel.doneDeleteRequest()

                    this@BottomSheetActionPakan.dismiss()
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

        if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetPakan") == null) {
            if (this.arguments != null) {
                val bottomSheetPakan = BottomSheetPakan()

                val bundle = Bundle()

                val pakanId = arguments!!.getInt("pakan_id")
                val editState = arguments!!.getBoolean("editState")

                bundle.putInt("pakan_id", pakanId)
                bundle.putBoolean("editState", editState)

                bottomSheetPakan.arguments = bundle

                bottomSheetPakan.show(
                    requireActivity().supportFragmentManager,
                    "BottomSheetPakan"
                )
            }
        }
    }

    override fun showAlertDialogDelete() {
        super.showAlertDialogDelete()

        if (this.arguments != null) {

            val pakanId = arguments!!.getInt("pakan_id")

            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Konfirmasi Hapus")

            builder.setMessage("Apa anda yakin untuk menghapus pakan ini?")

            builder.setPositiveButton("Ya") { _, _ ->

                pakanViewModel.deletePakan(pakanId)
            }

            builder.setNegativeButton("Batal") { _, _ -> }

            builder.show()
        }
    }
}