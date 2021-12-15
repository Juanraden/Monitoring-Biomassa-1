package com.kedaireka.monitoring_biomassa.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.databinding.FragmentSettingsBinding
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import com.kedaireka.monitoring_biomassa.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    private val settingViewModel by viewModels<SettingViewModel>()

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val _fragmentTag = "Setting_Fragment"

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            settingsFragment = this@SettingsFragment
        }

        setupNavigation()
    }

    fun launchUri(view: View) {
        when (view.id) {
            R.id.about_tv -> {
                Log.i(_fragmentTag, "About KJABB CLicked")
            }
            R.id.web_tv -> {
                Log.i(_fragmentTag, "Website Monitoring CLicked")
            }
            R.id.sosmed_tv -> {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/cemebsakedaireka/")
                )
                startActivity(intent)
            }
            R.id.logout_button -> {
                settingViewModel.logOut()

                kerambaViewModel.restartInit()

                val host = NavHostFragment.create(R.navigation.nav_graph)
                val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
                fragmentTransaction?.replace(R.id.nav_host_fragment, host)
                fragmentTransaction?.setPrimaryNavigationFragment(host)
                fragmentTransaction?.commit()
            }
            else -> {
                Log.i(_fragmentTag, "none")
            }
        }
    }

    private fun setupNavigation() {
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.homeFragment, R.id.settingsFragment))

        binding.toolbarFragment.setupWithNavController(navController, appBarConfiguration)

        binding.toolbarFragment.setNavigationOnClickListener {
            navController.navigateUp(appBarConfiguration)
        }
    }
}