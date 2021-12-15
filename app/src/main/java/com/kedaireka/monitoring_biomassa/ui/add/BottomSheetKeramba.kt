package com.kedaireka.monitoring_biomassa.ui.add

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.databinding.BottomSheetKerambaBinding
import com.kedaireka.monitoring_biomassa.ui.DatePickerFragment
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString
import com.kedaireka.monitoring_biomassa.util.convertStringToDateLong
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class BottomSheetKeramba : BottomSheetDialogFragment(), DatePickerDialog.OnDateSetListener {
    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private lateinit var binding: BottomSheetKerambaBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                    ukuranKerambaEt.text.toString(),
                    if (tanggalInstallEt.text.toString() != "") {
                        convertStringToDateLong(
                            tanggalInstallEt.text.toString(),
                            "EEEE dd-MMM-yyyy"
                        )
                    } else {
                        0L
                    }
                )
            ) {
                if (this@BottomSheetKeramba.arguments != null) {
                    updateKeramba(
                        arguments!!.getInt("keramba_id"),
                        namaKerambaEt.text.toString().trim(),
                        ukuranKerambaEt.text.toString(),
                        if (tanggalInstallEt.text.toString() != "") {
                            convertStringToDateLong(
                                tanggalInstallEt.text.toString(),
                                "EEEE dd-MMM-yyyy"
                            )
                        } else {
                            0L
                        }
                    )
                } else {
                    insertKeramba(
                        namaKerambaEt.text.toString().trim(),
                        ukuranKerambaEt.text.toString(),
                        if (tanggalInstallEt.text.toString() != "") {
                            convertStringToDateLong(
                                tanggalInstallEt.text.toString(),
                                "EEEE dd-MMM-yyyy"
                            )
                        } else {
                            0L
                        }
                    )
                }
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

    private fun updateKeramba(id: Int, nama_keramba: String, ukuran: String, tanggal: Long) {
        kerambaViewModel.updateKeramba(id, nama_keramba, ukuran, tanggal)
    }

    private fun bind(kerambaDomain: KerambaDomain) {
        binding.apply {
            titleTv.text = kerambaDomain.nama_keramba

            namaKerambaEt.setText(kerambaDomain.nama_keramba, TextView.BufferType.SPANNABLE)

            ukuranKerambaEt.setText(kerambaDomain.ukuran.toString(), TextView.BufferType.SPANNABLE)

            binding.tanggalInstallEt.setText(
                convertLongToDateString(kerambaDomain.tanggal_install, "EEEE dd-MMM-yyyy"),
                TextView.BufferType.EDITABLE
            )

            saveKerambaBtn.text = getString(R.string.update_keramba)
        }
    }

    private fun setupObserver() {
        kerambaViewModel.requestPostAddResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Loading -> {
                }
                is NetworkResult.Loaded -> {

                    kerambaViewModel.fetchKeramba()

                    kerambaViewModel.donePostAddRequest()

                    this.dismiss()
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        kerambaViewModel.doneToastException()
                    }
                }
            }
        })

        kerambaViewModel.requestPutUpdateResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Loading -> {
                }
                is NetworkResult.Loaded -> {
                    if (this@BottomSheetKeramba.arguments != null) {
                        kerambaViewModel.updateLocalKeramba(
                            arguments!!.getInt("keramba_id"),
                            binding.namaKerambaEt.text.toString().trim(),
                            binding.ukuranKerambaEt.text.toString(),
                            if (binding.tanggalInstallEt.text.toString() != "") {
                                convertStringToDateLong(
                                    binding.tanggalInstallEt.text.toString(),
                                    "EEEE dd-MMM-yyyy"
                                )
                            } else {
                                0L
                            }
                        )
                    }

                    kerambaViewModel.donePutUpdateRequest()

                    this.dismiss()
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

    fun showDatePicker() {
        if (childFragmentManager.findFragmentByTag("DatePicker") == null) {
            DatePickerFragment.create().show(childFragmentManager, "DatePicker")
        }
    }

    private fun isEntryValid(nama: String, ukuran: String, tanggal: Long): Boolean {
        return kerambaViewModel.isEntryValid(nama, ukuran, tanggal)
    }

    private fun insertKeramba(nama: String, ukuran: String, tanggal: Long) {
        kerambaViewModel.insertKeramba(nama, ukuran, tanggal)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate: Calendar = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)

        binding.tanggalInstallEt.setText(
            convertLongToDateString(selectedDate.timeInMillis, "EEEE dd-MMM-yyyy"),
            TextView.BufferType.EDITABLE
        )
    }
}