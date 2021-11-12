package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kedaireka.monitoring_biomassa.database.dao.BiotaDAO
import com.kedaireka.monitoring_biomassa.database.entity.Biota
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BiotaViewModel @Inject constructor(private val biotaDao: BiotaDAO): ViewModel() {
    private val _selectedKerambaId = MutableLiveData<Int>()
    val selectedKerambaId: LiveData<Int> = _selectedKerambaId

    private val _selectedTanggalTebar = MutableLiveData<Long>()
    val selectedTanggalTebar: LiveData<Long> = _selectedTanggalTebar

    fun selectKerambaId(id: Int){
        _selectedKerambaId.value = id
    }

    fun onSelectDateTime(date: Long){
        _selectedTanggalTebar.value = date
    }

    fun isEntryValid(jenis: String, bobot: String, panjang: String, jumlah: String): Boolean {
        return !(jenis.isBlank()|| bobot.isBlank() || panjang.isBlank() || jumlah.isBlank() || _selectedTanggalTebar.value == null || _selectedKerambaId.value == null)
    }

    fun insertBiota(jenis: String, bobot: String, panjang: String, jumlah: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                biotaDao.insert(
                    Biota(
                        kerambaid = _selectedKerambaId.value!!,
                        jenis_biota = jenis,
                        bobot = bobot.toDouble(),
                        panjang = panjang.toDouble(),
                        jumlah_bibit = jumlah.toInt(),
                        tanggal_tebar = _selectedTanggalTebar.value!!
                    )
                )
            }
        }
    }
}