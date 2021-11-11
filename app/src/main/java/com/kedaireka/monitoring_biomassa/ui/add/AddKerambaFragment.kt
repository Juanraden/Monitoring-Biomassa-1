package com.kedaireka.monitoring_biomassa.ui.add

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
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
                insertKeramba(
                    jenisKerambaEt.text.toString().trim(),
                    ukuranKerambaEt.text.toString()
                )
                kerambaViewModel.onSelectDateTime(0)
                navController.navigateUp()
            }
        }

        setupObserver()
    }

    private fun setupObserver() {
        kerambaViewModel.tanggalInstall.observe(viewLifecycleOwner, {
            if (it > 0){
                binding.tanggalInstallEt.setText(
                    convertLongToDateString(it),
                    TextView.BufferType.EDITABLE
                )
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
        if (isEntryValid(nama, ukuran)){
            kerambaViewModel.insertKeramba(nama, ukuran)
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

