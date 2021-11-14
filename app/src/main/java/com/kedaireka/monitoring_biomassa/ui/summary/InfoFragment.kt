package com.kedaireka.monitoring_biomassa.ui.summary

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.databinding.FragmentInfoBinding
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString
import com.kedaireka.monitoring_biomassa.viewmodel.BiotaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class InfoFragment : Fragment() {
    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private lateinit var binding: FragmentInfoBinding

    private lateinit var navController: NavController

    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInfoBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setupFragment()
    }

    private fun setupFragment() {
        kerambaViewModel.loadedKerambaid.observe(viewLifecycleOwner, { id ->
            kerambaViewModel.loadKerambaData(id).observe(viewLifecycleOwner, { keramba ->
                with(binding) {
                    namaKerambaTv.text = keramba.nama_keramba

                    tanggalInstallTv.text = convertLongToDateString(keramba.tanggal_install)

                    ukuranKerambaTv.text =
                        getString(R.string.meter_kubik, keramba.ukuran.toString())

                    editBtn.setOnClickListener {
                        navController.navigate(
                            SummaryFragmentDirections.actionSummaryFragmentToAddKerambaFragment(
                                keramba.kerambaid
                            )
                        )
                    }
                }
            })

            biotaViewModel.getAllBiota(id).observe(viewLifecycleOwner, { list ->
                if (list.isNotEmpty()) {
                    val barEntries = ArrayList<PieEntry>()

                    //mapping data if data is double
                    val mappedData = mutableMapOf<String, Int>()

                    list.forEach { mappedData[it.jenis_biota.lowercase()] = 0 }

                    list.forEach { mappedData[it.jenis_biota.lowercase()] = mappedData[it.jenis_biota.lowercase()]!! + it.jumlah_bibit }

                    mappedData.forEach{ barEntries.add(PieEntry(it.value.toFloat(), it.key)) }

                    val dataSet = PieDataSet(barEntries, "")

                    dataSet.setDrawIcons(false)
                    dataSet.sliceSpace = 3f
                    dataSet.iconsOffset = MPPointF(0F, 40F)
                    dataSet.selectionShift = 5f
                    dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

                    val dataNow = PieData(dataSet)

//                    dataNow.setValueFormatter(PercentFormatter())

                    pieChart = binding.biotaChart

                    pieChart.apply {
                        data = dataNow
//                        setUsePercentValues(true)
                        animateY(1400, Easing.EaseInOutQuad)
                        setDrawEntryLabels(false)
                        holeRadius = 58f
                        description.text = ""

                        transparentCircleRadius = 61f
                        isDrawHoleEnabled = true
                        setHoleColor(Color.WHITE)
                        centerText = "Komposisi Biota"

                        invalidate()
                    }
                } else{
                    binding.chartCard.visibility = View.GONE
                }
            })

        })
    }

}