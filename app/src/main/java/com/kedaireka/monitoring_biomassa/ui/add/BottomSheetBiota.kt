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
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.databinding.BottomSheetBiotaBinding
import com.kedaireka.monitoring_biomassa.ui.DatePickerFragment
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString
import com.kedaireka.monitoring_biomassa.util.convertStringToDateLong
import com.kedaireka.monitoring_biomassa.viewmodel.BiotaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


// parameter: biotaId: Int?, kerambaId: Int
@AndroidEntryPoint
class BottomSheetBiota : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener,
    DatePickerDialog.OnDateSetListener {

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private lateinit var binding: BottomSheetBiotaBinding

    private lateinit var mapKeramba: Map<String, Int>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetBiotaBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivClose.setOnClickListener { dismiss() }

        binding.bottomSheetBiota = this@BottomSheetBiota

        setupDropdown()

        if (this@BottomSheetBiota.arguments != null) {
            val biotaId: Int = this@BottomSheetBiota.arguments!!.getInt("biota_id")

            if (biotaId > 0) {
                biotaViewModel.loadBiotaData(biotaId).observe(viewLifecycleOwner, { bind(it) })
            }
        }

        setupObserver()
    }

    private fun setupObserver() {
        biotaViewModel.requestPostAddResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                    binding.apply {
                        saveBiotaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }
                }
                is NetworkResult.Loading -> {
                    binding.apply {
                        saveBiotaBtn.visibility = View.GONE

                        progressLoading.visibility = View.VISIBLE
                    }
                }
                is NetworkResult.Loaded -> {
                    binding.apply {
                        saveBiotaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != ""){
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    if (biotaViewModel.selectedKerambaId.value != null) {
                        biotaViewModel.fetchBiota(biotaViewModel.selectedKerambaId.value!!)
                    }

                    biotaViewModel.donePostAddRequest()

                    this.dismiss()
                }
                is NetworkResult.Error -> {
                    binding.apply {
                        saveBiotaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    biotaViewModel.donePostAddRequest()
                }
            }
        })

        biotaViewModel.requestPutUpdateResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                    binding.apply {
                        saveBiotaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }
                }
                is NetworkResult.Loading -> {
                    binding.apply {
                        saveBiotaBtn.visibility = View.GONE

                        progressLoading.visibility = View.VISIBLE
                    }
                }
                is NetworkResult.Loaded -> {
                    binding.apply {
                        saveBiotaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != ""){
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    if (this@BottomSheetBiota.arguments != null) {
                        val biotaId: Int = this@BottomSheetBiota.arguments!!.getInt("biota_id")

                        biotaViewModel.updateLocalBiota(
                            biotaId,
                            binding.jenisBiotaEt.text.toString().trim(),
                            binding.bobotBibitEt.text.toString(),
                            binding.panjangBibitEt.text.toString(),
                            binding.jumlahBibitEt.text.toString(),
                            if (binding.tanggalTebarEt.text.toString() != "") {
                                convertStringToDateLong(
                                    binding.tanggalTebarEt.text.toString(),
                                    "EEEE dd-MMM-yyyy"
                                )
                            } else {
                                0L
                            }
                        )
                    }
                    biotaViewModel.donePutUpdateRequest()

                    this.dismiss()
                }
                is NetworkResult.Error -> {
                    binding.apply {
                        saveBiotaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                    biotaViewModel.donePutUpdateRequest()
                }
            }
        })
    }

    private fun bind(biotaDomain: BiotaDomain) {
        binding.apply {
            titleTv.text = biotaDomain.jenis_biota

            jenisBiotaEt.setText(biotaDomain.jenis_biota, TextView.BufferType.SPANNABLE)

            bobotBibitEt.setText(biotaDomain.bobot.toString(), TextView.BufferType.SPANNABLE)

            panjangBibitEt.setText(biotaDomain.panjang.toString(), TextView.BufferType.SPANNABLE)

            jumlahBibitEt.setText(
                biotaDomain.jumlah_bibit.toString(),
                TextView.BufferType.SPANNABLE
            )

            binding.tanggalTebarEt.setText(
                convertLongToDateString(biotaDomain.tanggal_tebar, "EEEE dd-MMM-yyyy"),
                TextView.BufferType.EDITABLE
            )

            saveBiotaBtn.text = getString(R.string.edit_biota)
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


    fun saveBiota() {
        binding.apply {
            if (isEntryValid(
                    jenisBiotaEt.text.toString().trim(),
                    bobotBibitEt.text.toString(),
                    panjangBibitEt.text.toString(),
                    jumlahBibitEt.text.toString(),
                    if (tanggalTebarEt.text.toString() != "") {
                        convertStringToDateLong(tanggalTebarEt.text.toString(), "EEEE dd-MMM-yyyy")
                    } else {
                        0L
                    }
                )
            ) {
                if (this@BottomSheetBiota.arguments != null) {
                    val biotaId: Int = this@BottomSheetBiota.arguments!!.getInt("biota_id")

                    if (biotaId > 0) {
                        biotaViewModel.updateBiota(
                            biotaId,
                            jenisBiotaEt.text.toString().trim(),
                            bobotBibitEt.text.toString(),
                            panjangBibitEt.text.toString(),
                            jumlahBibitEt.text.toString(),
                            if (tanggalTebarEt.text.toString() != "") {
                                convertStringToDateLong(
                                    tanggalTebarEt.text.toString(),
                                    "EEEE dd-MMM-yyyy"
                                )
                            } else {
                                0L
                            }
                        )
                    } else {
                        biotaViewModel.insertBiota(
                            jenisBiotaEt.text.toString().trim(),
                            bobotBibitEt.text.toString(),
                            panjangBibitEt.text.toString(),
                            jumlahBibitEt.text.toString(),
                            if (tanggalTebarEt.text.toString() != "") {
                                convertStringToDateLong(
                                    tanggalTebarEt.text.toString(),
                                    "EEEE dd-MMM-yyyy"
                                )
                            } else {
                                0L
                            }
                        )
                    }
                } else {
                    biotaViewModel.insertBiota(
                        jenisBiotaEt.text.toString().trim(),
                        bobotBibitEt.text.toString(),
                        panjangBibitEt.text.toString(),
                        jumlahBibitEt.text.toString(),
                        if (tanggalTebarEt.text.toString() != "") {
                            convertStringToDateLong(
                                tanggalTebarEt.text.toString(),
                                "EEEE dd-MMM-yyyy"
                            )
                        } else {
                            0L
                        }
                    )
                }
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
        if (childFragmentManager.findFragmentByTag("DatePicker") == null) {
            DatePickerFragment.create().show(childFragmentManager, "DatePicker")
        }
    }

    private fun isEntryValid(
        jenis: String,
        bobot: String,
        panjang: String,
        jumlah: String,
        tanggal: Long,
    ): Boolean {
        return biotaViewModel.isEntryValid(jenis, bobot, panjang, jumlah, tanggal)
    }

    private fun setupDropdown() {
        kerambaViewModel.getAllKeramba().observe(viewLifecycleOwner, { listKeramba ->

            mapKeramba =
                listKeramba.map { keramba -> keramba.nama_keramba to keramba.keramba_id }.toMap()

            val kerambaList = mapKeramba.keys.toList()

            val arrayAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kerambaList)

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.kerambaDropdown.adapter = arrayAdapter

            if (this@BottomSheetBiota.arguments != null) {
                val kerambaId: Int = this@BottomSheetBiota.arguments!!.getInt("keramba_id")

                val kerambaIdlist: List<Int> = mapKeramba.values.toList()

                val index: Int = kerambaIdlist.indexOf(kerambaId)

                binding.kerambaDropdown.setSelection(index)

            }

            binding.kerambaDropdown.onItemSelectedListener = this@BottomSheetBiota
        })
    }

    override fun onItemSelected(parent: AdapterView<*>, p1: View?, pos: Int, id: Long) {
        val namaKeramba = parent.getItemAtPosition(pos)

        if (namaKeramba != null) {
            biotaViewModel.selectkerambaId(mapKeramba[namaKeramba]!!)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate: Calendar = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)

        binding.tanggalTebarEt.setText(
            convertLongToDateString(selectedDate.timeInMillis, "EEEE dd-MMM-yyyy"),
            TextView.BufferType.EDITABLE
        )
    }
}