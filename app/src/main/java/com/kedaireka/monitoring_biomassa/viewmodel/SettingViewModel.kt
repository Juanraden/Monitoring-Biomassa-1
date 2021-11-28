package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kedaireka.monitoring_biomassa.database.dao.KerambaDAO
import com.kedaireka.monitoring_biomassa.database.dao.PakanDAO
import com.kedaireka.monitoring_biomassa.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val kerambaDAO: KerambaDAO,
    private val pakanDAO: PakanDAO,
    private val loginRepository: LoginRepository
): ViewModel() {
    fun logOut(){
        loginRepository.logOutUser()

        viewModelScope.launch {
            withContext(Dispatchers.IO){
                kerambaDAO.deleteAllKeramba()

                pakanDAO.deleteAllPakan()
            }
        }
    }
}