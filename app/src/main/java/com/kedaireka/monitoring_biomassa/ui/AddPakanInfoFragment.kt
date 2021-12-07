package com.kedaireka.monitoring_biomassa.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Transformations
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.PakanDomain
import com.kedaireka.monitoring_biomassa.databinding.FragmentAddPakanInfoBinding
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetPakan
import com.kedaireka.monitoring_biomassa.viewmodel.PakanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPakanInfoFragment : Fragment() {
    private val pakanViewModel by activityViewModels<PakanViewModel>()

    private lateinit var binding: FragmentAddPakanInfoBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddPakanInfoBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        setupFragment()

        setupNavigation()
    }

    private fun setupFragment() {
        Transformations.switchMap(pakanViewModel.loadedPakanId){ pakan_id ->
            pakanViewModel.loadPakanData(pakan_id)
        }.observe(viewLifecycleOwner, { pakan-> bind(pakan) })
    }

    private fun bind(pakan: PakanDomain) {
        with(binding) {
            namaPakanTv.text = pakan.jenis_pakan

            deskripsiTv.text = pakan.deskripsi

            editBtn.setOnClickListener {
                if (childFragmentManager.findFragmentByTag("BottomSheetPakan") == null) {
                    val bottomSheetPakan = BottomSheetPakan()

                    val bundle = Bundle()

                    bundle.putInt("pakan_id", pakan.pakan_id)

                    bottomSheetPakan.arguments = bundle

                    bottomSheetPakan.show(childFragmentManager, "BottomSheetPakan")
                }
            }
        }
    }

    private fun setupNavigation() {
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.settingsFragment))

        binding.toolbarFragment.setupWithNavController(navController, appBarConfiguration)

        binding.toolbarFragment.setNavigationOnClickListener {
            navController.navigateUp(appBarConfiguration)
        }

        Transformations.switchMap(
            pakanViewModel.loadedPakanId){ id ->
            pakanViewModel.loadPakanData(id)
        }.observe(viewLifecycleOwner, { pakan ->
            binding.toolbarFragment.title = pakan.jenis_pakan
        })
    }
}
