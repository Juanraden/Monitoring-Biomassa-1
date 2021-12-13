package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kedaireka.monitoring_biomassa.database.dao.PanenDAO
import com.kedaireka.monitoring_biomassa.database.relation.PanenAndBiota
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PanenViewModel @Inject constructor(
    private val panenDAO: PanenDAO
): ViewModel(){
    fun getlistPanen(): LiveData<List<PanenAndBiota>> = panenDAO.getAllPanenAndBiota().asLiveData()
}