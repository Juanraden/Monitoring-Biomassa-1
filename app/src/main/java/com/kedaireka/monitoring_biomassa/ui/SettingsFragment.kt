package com.kedaireka.monitoring_biomassa.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.databinding.FragmentSettingsBinding
import com.kedaireka.monitoring_biomassa.ui.login.LoginFragment

class SettingsFragment : Fragment(){
    private lateinit var binding: FragmentSettingsBinding

    private val _fragmentTag = "Setting_Fragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            settingsFragment = this@SettingsFragment
        }
    }

    fun launchUri(view: View){
        when (view.id){
            R.id.about_tv -> {
                Log.i(_fragmentTag, "About KJABB CLicked")
            }
            R.id.web_tv -> {
                Log.i(_fragmentTag, "Website Monitoring CLicked")
            }
            R.id.sosmed_tv -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/cemebsakedaireka/"))
                startActivity(intent)
            }
            R.id.logout_button -> {
                val host = NavHostFragment.create(R.navigation.nav_graph)
                val vr = activity?.supportFragmentManager?.beginTransaction()
                vr?.replace(R.id.nav_host_fragment, host)
                vr?.setPrimaryNavigationFragment(host)
                vr?.commit()

            }
            else -> {
                Log.i(_fragmentTag, "none")
            }
        }

    }
}