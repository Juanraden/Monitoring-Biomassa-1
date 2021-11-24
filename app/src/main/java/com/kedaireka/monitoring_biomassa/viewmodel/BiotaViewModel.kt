package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.*
import com.kedaireka.monitoring_biomassa.data.domain.BiotaDomain
import com.kedaireka.monitoring_biomassa.database.dao.BiotaDAO
import com.kedaireka.monitoring_biomassa.database.entity.Biota
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BiotaViewModel @Inject constructor(
    private val biotaDao: BiotaDAO,
    private val biotaMapper: EntityMapper<Biota, BiotaDomain>): ViewModel() {

    private val _loadedBiotaId = MutableLiveData<Int>()
    val loadedBiotaId: LiveData<Int> = _loadedBiotaId

    private val _selectedKerambaId = MutableLiveData<Int>()

    private val _selectedTanggalTebar = MutableLiveData<Long>()
    val selectedTanggalTebar: LiveData<Long> = _selectedTanggalTebar

    fun loadBiotaData(id: Int): LiveData<BiotaDomain>{
        return Transformations.map(biotaDao.getById(id).asLiveData()){biotaMapper.mapFromEntity(it)}
    }

    fun setBiotaId(id: Int){
        _loadedBiotaId.value = id
    }

    fun getAllBiota(id: Int): LiveData<List<BiotaDomain>> = Transformations.map(biotaDao.getAll(id).asLiveData()){list->
        list.map { biotaMapper.mapFromEntity(it) }
    }

    fun getAllBiotaHistory(id: Int): LiveData<List<BiotaDomain>> = Transformations.map(biotaDao.getAllHistory(id).asLiveData()) { list ->
        list.map { biotaMapper.mapFromEntity(it) }
    }

    fun selectkerambaId(id: Int){
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
                        keramba_id = _selectedKerambaId.value!!,
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

    fun updateBiota(biota_id: Int, jenis: String, bobot: String, panjang: String, jumlah: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                biotaDao.updateOne(
                    Biota(
                        biota_id = biota_id,
                        jenis_biota = jenis,
                        bobot = bobot.toDouble(),
                        panjang = panjang.toDouble(),
                        jumlah_bibit = jumlah.toInt(),
                        tanggal_tebar = _selectedTanggalTebar.value!!,
                        keramba_id = _selectedKerambaId.value!!
                    )
                )
            }
        }
    }
}