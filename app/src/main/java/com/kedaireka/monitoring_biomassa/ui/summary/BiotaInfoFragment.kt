package com.kedaireka.monitoring_biomassa.ui.summary

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.PengukuranDomain
import com.kedaireka.monitoring_biomassa.databinding.FragmentBiotaInfoBinding
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetBiota
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString
import com.kedaireka.monitoring_biomassa.viewmodel.BiotaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.PengukuranViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class BiotaInfoFragment : Fragment() {
    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val pengukuranViewModel by activityViewModels<PengukuranViewModel>()

    private var listData = ArrayList<PengukuranDomain>()

    private lateinit var binding: FragmentBiotaInfoBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBiotaInfoBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFragment()
    }

    private fun setupFragment() {
        biotaViewModel.loadedBiotaid.observe(viewLifecycleOwner, { biotaid ->
            biotaViewModel.loadBiotaData(biotaid).observe(viewLifecycleOwner, { biota ->
                with(binding) {
                    jenisBiotaTv.text = biota.jenis_biota

                    bobotBibitTv.text = biota.bobot.toString()

                    ukuranBibitTv.text = biota.panjang.toString()

                    jumlahBibitTv.text = biota.jumlah_bibit.toString()

                    tanggalTebarTv.text = convertLongToDateString(biota.tanggal_tebar)
                }
            })

            kerambaViewModel.loadedKerambaid.observe(viewLifecycleOwner, { kerambaid ->
                binding.editBtn.setOnClickListener { onEditBiota(kerambaid, biotaid) }
            })

            setupLineChart(biotaid)
        })
    }

    private fun setupLineChart(biotaid: Int) {
        pengukuranViewModel.getAll(biotaid).observe(viewLifecycleOwner, { list ->

            listData.clear()

            if (list.isEmpty()) {
                binding.panjangChartCard.visibility = View.GONE

                binding.bobotChartCard.visibility = View.GONE
            } else {
                binding.panjangChartCard.visibility = View.VISIBLE

                binding.bobotChartCard.visibility = View.VISIBLE

                if (list.size > 4) {
                    listData.addAll(list.slice(0..3).reversed())
                } else {
                    listData.addAll(list.reversed())
                }

                initChart(binding.panjangChart, listData)

                initChart(binding.bobotChart, listData)
            }
        })
    }


    private fun initChart(chart: LineChart, list: List<PengukuranDomain>) {
        chart.apply {
            axisLeft.setDrawGridLines(false)

            axisRight.isEnabled = false

            description.isEnabled = false

            val xAxis: XAxis = this.xAxis

            xAxis.setDrawGridLines(false)

            xAxis.setDrawAxisLine(false)

            legend.isEnabled = true

            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP

            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER

            legend.orientation = Legend.LegendOrientation.HORIZONTAL

            legend.setDrawInside(false)

            animateX(1500, Easing.EaseInSine)

            xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE

            xAxis.valueFormatter = object : IndexAxisValueFormatter() {
                @SuppressLint("SimpleDateFormat")
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    val index = value.toInt()

                    return if (index < list.size) {
                        SimpleDateFormat("d MMM")
                            .format(list[index].tanggal_ukur).toString()
                    } else {
                        ""
                    }
                }
            }

            xAxis.setDrawLabels(true)

            val entries: ArrayList<Entry> = ArrayList()

            when (chart.id) {
                R.id.panjang_chart -> {
                    xAxis.granularity = list.maxOf { it.panjang }.toFloat()

                    for (i in list.indices) {
                        val panjang = list[i]

                        entries.add(Entry(i.toFloat(), panjang.panjang.toFloat()))

                        val lineDataSet = LineDataSet(entries, "Panjang Biota")

                        val data = LineData(lineDataSet)

                        this.data = data

                        this.invalidate()
                    }
                }

                R.id.bobot_chart -> {
                    xAxis.granularity = list.maxOf { it.bobot }.toFloat()

                    for (i in list.indices) {
                        val bobot = list[i]

                        entries.add(Entry(i.toFloat(), bobot.bobot.toFloat()))

                        val lineDataSet = LineDataSet(entries, "Bobot Biota")

                        val data = LineData(lineDataSet)

                        this.data = data

                        this.invalidate()
                    }
                }
            }
        }
    }

    private fun onEditBiota(kerambaid: Int, biotaid: Int) {
        if (childFragmentManager.findFragmentByTag("BottomSheetBiota") == null) {

            val bundle = Bundle()

            bundle.apply {
                putInt("kerambaid", kerambaid)
                putInt("biotaid", biotaid)
            }

            val bottomSheetBiota = BottomSheetBiota()

            bottomSheetBiota.arguments = bundle

            bottomSheetBiota.show(childFragmentManager, "BottomSheetBiota")
        }
    }
}