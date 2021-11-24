package com.kedaireka.monitoring_biomassa.ui.add

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.databinding.BottomSheetKerambaBinding
import com.kedaireka.monitoring_biomassa.ui.DatePickerFragment
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class BottomSheetKeramba : BottomSheetDialogFragment(), DatePickerDialog.OnDateSetListener {
    private val kerambaViewModel by viewModels<KerambaViewModel>()

    private lateinit var binding: BottomSheetKerambaBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetKerambaBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheetKeramba = this@BottomSheetKeramba

        binding.ivClose.setOnClickListener { dismiss() }

        if (this.arguments != null) {
            val id = arguments!!.getInt("keramba_id")

            kerambaViewModel.loadKerambaData(id)
                .observe(viewLifecycleOwner, { bind(kerambaDomain = it) })
        }

        setupObserver()
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

    fun onSaveKerambaBtnClicked() {
        binding.apply {
            if (isEntryValid(
                    namaKerambaEt.text.toString().trim(),
                    ukuranKerambaEt.text.toString()
                )
            ) {
                if (this@BottomSheetKeramba.arguments != null) {
                    updateKeramba(
                        arguments!!.getInt("keramba_id"),
                        namaKerambaEt.text.toString().trim(),
                        ukuranKerambaEt.text.toString()
                    )
                } else {
                    insertKeramba(
                        namaKerambaEt.text.toString().trim(),
                        ukuranKerambaEt.text.toString()
                    )
                }
                dismiss()
            } else {
                if (TextUtils.isEmpty(namaKerambaEt.text)) {
                    namaKerambaEt.error = "Nama keramba harus diisi!"
                }

                if (TextUtils.isEmpty(ukuranKerambaEt.text)) {
                    ukuranKerambaEt.error = "Ukuran keramba harus diisi!"
                }

                if (TextUtils.isEmpty(tanggalInstallEt.text)) {
                    tanggalInstallEt.error = "Tanggal install harus diketahui!"
                }
            }
        }
    }

    private fun updateKeramba(id: Int, nama_keramba: String, ukuran: String) {
        kerambaViewModel.updateKeramba(id, nama_keramba, ukuran)
    }

    private fun bind(kerambaDomain: KerambaDomain) {
        binding.apply {
            titleTv.text = kerambaDomain.nama_keramba

            namaKerambaEt.setText(kerambaDomain.nama_keramba, TextView.BufferType.SPANNABLE)

            ukuranKerambaEt.setText(kerambaDomain.ukuran.toString(), TextView.BufferType.SPANNABLE)

            kerambaViewModel.onSelectDateTime(kerambaDomain.tanggal_install)

            saveKerambaBtn.text = getString(R.string.update_keramba)
        }
    }

    private fun setupObserver() {
        kerambaViewModel.tanggalInstall.observe(viewLifecycleOwner, {
            if (it > 0) {
                binding.tanggalInstallEt.setText(
                    convertLongToDateString(it),
                    TextView.BufferType.EDITABLE
                )

                binding.tanggalInstallEt.error = null
            } else binding.tanggalInstallEt.setText("")
        })
    }

    fun showDatePicker() {
        if (childFragmentManager.findFragmentByTag("DatePicker") == null) {
            DatePickerFragment.create().show(childFragmentManager, "DatePicker")
        }
    }

    private fun isEntryValid(nama: String, ukuran: String): Boolean {
        return kerambaViewModel.isEntryValid(nama, ukuran)
    }

    private fun insertKeramba(nama: String, ukuran: String) {
        kerambaViewModel.insertKeramba(nama, ukuran)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate: Calendar = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)

        kerambaViewModel.onSelectDateTime(selectedDate.timeInMillis)
    }
}