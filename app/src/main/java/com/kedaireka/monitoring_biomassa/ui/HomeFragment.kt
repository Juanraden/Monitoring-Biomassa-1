package com.kedaireka.monitoring_biomassa.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.kedaireka.monitoring_biomassa.adapter.KerambaListAdapter
import com.kedaireka.monitoring_biomassa.adapter.OnClickListener
import com.kedaireka.monitoring_biomassa.databinding.FragmentHomeBinding
import com.kedaireka.monitoring_biomassa.viewmodel.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var navController: NavController

    private val kerambaViewModel by viewModels<KerambaViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this.viewLifecycleOwner

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val kerambaListAdapter = KerambaListAdapter(
            OnClickListener {
                navController.navigate(HomeFragmentDirections.actionHomeFragmentToSummaryFragment())
            }
        )

        binding.kerambaList.adapter = kerambaListAdapter

        kerambaViewModel.allKeramba.observe(viewLifecycleOwner, {
            kerambaListAdapter.submitList(it)

            binding.loadingSpinner.visibility = View.GONE
        })
    }
}