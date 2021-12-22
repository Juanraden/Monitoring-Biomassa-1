package com.kedaireka.monitoring_biomassa.ui.summary

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Transformations
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.adapter.SummaryFragmentTabAdapter
import com.kedaireka.monitoring_biomassa.databinding.FragmentSummaryBinding
import com.kedaireka.monitoring_biomassa.viewmodel.BiotaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.PanenViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SummaryFragment : Fragment() {
    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private val panenViewModel by activityViewModels<PanenViewModel>()

    private lateinit var binding: FragmentSummaryBinding

    private lateinit var navController: NavController

    companion object;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSummaryBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()

        setupTabLayout()

        setupToolbarDownload()

        fetchBiota()
    }

    private fun setupToolbarDownload() {
        binding.toolbarFragment.inflateMenu(R.menu.download_menu)

        binding.toolbarFragment.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.appDownloadBar -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        if (checkPermissions()) {
                            if (kerambaViewModel.loadedKerambaId.value != null) {
                                panenViewModel.downloadExportedData(
                                    kerambaViewModel.loadedKerambaId.value!!,
                                    binding.toolbarFragment.title.toString()
                                )
                            }
                        } else {
                            askPermissions()
                        }
                    } else {
                        if (kerambaViewModel.loadedKerambaId.value != null) {
                            panenViewModel.downloadExportedData(
                                kerambaViewModel.loadedKerambaId.value!!,
                                binding.toolbarFragment.title.toString()
                            )
                        }
                    }

                    true
                }
                else -> false
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun askPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireNotNull(activity),
                WRITE_EXTERNAL_STORAGE
            )
        ) {
            AlertDialog.Builder(requireContext())
                .setTitle("Permission required")
                .setMessage("Permission required to download file from the Web.")
                .setPositiveButton("Allow") { _, _ ->
                    requestMultiplePermissions.launch(
                        arrayOf(WRITE_EXTERNAL_STORAGE)
                    )
                }
                .setNegativeButton("Deny") { dialog, _ -> dialog.cancel() }
                .show()
        } else {
            requestMultiplePermissions.launch(
                arrayOf(WRITE_EXTERNAL_STORAGE)
            )
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.d("SummmaryFragment", "${it.key} = ${it.value}")
            }
            if (permissions[WRITE_EXTERNAL_STORAGE] == true) {
                Log.d("SummmaryFragment", "Permission granted")

                if (kerambaViewModel.loadedKerambaId.value != null) {
                    panenViewModel.downloadExportedData(
                        kerambaViewModel.loadedKerambaId.value!!,
                        binding.toolbarFragment.title.toString()
                    )
                }
            } else {
                Log.d("SummmaryFragment", "Permission not granted")
            }
        }

    private fun fetchBiota() {
        if (kerambaViewModel.loadedKerambaId.value != null) {
            biotaViewModel.fetchBiota(kerambaViewModel.loadedKerambaId.value!!)
        }
    }


    private fun setupTabLayout() {
        val tabAdapter = SummaryFragmentTabAdapter(this)

        with(binding) {
            pager.adapter = tabAdapter

            TabLayoutMediator(tabLayout, pager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.info)
                    1 -> tab.text = getString(R.string.biota)
                    2 -> tab.text = getString(R.string.pakan)
                    3 -> tab.text = getString(R.string.panen)
                }
            }.attach()
        }
    }

    private fun setupNavigation() {
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.homeFragment, R.id.settingsFragment))

        binding.toolbarFragment.setupWithNavController(navController, appBarConfiguration)

        binding.toolbarFragment.setNavigationOnClickListener {
            navController.navigateUp(appBarConfiguration)
        }

        Transformations.switchMap(
            kerambaViewModel.loadedKerambaId
        ) { id ->
            kerambaViewModel.loadKerambaData(id)
        }.observe(viewLifecycleOwner, { keramba ->
            binding.toolbarFragment.title = keramba.nama_keramba
        })
    }
}