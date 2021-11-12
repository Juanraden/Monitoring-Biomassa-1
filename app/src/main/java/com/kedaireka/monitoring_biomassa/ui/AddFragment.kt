package com.kedaireka.monitoring_biomassa.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.databinding.FragmentAddBinding

class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentAddBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            addFragment = this@AddFragment
        }
        setupNavigation()
    }

    fun navigateToFragment(view: View){
        when(view.id){
            R.id.add_keramba_btn -> navController.navigate(AddFragmentDirections.actionAddFragmentToAddKerambaFragment())
            R.id.add_biota_btn -> navController.navigate(AddFragmentDirections.actionAddFragmentToAddBiotaFragment())
            else -> return
        }

    }

    private fun setupNavigation() {

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.addFragment,R.id.settingsFragment))

        binding.toolbarFragment.setupWithNavController(navController, appBarConfiguration)

        binding.toolbarFragment.setNavigationOnClickListener {
            navController.navigateUp(appBarConfiguration)
        }
    }
}