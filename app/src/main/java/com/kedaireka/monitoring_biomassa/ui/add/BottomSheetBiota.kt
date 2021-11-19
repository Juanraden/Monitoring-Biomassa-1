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
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.databinding.BottomSheetBiotaBinding
import com.kedaireka.monitoring_biomassa.ui.DatePickerFragment
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString
import com.kedaireka.monitoring_biomassa.viewmodel.BiotaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class BottomSheetBiota : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener,
    DatePickerDialog.OnDateSetListener {

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val biotaViewModel by viewModels<BiotaViewModel>()

    private lateinit var binding: BottomSheetBiotaBinding

    private lateinit var mapKeramba: Map<String, Int>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetBiotaBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivClose.setOnClickListener { dismiss() }

        binding.bottomSheetBiota = this@BottomSheetBiota

        setupDropdown()

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

    private fun setupObserver() {
        biotaViewModel.selectedTanggalTebar.observe(viewLifecycleOwner, {
            if (it > 0) {
                binding.tanggalTebarEt.setText(
                    convertLongToDateString(it),
                    TextView.BufferType.EDITABLE
                )

                binding.tanggalTebarEt.error = null
            } else binding.tanggalTebarEt.setText("")
        })
    }

    fun saveBiota() {
        binding.apply {
            if (isEntryValid(
                    jenisBiotaEt.text.toString().trim(),
                    bobotBibitEt.text.toString(),
                    panjangBibitEt.text.toString(),
                    jumlahBibitEt.text.toString()
                )
            ) {
                biotaViewModel.insertBiota(
                    jenisBiotaEt.text.toString().trim(),
                    bobotBibitEt.text.toString(),
                    panjangBibitEt.text.toString(),
                    jumlahBibitEt.text.toString()
                )

                dismiss()

            } else {
                if (TextUtils.isEmpty(jenisBiotaEt.text)) {
                    jenisBiotaEt.error = "Jenis biota harus diisi!"
                }

                if (TextUtils.isEmpty(bobotBibitEt.text)) {
                    bobotBibitEt.error = "Bobot bibit harus diisi!"
                }

                if (TextUtils.isEmpty(panjangBibitEt.text)) {
                    panjangBibitEt.error = "Panjang bibit harus diisi!"
                }

                if (TextUtils.isEmpty(jumlahBibitEt.text)) {
                    jumlahBibitEt.error = "Banyaknya bibit harus diisi!"
                }

                if (TextUtils.isEmpty(tanggalTebarEt.text)) {
                    tanggalTebarEt.error = "Tanggal tebar harus diketahui!"
                }
            }
        }
    }

    fun showDatePicker() {
        DatePickerFragment.create().show(childFragmentManager, "datePicker")
    }

    private fun isEntryValid(
        jenis: String,
        bobot: String,
        panjang: String,
        jumlah: String
    ): Boolean {
        return biotaViewModel.isEntryValid(jenis, bobot, panjang, jumlah)
    }

    private fun setupDropdown() {
        kerambaViewModel.getAllKeramba().observe(viewLifecycleOwner, { listKeramba ->

            mapKeramba = if (this@BottomSheetBiota.arguments != null) {
                val kerambaid = this@BottomSheetBiota.arguments!!.getInt("kerambaid")

                listKeramba.map { keramba -> keramba.nama_keramba to keramba.kerambaid }.toMap().filterValues { it == kerambaid }

            } else {
                listKeramba.map { keramba -> keramba.nama_keramba to keramba.kerambaid }.toMap()
            }


            val kerambaList = mapKeramba.keys.toList()

            val arrayAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kerambaList)

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.kerambaDropdown.adapter = arrayAdapter

            binding.kerambaDropdown.onItemSelectedListener = this@BottomSheetBiota
        })
    }

    override fun onItemSelected(parent: AdapterView<*>, p1: View?, pos: Int, id: Long) {
        val namaKeramba = parent.getItemAtPosition(pos)

        if (namaKeramba != null) {
            biotaViewModel.selectKerambaId(mapKeramba[namaKeramba]!!)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate: Calendar = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)

        biotaViewModel.onSelectDateTime(selectedDate.timeInMillis)
    }
}