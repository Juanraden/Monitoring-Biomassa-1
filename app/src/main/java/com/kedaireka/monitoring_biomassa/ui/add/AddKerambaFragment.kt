package com.kedaireka.monitoring_biomassa.ui.add

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.databinding.FragmentAddKerambaBinding
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddKerambaFragment : Fragment() {
    private lateinit var binding: FragmentAddKerambaBinding

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddKerambaBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            addKerambaFragment = this@AddKerambaFragment

            saveKerambaBtn.setOnClickListener {
                if (isEntryValid(namaKerambaEt.text.toString().trim(), ukuranKerambaEt.text.toString())){
                    insertKeramba(
                        namaKerambaEt.text.toString().trim(),
                        ukuranKerambaEt.text.toString()
                    )
                    navController.navigateUp()
                } else {
                    if (TextUtils.isEmpty(namaKerambaEt.text)){
                        namaKerambaEt.error = "Nama keramba harus diisi!"
                    }

                    if (TextUtils.isEmpty(ukuranKerambaEt.text)){
                        ukuranKerambaEt.error ="Ukuran keramba harus diisi!"
                    }

                    if (TextUtils.isEmpty(tanggalInstallEt.text)){
                        tanggalInstallEt.error = "Tanggal install harus diketahui!"
                    }
                }

            }
        }
        setupNavigation()

        setupObserver()
    }

    override fun onStop() {
        super.onStop()
        kerambaViewModel.onSelectDateTime(0)
    }

    private fun setupObserver() {
        kerambaViewModel.tanggalInstall.observe(viewLifecycleOwner, {
            if (it > 0){
                binding.tanggalInstallEt.setText(
                    convertLongToDateString(it),
                    TextView.BufferType.EDITABLE
                )

                binding.tanggalInstallEt.error = null
            } else binding.tanggalInstallEt.setText("")
        })
    }

    fun showDatePicker() {
        DatePickerFragment.create().show(requireActivity().supportFragmentManager, "datePicker")
    }

    private fun isEntryValid(nama: String, ukuran: String): Boolean{
        return kerambaViewModel.isEntryValid(nama, ukuran)
    }

    private fun insertKeramba(nama: String, ukuran: String){
        kerambaViewModel.insertKeramba(nama, ukuran)
    }

    private fun setupNavigation(){
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.addFragment, R.id.settingsFragment))

        binding.toolbarFragment.setupWithNavController(navController, appBarConfiguration)

        binding.toolbarFragment.setNavigationOnClickListener {
            navController.navigateUp(appBarConfiguration)
        }
    }
}

@AndroidEntryPoint
class DatePickerFragment : DialogFragment(),
    DatePickerDialog.OnDateSetListener {

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    companion object {
        fun create(): DatePickerFragment =
            DatePickerFragment()

        private var selectedYear: Int = 0
        private var selectedMonth: Int = 0
        private var selectedDay: Int = 0
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        selectedYear = year
        selectedMonth = month
        selectedDay = dayOfMonth
        val selectedDate: Calendar = Calendar.getInstance()
        selectedDate.set(selectedYear, selectedMonth, selectedDay)

        kerambaViewModel.onSelectDateTime(selectedDate.timeInMillis)
    }
}

