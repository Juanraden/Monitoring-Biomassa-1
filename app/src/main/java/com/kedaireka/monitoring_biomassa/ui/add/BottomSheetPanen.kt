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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.BiotaDomain
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.databinding.BottomSheetPanenBinding
import com.kedaireka.monitoring_biomassa.ui.DatePickerFragment
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString
import com.kedaireka.monitoring_biomassa.viewmodel.BiotaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.PanenViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class BottomSheetPanen : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener,
    DatePickerDialog.OnDateSetListener {
    private lateinit var binding: BottomSheetPanenBinding

    private val panenViewModel by activityViewModels<PanenViewModel>()

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private lateinit var mapKerambatoBiota: Map<KerambaDomain, List<BiotaDomain>>

    private lateinit var kerambaList: List<KerambaDomain>

    private lateinit var biotaList: List<BiotaDomain>

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
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
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
    ): View {

        binding = BottomSheetPanenBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheetPanen = this@BottomSheetPanen

        binding.ivClose.setOnClickListener { dismiss() }

        setupDropdown()

        setupObserver()
    }

    private fun setupObserver() {
        panenViewModel.requestPostAddResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Loading -> {
                }
                is NetworkResult.Loaded -> {
                    if (panenViewModel.inputKerambaId.value != null) {
                        biotaViewModel.fetchBiotaHistory(panenViewModel.inputKerambaId.value!!)
                    }

                    panenViewModel.donePostAddRequest()

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

    fun savePanen() {
        binding.apply {
            if (isEntryValid(
                    binding.panjangBiotaEt.text.toString(),
                    binding.bobotBiotaEt.text.toString(),
                    binding.jumlahBiotaEt.text.toString(),
                    binding.jumlahGagalBiotaEt.text.toString(),
                    binding.tanggalPanenEt.text.toString()
                )
            ) {
                panenViewModel.insertPanen(
                    binding.panjangBiotaEt.text.toString(),
                    binding.bobotBiotaEt.text.toString(),
                    binding.jumlahBiotaEt.text.toString(),
                    binding.jumlahGagalBiotaEt.text.toString(),
                    binding.tanggalPanenEt.text.toString()
                )
            } else {
                if (TextUtils.isEmpty(panjangBiotaEt.text)) {
                    panjangBiotaEt.error = "Panjang biota harus diisi!"
                }

                if (TextUtils.isEmpty(bobotBiotaEt.text)) {
                    bobotBiotaEt.error = "Bobot biota harus diisi!"
                }

                if (TextUtils.isEmpty(jumlahBiotaEt.text)) {
                    jumlahBiotaEt.error = "Jumlah biota harus diisi!"
                }

                if (TextUtils.isEmpty(jumlahGagalBiotaEt.text)) {
                    jumlahGagalBiotaEt.error = "Jumlah gagal panen harus diisi!"
                }

                if (TextUtils.isEmpty(tanggalPanenEt.text)) {
                    tanggalPanenEt.error = "Tanggal panen harus diisi!"
                }
            }
        }
    }

    private fun setupDropdown() {
        kerambaViewModel.loadKerambaAndBiota().observe(viewLifecycleOwner, {
            mapKerambatoBiota = it

            kerambaList = it.keys.toList()

            val kerambaIdlist = kerambaList.map { keramba -> keramba.keramba_id }

            val kerambaAdapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    kerambaList.map { kerambaDomain -> kerambaDomain.nama_keramba }
                )

            kerambaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.kerambaDropdown.adapter = kerambaAdapter

            if (this@BottomSheetPanen.arguments != null) {
                val kerambaId = this@BottomSheetPanen.arguments!!.getInt("keramba_id")

                if (kerambaId > 0) {
                    val index = kerambaIdlist.indexOf(kerambaId)

                    binding.kerambaDropdown.setSelection(index)
                }
            }

            binding.kerambaDropdown.onItemSelectedListener = this@BottomSheetPanen
        })
    }

    fun showDatePicker() {
        if (childFragmentManager.findFragmentByTag("DatePicker") == null) {
            DatePickerFragment.create().show(childFragmentManager, "DatePicker")
        }
    }

    private fun isEntryValid(
        panjang: String,
        bobot: String,
        jumlahHidup: String,
        jumlahMati: String,
        tanggal: String
    ): Boolean {
        return panenViewModel.isEntryValid(panjang, bobot, jumlahHidup, jumlahMati, tanggal)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate: Calendar = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)

        binding.tanggalPanenEt.setText(
            convertLongToDateString(selectedDate.timeInMillis, "EEEE dd-MMM-yyyy"),
            TextView.BufferType.EDITABLE
        )
    }


    override fun onItemSelected(parent: AdapterView<*>, p1: View, pos: Int, p3: Long) {
        when (parent.id) {
            binding.kerambaDropdown.id -> {
                val keramba = kerambaList[pos]

                panenViewModel.selectKeramba(keramba.keramba_id)

                biotaList = mapKerambatoBiota[keramba]!!

                val biotaAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    biotaList.map { biotaDomain -> biotaDomain.jenis_biota }
                )

                biotaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                binding.biotaDropdown.adapter = biotaAdapter

                binding.biotaDropdown.onItemSelectedListener = this@BottomSheetPanen
            }

            binding.biotaDropdown.id -> {
                val biota = biotaList[pos]

                panenViewModel.selectBiota(biota.biota_id)
            }

        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}