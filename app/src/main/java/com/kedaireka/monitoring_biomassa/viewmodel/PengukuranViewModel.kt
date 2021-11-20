package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.*
import com.kedaireka.monitoring_biomassa.data.domain.PengukuranDomain
import com.kedaireka.monitoring_biomassa.database.dao.PengukuranDAO
import com.kedaireka.monitoring_biomassa.database.entity.Pengukuran
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PengukuranViewModel @Inject constructor(
    private val pengukuranDAO: PengukuranDAO,
    private val pengukuranMapper: EntityMapper<Pengukuran, PengukuranDomain>
): ViewModel() {
    private val _selectedTanggalUkur = MutableLiveData<Long>()
    val selectedTanggalUkur: LiveData<Long> = _selectedTanggalUkur

    private val _selectedBiotaid = MutableLiveData<Int>()
    val selectedBiotaid: LiveData<Int> = _selectedBiotaid

    fun getAll(biotaid: Int): LiveData<List<PengukuranDomain>> = Transformations.map(pengukuranDAO.getAll(biotaid).asLiveData()){list->
        list.map { pengukuranMapper.mapFromEntity(it) }
    }

    fun selectBiotaId(biotaid: Int){
        _selectedBiotaid.value = biotaid
    }

    fun onSelectDateTime(dateTime: Long) {
        _selectedTanggalUkur.value = dateTime }


    fun isEntryValid(panjang: String, bobot: String): Boolean{
        return !(panjang.isBlank() || bobot.isBlank() || _selectedBiotaid.value == null || _selectedTanggalUkur.value == null)
    }

    fun insertPengukuran(panjang: String, bobot: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                pengukuranDAO.insertOne(
                    Pengukuran(
                        panjang = panjang.toDouble(),
                        bobot =  bobot.toDouble(),
                        tanggal_ukur = _selectedTanggalUkur.value!!,
                        biotaid = _selectedBiotaid.value!!
                    )
                )
            }
        }
    }
}