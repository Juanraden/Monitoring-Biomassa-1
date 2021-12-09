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
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.BiotaDomain
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.databinding.BottomSheetPengukuranBinding
import com.kedaireka.monitoring_biomassa.ui.DatePickerFragment
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.PengukuranViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class BottomSheetPengukuran : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener,
    DatePickerDialog.OnDateSetListener {

    private val kerambaViewModel by viewModels<KerambaViewModel>()

    private val pengukuranViewModel by viewModels<PengukuranViewModel>()

    private lateinit var mapKerambatoBiota: Map<KerambaDomain, List<BiotaDomain>>

    private lateinit var kerambaList: List<KerambaDomain>

    private lateinit var biotaList: List<BiotaDomain>

    private lateinit var binding: BottomSheetPengukuranBinding

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetPengukuranBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheetPengukuran = this@BottomSheetPengukuran

        binding.ivClose.setOnClickListener { dismiss() }

        setupDropdown()

        setupObserver()
    }

    private fun isEntryValid(panjang: String, bobot: String): Boolean =
        pengukuranViewModel.isEntryValid(panjang, bobot)

    fun savePengukuran() {
        binding.apply {
            if (isEntryValid(
                    panjangBiotaEt.text.toString(),
                    bobotBiotaEt.text.toString()
                )
            ) {
                pengukuranViewModel.insertPengukuran(
                    panjangBiotaEt.text.toString(),
                    bobotBiotaEt.text.toString()
                )

                dismiss()
            } else {
                if (TextUtils.isEmpty(bobotBiotaEt.text)) {
                    bobotBiotaEt.error = "Bobot biota harus diisi!"
                }

                if (TextUtils.isEmpty(panjangBiotaEt.text)) {
                    panjangBiotaEt.error = "Panjang biota harus diisi!"
                }

                if (TextUtils.isEmpty(tanggalUkurEt.text)) {
                    tanggalUkurEt.error = "Tanggal pengukuran harus diisi!"
                }
            }
        }
    }

    private fun setupObserver() {
        pengukuranViewModel.selectedTanggalUkur.observe(viewLifecycleOwner, {
            if (it > 0) {
                binding.tanggalUkurEt.setText(
                    convertLongToDateString(it, "EEEE dd-MMM-yyyy"),
                    TextView.BufferType.EDITABLE
                )

                binding.tanggalUkurEt.error = null
            } else binding.tanggalUkurEt.setText("")
        })
    }

    private fun setupDropdown() {
        kerambaViewModel.loadKerambaAndBiota().observe(viewLifecycleOwner, {
            mapKerambatoBiota = it

            kerambaList = it.keys.toList()

            val keramba_idList = kerambaList.map {keramba -> keramba.keramba_id }

            val kerambaAdapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    kerambaList.map { kerambaDomain -> kerambaDomain.nama_keramba }
                )

            kerambaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.kerambaDropdown.adapter = kerambaAdapter

            if (this@BottomSheetPengukuran.arguments != null){
                val keramba_id = this@BottomSheetPengukuran.arguments!!.getInt("keramba_id")

                if (keramba_id > 0){
                    val index = keramba_idList.indexOf(keramba_id)

                    binding.kerambaDropdown.setSelection(index)
                }
            }

            binding.kerambaDropdown.onItemSelectedListener = object :

                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                    val keramba = kerambaList[pos]

                    biotaList = mapKerambatoBiota[keramba]!!

                    val biota_idList = biotaList.map {biota-> biota.biota_id }

                    val biotaAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        biotaList.map { biotaDomain -> biotaDomain.jenis_biota }
                    )

                    biotaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.biotaDropdown.adapter = biotaAdapter

                    if (this@BottomSheetPengukuran.arguments != null) {
                        val biota_id = this@BottomSheetPengukuran.arguments!!.getInt("biota_id")

                        val index = biota_idList.indexOf(biota_id)

                        binding.biotaDropdown.setSelection(index)
                    }

                    binding.biotaDropdown.onItemSelectedListener = this@BottomSheetPengukuran
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        })
    }

    override fun onItemSelected(parent: AdapterView<*>, p1: View?, pos: Int, p3: Long) {
        val biota = biotaList[pos]

        pengukuranViewModel.selectBiotaId(biota.biota_id)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    fun showDatePicker() {
        if (childFragmentManager.findFragmentByTag("DatePicker") == null) {
            DatePickerFragment.create().show(childFragmentManager, "DatePicker")
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate: Calendar = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)

        pengukuranViewModel.onSelectDateTime(selectedDate.timeInMillis)
    }
}