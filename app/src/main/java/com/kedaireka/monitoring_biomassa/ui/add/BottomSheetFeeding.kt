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
import com.kedaireka.monitoring_biomassa.data.domain.FeedingDomain
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.databinding.BottomSheetFeedingBinding
import com.kedaireka.monitoring_biomassa.ui.DatePickerFragment
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString
import com.kedaireka.monitoring_biomassa.util.convertStringToDateLong
import com.kedaireka.monitoring_biomassa.viewmodel.FeedingViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class BottomSheetFeeding : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener,
    DatePickerDialog.OnDateSetListener {

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val feedingViewModel by activityViewModels<FeedingViewModel>()

    private lateinit var binding: BottomSheetFeedingBinding

    private lateinit var mapKeramba: Map<String, Int>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetFeedingBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivClose.setOnClickListener { dismiss() }

        binding.bottomSheetFeeding = this@BottomSheetFeeding

        setupDropdown()

        if (this@BottomSheetFeeding.arguments != null) {
            val feedingId: Int = this@BottomSheetFeeding.arguments!!.getInt("feeding_id")

            if (feedingId > 0) {
                feedingViewModel.loadFeedingData(feedingId)
                    .observe(viewLifecycleOwner, { bind(it) })
            }
        }

        setupObserver()
    }

    private fun setupObserver() {
        feedingViewModel.requestPostAddResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                    binding.saveFeedingBtn.visibility = View.VISIBLE

                    binding.progressLoading.visibility = View.GONE
                }
                is NetworkResult.Loading -> {
                    binding.saveFeedingBtn.visibility = View.GONE

                    binding.progressLoading.visibility = View.VISIBLE
                }
                is NetworkResult.Loaded -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    if (feedingViewModel.selectedKerambaId.value != null) {
                        feedingViewModel.fetchFeeding(feedingViewModel.selectedKerambaId.value!!)

                        binding.saveFeedingBtn.visibility = View.VISIBLE

                        binding.progressLoading.visibility = View.GONE

                        feedingViewModel.donePostAddRequest()
                    }

                    this.dismiss()
                }
                is NetworkResult.Error -> {
                    binding.saveFeedingBtn.visibility = View.VISIBLE

                    binding.progressLoading.visibility = View.GONE

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        feedingViewModel.requestPutUpdateResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                    binding.saveFeedingBtn.visibility = View.VISIBLE

                    binding.progressLoading.visibility = View.GONE
                }
                is NetworkResult.Loading -> {
                }
                is NetworkResult.Loaded -> {
                    if (this@BottomSheetFeeding.arguments != null) {
                        val feedingId: Int =
                            this@BottomSheetFeeding.arguments!!.getInt("feeding_id")

                        feedingViewModel.updateLocalFeeding(
                            feedingId,
                            if (binding.tanggalFeedingEt.text.toString() != "") {
                                convertStringToDateLong(
                                    binding.tanggalFeedingEt.text.toString(),
                                    "EEEE dd-MMM-yyyy"
                                )
                            } else {
                                0L
                            }
                        )

                        feedingViewModel.donePutUpdateRequest()
                    }
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

    private fun bind(feedingDomain: FeedingDomain) {
        binding.apply {
            titleTv.text =
                convertLongToDateString(feedingDomain.tanggal_feeding, "EEEE dd-MMM-yyyy")

            binding.tanggalFeedingEt.setText(
                convertLongToDateString(feedingDomain.tanggal_feeding, "EEEE dd-MMM-yyyy"),
                TextView.BufferType.EDITABLE
            )

            saveFeedingBtn.text = getString(R.string.edit_biota)
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

    fun saveFeeding() {
        binding.apply {
            if (isEntryValid(
                    if (tanggalFeedingEt.text.toString() != "") {
                        convertStringToDateLong(
                            tanggalFeedingEt.text.toString(),
                            "EEEE dd-MMM-yyyy"
                        )
                    } else {
                        0L
                    }
                )
            ) {
                if (this@BottomSheetFeeding.arguments != null) {
                    val feedingId: Int = this@BottomSheetFeeding.arguments!!.getInt("feeding_id")

                    if (feedingId > 0) {
                        feedingViewModel.updateFeeding(
                            feedingId,
                            if (tanggalFeedingEt.text.toString() != "") {
                                convertStringToDateLong(
                                    tanggalFeedingEt.text.toString(),
                                    "EEEE dd-MMM-yyyy"
                                )
                            } else {
                                0L
                            }
                        )
                    } else {
                        feedingViewModel.insertFeeding(
                            if (tanggalFeedingEt.text.toString() != "") {
                                convertStringToDateLong(
                                    tanggalFeedingEt.text.toString(),
                                    "EEEE dd-MMM-yyyy"
                                )
                            } else {
                                0L
                            }
                        )
                    }
                } else {
                    feedingViewModel.insertFeeding(
                        if (tanggalFeedingEt.text.toString() != "") {
                            convertStringToDateLong(
                                tanggalFeedingEt.text.toString(),
                                "EEEE dd-MMM-yyyy"
                            )
                        } else {
                            0L
                        }
                    )
                }
            } else {
                if (TextUtils.isEmpty(tanggalFeedingEt.text)) {
                    tanggalFeedingEt.error = "Tanggal pemberian pakan harus diketahui!"
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
        tanggal: Long
    ): Boolean {
        return feedingViewModel.isEntryValid(tanggal)
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

            if (this@BottomSheetFeeding.arguments != null) {
                val kerambaId: Int = this@BottomSheetFeeding.arguments!!.getInt("keramba_id")

                val kerambaIdlist: List<Int> = mapKeramba.values.toList()

                val index: Int = kerambaIdlist.indexOf(kerambaId)

                binding.kerambaDropdown.setSelection(index)

            }

            binding.kerambaDropdown.onItemSelectedListener = this@BottomSheetFeeding
        })
    }

    override fun onItemSelected(parent: AdapterView<*>, p1: View?, pos: Int, id: Long) {
        val namaKeramba = parent.getItemAtPosition(pos)

        if (namaKeramba != null) {
            feedingViewModel.selectKerambaId(mapKeramba[namaKeramba]!!)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate: Calendar = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)

        binding.tanggalFeedingEt.setText(
            convertLongToDateString(selectedDate.timeInMillis, "EEEE dd-MMM-yyyy"),
            TextView.BufferType.EDITABLE
        )
    }
}