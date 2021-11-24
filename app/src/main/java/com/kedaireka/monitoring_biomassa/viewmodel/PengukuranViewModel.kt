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

    private val _selectedBiotaId = MutableLiveData<Int>()
    val selectedBiotaId: LiveData<Int> = _selectedBiotaId

    fun getAll(biota_id: Int): LiveData<List<PengukuranDomain>> = Transformations.map(pengukuranDAO.getAll(biota_id).asLiveData()){list->
        list.map { pengukuranMapper.mapFromEntity(it) }
    }

    fun selectBiotaId(biota_id: Int){
        _selectedBiotaId.value = biota_id
    }

    fun onSelectDateTime(dateTime: Long) {
        _selectedTanggalUkur.value = dateTime }


    fun isEntryValid(panjang: String, bobot: String): Boolean{
        return !(panjang.isBlank() || bobot.isBlank() || _selectedBiotaId.value == null || _selectedTanggalUkur.value == null)
    }

    fun insertPengukuran(panjang: String, bobot: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                pengukuranDAO.insertOne(
                    Pengukuran(
                        panjang = panjang.toDouble(),
                        bobot =  bobot.toDouble(),
                        tanggal_ukur = _selectedTanggalUkur.value!!,
                        biota_id = _selectedBiotaId.value!!
                    )
                )
            }
        }
    }
}