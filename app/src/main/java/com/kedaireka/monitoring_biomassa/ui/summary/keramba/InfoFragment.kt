package com.kedaireka.monitoring_biomassa.ui.summary.keramba

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Transformations
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.BiotaDomain
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.databinding.FragmentInfoBinding
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetKeramba
import com.kedaireka.monitoring_biomassa.ui.summary.SummaryFragmentDirections
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        Transformations.switchMap(kerambaViewModel.loadedKerambaId) { keramba_id ->
            kerambaViewModel.loadKerambaData(keramba_id)
        }.observe(viewLifecycleOwner, { keramba -> bind(keramba) })

        Transformations.switchMap(kerambaViewModel.loadedKerambaId) { keramba_id ->

            biotaViewModel.getAllBiota(keramba_id)

        }.observe(viewLifecycleOwner, { list ->
            if (biotaViewModel.requestGetResult.value is NetworkResult.Loaded || biotaViewModel.requestGetResult.value is NetworkResult.Error) {
                initBiotaChart(list)
            }
        })

        binding.biotaHistoryBtn.setOnClickListener {
            navController.navigate(
                SummaryFragmentDirections.actionSummaryFragmentToBiotaHistoryFragment()
            )
        }
    }

    private fun bind(keramba: KerambaDomain) {
        with(binding) {
            namaKerambaTv.text = keramba.nama_keramba

            tanggalInstallTv.text =
                convertLongToDateString(keramba.tanggal_install, "EEEE dd-MMM-yyyy")

            ukuranKerambaTv.text =
                getString(R.string.meter_kubik, keramba.ukuran.toString())

            editBtn.setOnClickListener {
                if (childFragmentManager.findFragmentByTag("BottomSheetKeramba") == null) {
                    val bottomSheetKeramba = BottomSheetKeramba()

                    val bundle = Bundle()

                    bundle.putInt("keramba_id", keramba.keramba_id)

                    bottomSheetKeramba.arguments = bundle

                    bottomSheetKeramba.show(childFragmentManager, "BottomSheetKeramba")
                }
            }
        }
    }

    private fun initBiotaChart(list: List<BiotaDomain>) {
        if (list.isEmpty()) {
            binding.chartCard.visibility = View.GONE
        } else {
            binding.chartCard.visibility = View.VISIBLE

            val barEntries = ArrayList<PieEntry>()

            //mapping data if data is double
            val mappedData = mutableMapOf<String, Int>()

            list.forEach { mappedData[it.jenis_biota.lowercase()] = 0 }

            list.forEach {
                mappedData[it.jenis_biota.lowercase()] =
                    mappedData[it.jenis_biota.lowercase()]!! + it.jumlah_bibit
            }

            mappedData.forEach { barEntries.add(PieEntry(it.value.toFloat(), it.key)) }

            val dataSet = PieDataSet(barEntries, "")

            dataSet.setDrawIcons(false)
            dataSet.sliceSpace = 3f
            dataSet.iconsOffset = MPPointF(0F, 40F)
            dataSet.selectionShift = 5f
            dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

            val dataNow = PieData(dataSet)
            dataNow.setValueTextSize(20f)
            dataNow.setValueTextColor(Color.WHITE)

            binding.biotaChart.apply {
                data = dataNow
                animateY(1400, Easing.EaseInOutQuad)
                setDrawEntryLabels(false)
                holeRadius = 58f
                description.text = ""

                legend.textSize = 15f
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        legend.textColor = Color.WHITE
                    }
                }

                transparentCircleRadius = 61f
                isDrawHoleEnabled = true
                setHoleColor(Color.WHITE)
                centerText = "Komposisi Biota"
                setCenterTextSize(15f)

                invalidate()
            }
        }
    }
}