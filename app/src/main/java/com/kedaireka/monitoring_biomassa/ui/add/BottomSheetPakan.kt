package com.kedaireka.monitoring_biomassa.ui.add

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.PakanDomain
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.databinding.BottomSheetPakanBinding
import com.kedaireka.monitoring_biomassa.viewmodel.PakanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetPakan : BottomSheetDialogFragment() {
    private val pakanViewModel by activityViewModels<PakanViewModel>()

    private lateinit var binding: BottomSheetPakanBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetPakanBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheetPakan = this@BottomSheetPakan

        binding.ivClose.setOnClickListener { dismiss() }

        if (this.arguments != null) {
            val pakanId = arguments!!.getInt("pakan_id")

            pakanViewModel.loadPakanData(pakanId).observe(viewLifecycleOwner, {
                bind(it)
            })
        }

        setupObserver()
    }

    private fun bind(pakan: PakanDomain) {
        binding.apply {
            titleTv.text = pakan.jenis_pakan

            namaPakanEt.setText(pakan.jenis_pakan, TextView.BufferType.SPANNABLE)

            deskripsiPakanEt.setText(pakan.deskripsi, TextView.BufferType.SPANNABLE)

            savePakanBtn.text = getString(R.string.update_jenis_pakan)
        }
    }

    private fun setupObserver() {
        pakanViewModel.requestPostAddResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Loading -> {
                }
                is NetworkResult.Loaded -> {

                    pakanViewModel.fetchPakan()

                    pakanViewModel.donePostAddRequest()

                    this.dismiss()
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        pakanViewModel.requestPutUpdateResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Loading -> {
                }
                is NetworkResult.Loaded -> {
                    if (this@BottomSheetPakan.arguments != null) {
                        val pakanId = this@BottomSheetPakan.arguments!!.getInt("pakan_id")

                        pakanViewModel.updateLocalPakan(
                            pakanId, binding.namaPakanEt.text.toString().trim(),
                            binding.deskripsiPakanEt.text.toString().trim()
                        )
                    }

                    pakanViewModel.donePutUpdateRequest()

                    this.dismiss()
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()

        val view: FrameLayout = dialog?.findViewById(R.id.design_bottom_sheet)!!

        view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

        val behavior = BottomSheetBehavior.from(view)

        behavior.peekHeight = 3000
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCancel(dialog: DialogInterface) {
        dialog.dismiss()
        super.onCancel(dialog)
    }

    override fun dismiss() {
        hideKeyBoard()
        super.dismiss()
    }

    private fun hideKeyBoard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = dialog?.window?.currentFocus
        view?.let {
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun savePakan() {
        binding.apply {
            if (isEntryValid(
                    namaPakanEt.text.toString().trim(),
                )
            ) {
                if (this@BottomSheetPakan.arguments != null) {
                    val pakanId = this@BottomSheetPakan.arguments!!.getInt("pakan_id")

                    pakanViewModel.updatePakan(
                        pakanId, namaPakanEt.text.toString().trim(),
                        deskripsiPakanEt.text.toString().trim()
                    )
                } else {
                    pakanViewModel.insertPakan(
                        namaPakanEt.text.toString().trim(),
                        deskripsiPakanEt.text.toString().trim()
                    )
                }
            } else {
                if (TextUtils.isEmpty(namaPakanEt.text)) {
                    namaPakanEt.error = "Nama pakan harus diisi!"
                }
            }
        }
    }

    private fun isEntryValid(jenis_pakan: String): Boolean {
        return pakanViewModel.isEntryValid(jenis_pakan)
    }
}