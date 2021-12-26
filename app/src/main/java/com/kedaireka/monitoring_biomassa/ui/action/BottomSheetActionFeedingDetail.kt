package com.kedaireka.monitoring_biomassa.ui.action

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.viewmodel.FeedingDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetActionFeedingDetail: BottomSheetAction() {
    private val feedingDetailViewModel by activityViewModels<FeedingDetailViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.binding.editBtn.visibility = View.GONE

        feedingDetailViewModel.requestDeleteResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {}
                is NetworkResult.Loading -> {}
                is NetworkResult.Loaded -> {
                    if (result.message != ""){
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    if (this.arguments != null) {

                        val detailId = arguments!!.getInt("detail_id")

                        feedingDetailViewModel.deleteLocalFeedingDetail(detailId)
                    }

                    feedingDetailViewModel.doneDeleteRequest()

                    this@BottomSheetActionFeedingDetail.dismiss()
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    feedingDetailViewModel.doneDeleteRequest()
                }
            }
        })


    }

    override fun showAlertDialogDelete() {
        super.showAlertDialogDelete()

        if (this.arguments != null) {
            val detailId = arguments!!.getInt("detail_id")

            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Konfirmasi Hapus")

            builder.setMessage("Apa anda yakin untuk menghapus data ini?")

            builder.setPositiveButton("Ya") { _, _ ->
                feedingDetailViewModel.deleteFeedingDetail(detailId)
            }

            builder.setNegativeButton("Batal") { _, _ -> }

            builder.show()
        }
    }
}