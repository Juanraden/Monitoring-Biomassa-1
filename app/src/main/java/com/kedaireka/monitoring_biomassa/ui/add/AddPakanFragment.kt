package com.kedaireka.monitoring_biomassa.ui.add

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.adapter.HeaderButtonAdapter
import com.kedaireka.monitoring_biomassa.adapter.PakanListAdapter
import com.kedaireka.monitoring_biomassa.databinding.FragmentAddPakanBinding
import com.kedaireka.monitoring_biomassa.viewmodel.PakanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPakanFragment : Fragment() {

    private val pakanViewModel by viewModels<PakanViewModel>()

    private lateinit var binding: FragmentAddPakanBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentAddPakanBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            addPakanFragment = this@AddPakanFragment
        }

        setupPakanList()
    }

    private fun setupPakanList() {
        val pakanListAdapter = PakanListAdapter()

        val pakanHeaderAdapter = HeaderButtonAdapter{}

        val concatAdapter = ConcatAdapter(pakanHeaderAdapter, pakanListAdapter)

        binding.pakanList.adapter = concatAdapter

        pakanViewModel.getAll().observe(viewLifecycleOwner, {
            pakanListAdapter.submitList(it)

            binding.loadingSpinner.visibility = View.GONE
        })
    }
}