package com.kedaireka.monitoring_biomassa.ui.add

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.BiotaDomain
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.databinding.BottomSheetPanenBinding
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.PanenViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetPanen: BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: BottomSheetPanenBinding

    private val panenViewModel by viewModels<PanenViewModel>()

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

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

        binding = BottomSheetPanenBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivClose.setOnClickListener { dismiss() }

        setupDropdown()
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

            if (this@BottomSheetPanen.arguments != null){
                val kerambaId = this@BottomSheetPanen.arguments!!.getInt("keramba_id")

                if (kerambaId > 0){
                    val index = kerambaIdlist.indexOf(kerambaId)

                    binding.kerambaDropdown.setSelection(index)
                }
            }

            binding.kerambaDropdown.onItemSelectedListener = object :

                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                    val keramba = kerambaList[pos]

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

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        })
    }

    override fun onItemSelected(parent: AdapterView<*>, p1: View?, pos: Int, p3: Long) {
        val biota = biotaList[pos]
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}