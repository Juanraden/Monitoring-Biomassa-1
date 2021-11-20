package com.kedaireka.monitoring_biomassa.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.databinding.FragmentNavBarBinding
import com.kedaireka.monitoring_biomassa.ui.add.BottomSheetAdd

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
        val nestedNavHostFragment =
            childFragmentManager.findFragmentById(R.id.nested_nav_host_fragment) as NavHostFragment

        val navController = nestedNavHostFragment.findNavController()

        binding.bottomNavView.setupWithNavController(navController)

        binding.fab.setOnClickListener {
            if (childFragmentManager.findFragmentByTag("BottomSheetAdd") == null) {
                BottomSheetAdd().show(childFragmentManager, "BottomSheetAdd")
            }
        }
    }
}