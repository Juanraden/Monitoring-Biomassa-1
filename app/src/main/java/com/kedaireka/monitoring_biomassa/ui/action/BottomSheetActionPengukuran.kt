package com.kedaireka.monitoring_biomassa.ui.action

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.viewmodel.PengukuranViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetActionPengukuran:BottomSheetAction() {
    private val pengukuranViewModel by activityViewModels<PengukuranViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.binding.editBtn.visibility = View.GONE

        pengukuranViewModel.requestDeleteResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Loading -> {

                }
                is NetworkResult.Loaded -> {

                    if (this.arguments != null) {

                        val pengukuranId = arguments!!.getInt("pengukuran_id")

                        pengukuranViewModel.deleteLocalPengukuran(pengukuranId)
                    }

                    pengukuranViewModel.doneDeleteRequest()

                    this@BottomSheetActionPengukuran.dismiss()
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun showAlertDialogDelete() {
        super.showAlertDialogDelete()

        if (this.arguments != null) {
            val pengukuranId = arguments!!.getInt("pengukuran_id")

            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Konfirmasi Hapus")

            builder.setMessage("Apa anda yakin untuk menghapus data biota ini?")

            builder.setPositiveButton("Ya") { dialog, id ->

                pengukuranViewModel.deletePengukuran(pengukuranId)

            }

            builder.setNegativeButton("Batal") { dialog, id -> }

            builder.show()
        }
    }
}