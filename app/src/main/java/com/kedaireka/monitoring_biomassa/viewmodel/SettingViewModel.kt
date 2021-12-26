package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kedaireka.monitoring_biomassa.repository.KerambaRepository
import com.kedaireka.monitoring_biomassa.repository.LoginRepository
import com.kedaireka.monitoring_biomassa.repository.PakanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val kerambaRepository: KerambaRepository,
    private val pakanRepository: PakanRepository,
    private val loginRepository: LoginRepository
): ViewModel() {
    fun logOut(){
        loginRepository.logOutUser()

        viewModelScope.launch {
            kerambaRepository.deleteAllLocalKeramba()

            pakanRepository.deleteAllLocalPakan()
        }
    }
}