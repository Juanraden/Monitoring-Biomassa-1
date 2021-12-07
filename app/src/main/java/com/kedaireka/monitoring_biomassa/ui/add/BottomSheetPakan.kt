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
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.PakanDomain
import com.kedaireka.monitoring_biomassa.databinding.BottomSheetPakanBinding
import com.kedaireka.monitoring_biomassa.viewmodel.PakanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetPakan : BottomSheetDialogFragment() {
    private val pakanViewModel by viewModels<PakanViewModel>()

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
            val id = arguments!!.getInt("pakan_id")

            pakanViewModel.loadPakanData(id)
                .observe(viewLifecycleOwner, { bind(pakanDomain = it) })
        }
    }

    override fun onStart() {
        super.onStart()

        val view: FrameLayout = dialog?.findViewById(R.id.design_bottom_sheet)!!

        view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

        val behavior = BottomSheetBehavior.from(view)

        behavior.peekHeight = 3000
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

        })
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
                    deskripsiPakanEt.text.toString().trim()
                )
            ) {
                if (this@BottomSheetPakan.arguments != null) {
                    updatePakan(
                        arguments!!.getInt("pakan_id"),
                        namaPakanEt.text.toString().trim(),
                        deskripsiPakanEt.text.toString().trim()
                    )
                } else {
                    insertPakan(
                        namaPakanEt.text.toString().trim(),
                        deskripsiPakanEt.text.toString().trim()
                    )
                }

                dismiss()
            } else {
                if (TextUtils.isEmpty(namaPakanEt.text)) {
                    namaPakanEt.error = "Nama pakan harus diisi!"
                }
            }
        }
    }

    private fun updatePakan(id: Int, nama_pakan: String, deskripsi: String) {
        pakanViewModel.updatePakan(id, nama_pakan, deskripsi)
    }

    private fun insertPakan(nama: String, deskripsi: String) {
        pakanViewModel.insertPakan(nama, deskripsi)
    }

    private fun bind(pakanDomain: PakanDomain) {
        binding.apply {
            titleTv.text = getString(R.string.edit_pakan)

            namaPakanEt.setText(pakanDomain.jenis_pakan, TextView.BufferType.SPANNABLE)

            deskripsiPakanEt.setText(pakanDomain.deskripsi, TextView.BufferType.SPANNABLE)

            savePakanBtn.text = getString(R.string.update_pakan)
        }
    }

    private fun isEntryValid(jenis_pakan: String, deskripsi: String): Boolean {
        return pakanViewModel.isEntryValid(jenis_pakan, deskripsi)
    }
}