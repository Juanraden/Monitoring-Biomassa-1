package com.kedaireka.monitoring_biomassa.ui.add

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.PakanDomain
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.databinding.BottomSheetPakanBinding
import com.kedaireka.monitoring_biomassa.util.observeOnce
import com.kedaireka.monitoring_biomassa.viewmodel.PakanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetPakan : BottomSheetDialogFragment() {
    private val pakanViewModel by activityViewModels<PakanViewModel>()

    private lateinit var binding: BottomSheetPakanBinding

    private var editState: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetPakanBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheetPakan = this@BottomSheetPakan

        binding.ivClose.setOnClickListener { dismiss() }

        if (this.arguments != null) {
            val pakanId = arguments!!.getInt("pakan_id")
            editState = arguments!!.getBoolean("editState")

            pakanViewModel.loadPakanData(pakanId).observeOnce(viewLifecycleOwner, {
                bind(it)
            })

            checkEditState(editState)
        }

        setupObserver()

        switchEditButton()
    }

    private fun bind(pakan: PakanDomain) {
        binding.apply {
            titleTv.text = pakan.jenis_pakan

            namaPakanEt.setText(pakan.jenis_pakan, TextView.BufferType.SPANNABLE)

            deskripsiPakanEt.setText(pakan.deskripsi, TextView.BufferType.SPANNABLE)
        }
    }

    private fun setupObserver() {
        pakanViewModel.requestPostAddResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                    switchEditButton()
                }
                is NetworkResult.Loading -> {
                    binding.apply {
                        savePakanBtn.visibility = View.GONE

                        progressLoading.visibility = View.VISIBLE
                    }
                }
                is NetworkResult.Loaded -> {
                    binding.apply {
                        savePakanBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != ""){
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    pakanViewModel.fetchPakan()

                    pakanViewModel.donePostAddRequest()

                    this.dismiss()
                }
                is NetworkResult.Error -> {
                    binding.apply {
                        savePakanBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        pakanViewModel.requestPutUpdateResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                    binding.apply {
                        saveEditBtn.visibility = View.VISIBLE

                        batalEditBtn.visibility = View.VISIBLE

                        progressEditLoading.visibility = View.GONE
                    }
                }
                is NetworkResult.Loading -> {
                    binding.apply {
                        batalEditBtn.visibility = View.GONE

                        saveEditBtn.visibility = View.GONE

                        progressEditLoading.visibility = View.VISIBLE
                    }
                }
                is NetworkResult.Loaded -> {
                    binding.apply {
                        saveEditBtn.visibility = View.VISIBLE

                        batalEditBtn.visibility = View.VISIBLE

                        progressEditLoading.visibility = View.GONE
                    }

                    if (result.message != ""){
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                    if (this@BottomSheetPakan.arguments != null) {
                        val pakanId = this@BottomSheetPakan.arguments!!.getInt("pakan_id")

                        pakanViewModel.updateLocalPakan(
                            pakanId, binding.namaPakanEt.text.toString().trim(),
                            binding.deskripsiPakanEt.text.toString().trim()
                        )
                    }

                    pakanViewModel.donePutUpdateRequest()

                    this.dismiss()
                }
                is NetworkResult.Error -> {
                    binding.apply {
                        saveEditBtn.visibility = View.VISIBLE

                        batalEditBtn.visibility = View.VISIBLE

                        progressEditLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
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

    fun savePakan() {
        binding.apply {
            if (isEntryValid(
                    namaPakanEt.text.toString().trim(),
                )
            ) {
                if (this@BottomSheetPakan.arguments != null) {
                    val pakanId = this@BottomSheetPakan.arguments!!.getInt("pakan_id")

                    pakanViewModel.updatePakan(
                        pakanId, namaPakanEt.text.toString().trim(),
                        deskripsiPakanEt.text.toString().trim()
                    )
                } else {
                    pakanViewModel.insertPakan(
                        namaPakanEt.text.toString().trim(),
                        deskripsiPakanEt.text.toString().trim()
                    )
                }
            } else {
                if (TextUtils.isEmpty(namaPakanEt.text)) {
                    namaPakanEt.error = "Nama pakan harus diisi!"
                }
            }
        }
    }

    private fun isEntryValid(jenis_pakan: String): Boolean {
        return pakanViewModel.isEntryValid(jenis_pakan)
    }

    private fun switchEditButton() {
        if (this.arguments != null) {
            binding.apply {
                savePakanBtn.setOnClickListener {
                    editState = true
                    checkEditState(editState)
                }

                batalEditBtn.setOnClickListener {
                    editState = false
                    val pakanId = arguments!!.getInt("pakan_id")
                    pakanViewModel.loadPakanData(pakanId).observeOnce(viewLifecycleOwner, {
                        bind(it)
                    })
                    checkEditState(editState)
                }
            }
        } else {
            binding.savePakanBtn.setOnClickListener {
                savePakan()
            }
        }
    }

    private fun checkEditState(editState: Boolean) {
        if (!editState) {
            binding.apply {
                namaPakanEt.isEnabled = false
                deskripsiPakanEt.isEnabled = false

                savePakanBtn.text = getString(R.string.edit)

                savePakanBtn.visibility = View.VISIBLE
                batalEditBtn.visibility = View.GONE
                saveEditBtn.visibility = View.GONE
            }
        } else {
            binding.apply {
                namaPakanEt.isEnabled = true
                deskripsiPakanEt.isEnabled = true

                savePakanBtn.visibility = View.GONE
                batalEditBtn.visibility = View.VISIBLE
                saveEditBtn.visibility = View.VISIBLE
            }
        }
    }
}