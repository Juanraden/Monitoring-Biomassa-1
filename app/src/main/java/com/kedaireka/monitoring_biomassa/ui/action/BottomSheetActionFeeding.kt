package com.kedaireka.monitoring_biomassa.ui.action

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetFeeding
import com.kedaireka.monitoring_biomassa.viewmodel.FeedingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetActionFeeding : BottomSheetAction() {
    private val feedingViewModel by activityViewModels<FeedingViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedingViewModel.requestDeleteResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {}
                is NetworkResult.Loading -> {}
                is NetworkResult.Loaded -> {
                    if (result.message != ""){
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    if (result.message != ""){
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    if (this.arguments != null) {

                        val feedingId = arguments!!.getInt("feeding_id")

                        feedingViewModel.deleteLocalFeeding(feedingId)
                    }

                    feedingViewModel.doneDeleteRequest()

                    this@BottomSheetActionFeeding.dismiss()
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    feedingViewModel.doneDeleteRequest()
                }
            }
        })
    }

    override fun showBottomSheetEdit() {
        super.showBottomSheetEdit()

        if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetFeeding") == null) {
            if (this.arguments != null) {
                val bottomSheetFeeding = BottomSheetFeeding()

                val bundle = Bundle()

                val kerambaId = arguments!!.getInt("keramba_id")

                val feedingId = arguments!!.getInt("feeding_id")

                bundle.putInt("keramba_id", kerambaId)

                bundle.putInt("feeding_id", feedingId)

                bottomSheetFeeding.arguments = bundle

                bottomSheetFeeding.show(requireActivity().supportFragmentManager, "BottomSheetFeeding")
            }

            this@BottomSheetActionFeeding.dismiss()
        }
    }

    override fun showAlertDialogDelete() {
        super.showAlertDialogDelete()

        if (this.arguments != null) {

            val feedingId = arguments!!.getInt("feeding_id")

            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Konfirmasi Hapus")

            builder.setMessage("Apa anda yakin untuk menghapus pemberian pakan ini?")

            builder.setPositiveButton("Ya") { _, _ ->

                feedingViewModel.deleteFeeding(feedingId)
            }

            builder.setNegativeButton("Batal") { _, _ -> }

            builder.show()
        }
    }
}