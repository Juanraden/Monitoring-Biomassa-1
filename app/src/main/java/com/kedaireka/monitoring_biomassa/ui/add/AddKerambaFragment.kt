package com.kedaireka.monitoring_biomassa.ui.add

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.databinding.FragmentAddKerambaBinding
import com.kedaireka.monitoring_biomassa.ui.DatePickerFragment
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddKerambaFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private lateinit var binding: FragmentAddKerambaBinding

    private val kerambaViewModel by viewModels<KerambaViewModel>()

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
        DatePickerFragment.create().show(childFragmentManager, "datePicker")
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
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate: Calendar = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)

        kerambaViewModel.onSelectDateTime(selectedDate.timeInMillis)
    }
}

