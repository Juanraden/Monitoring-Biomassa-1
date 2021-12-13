package com.kedaireka.monitoring_biomassa.ui.summary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.adapter.HeaderButtonAdapter
import com.kedaireka.monitoring_biomassa.adapter.PanenListAdapter
import com.kedaireka.monitoring_biomassa.databinding.FragmentPanenBinding
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetPakan
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetPanen
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel

class PanenFragment : Fragment() {
    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private lateinit var binding: FragmentPanenBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPanenBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPanenList()
    }

    private fun setupPanenList() {
        val panenHeaderAdapter = HeaderButtonAdapter{
            if (childFragmentManager.findFragmentByTag("BottomSheetPanen") == null) {

                val bundle = Bundle()

                val kerambaId = kerambaViewModel.loadedKerambaId.value!!

                bundle.putInt("keramba_id", kerambaId)

                val bottomSheetPanen = BottomSheetPanen()

                bottomSheetPanen.arguments = bundle

                bottomSheetPanen.show(childFragmentManager, "BottomSheetPanen")
            }
        }

        val panenListAdapter = PanenListAdapter()

        val concatAdapter = ConcatAdapter(panenHeaderAdapter, panenListAdapter)

        binding.panenList.adapter = concatAdapter
    }
}