package com.kedaireka.monitoring_biomassa.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.databinding.FragmentNavBarBinding
import java.lang.RuntimeException

class NavBarFragment : Fragment() {

    private lateinit var binding: FragmentNavBarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNavBarBinding.inflate(inflater, container, false)

        setupNavigation()

        return binding.root
    }

    private fun setupNavigation() {
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.homeFragment, R.id.addFragment, R.id.settingsFragment))

        val nestedNavHostFragment =
            childFragmentManager.findFragmentById(R.id.nested_nav_host_fragment) as NavHostFragment

        val navController = nestedNavHostFragment?.findNavController()

        if (navController != null) {
            binding.bottomNavView.setupWithNavController(navController)

            binding.toolbarFragment.setupWithNavController(navController, appBarConfiguration)

            binding.toolbarFragment.setNavigationOnClickListener {
                navController.navigateUp(appBarConfiguration)
            }
        } else {
            throw RuntimeException("Controller not found")
        }
    }
}