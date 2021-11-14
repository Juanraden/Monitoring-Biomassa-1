package com.kedaireka.monitoring_biomassa.ui.add

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.databinding.FragmentAddBiotaBinding
import com.kedaireka.monitoring_biomassa.ui.DatePickerFragment
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString
import com.kedaireka.monitoring_biomassa.viewmodel.BiotaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddBiotaFragment : Fragment(), AdapterView.OnItemSelectedListener,
    DatePickerDialog.OnDateSetListener {

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private lateinit var binding: FragmentAddBiotaBinding

    private lateinit var navController: NavController

    private lateinit var mapKeramba: Map<String, Int>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddBiotaBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.addBiotaFragment = this@AddBiotaFragment

        setupNavigation()

        setupDropdown()

        setupObserver()

    }

    override fun onStop() {
        super.onStop()

        biotaViewModel.onSelectDateTime(0)
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

                navController.navigateUp()

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

    private fun setupNavigation() {

        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.homeFragment, R.id.addFragment, R.id.settingsFragment))

        binding.toolbarFragment.setupWithNavController(navController, appBarConfiguration)

        binding.toolbarFragment.setNavigationOnClickListener {
            navController.navigateUp(appBarConfiguration)
        }
    }

    private fun setupDropdown() {

        kerambaViewModel.allKeramba.observe(viewLifecycleOwner, {
            mapKeramba = it.map { keramba -> keramba.nama_keramba to keramba.kerambaid }.toMap()

            val kerambaList = mapKeramba.keys.toList()

            val arrayAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kerambaList)

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.kerambaDropdown.adapter = arrayAdapter

            binding.kerambaDropdown.onItemSelectedListener = this@AddBiotaFragment
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
