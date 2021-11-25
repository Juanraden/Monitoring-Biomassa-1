package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.*
import com.kedaireka.monitoring_biomassa.data.domain.BiotaDomain
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.database.dao.KerambaDAO
import com.kedaireka.monitoring_biomassa.database.entity.Biota
import com.kedaireka.monitoring_biomassa.database.entity.Keramba
import com.kedaireka.monitoring_biomassa.repository.KerambaRepository
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class KerambaViewModel @Inject constructor(
    private val kerambaDAO: KerambaDAO,
    private val kerambaMapper: EntityMapper<Keramba, KerambaDomain>,
    private val biotaMapper: EntityMapper<Biota,BiotaDomain>,
    private val repository: KerambaRepository
): ViewModel() {
    private val _loadedKerambaId = MutableLiveData<Int>()
    val loadedKerambaId: LiveData<Int> = _loadedKerambaId

    private val _tanggalInstall = MutableLiveData<Long>()
    val tanggalInstall: LiveData<Long> = _tanggalInstall

    private val _querySearch = MutableLiveData<String>()
    val querySearch: LiveData<String> = _querySearch

    fun setKerambaId(id: Int){
        _loadedKerambaId.value = id
    }

    fun getAllKeramba(): LiveData<List<KerambaDomain>> = repository.kerambaList

    fun setQuerySearch(query: String){
        _querySearch.value = query
    }

    fun loadKerambaData(id: Int): LiveData<KerambaDomain>{
        return Transformations.map(kerambaDAO.getById(id).asLiveData()){
            kerambaMapper.mapFromEntity(it)
        }
    }

    fun onSelectDateTime(dateTime: Long) {
        _tanggalInstall.value = dateTime }

    fun isEntryValid(jenis: String, ukuran: String): Boolean {
        return !(jenis.isBlank() || ukuran.isBlank() || tanggalInstall.value == null)
    }

    fun insertKeramba(nama: String, ukuran: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                kerambaDAO.insertOne(
                    Keramba(
                        nama_keramba = nama,
                        ukuran = ukuran.toDouble(),
                        tanggal_install = _tanggalInstall.value!!
                    )
                )
            }
        }
    }

    fun updateKeramba(id: Int, nama: String, ukuran: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                kerambaDAO.updateOne(
                    Keramba(
                        keramba_id = id,
                        nama_keramba = nama,
                        ukuran = ukuran.toDouble(),
                        tanggal_install = _tanggalInstall.value!!
                    )
                )
            }
        }
    }

    fun loadKerambaAndBiota(): LiveData<Map<KerambaDomain, List<BiotaDomain>>>{
        return Transformations.map(kerambaDAO.getKerambaAndBiota().asLiveData()){listKerambaAndBiota->
            listKerambaAndBiota.map {
                kerambaMapper.mapFromEntity(it.keramba) to it.biotaList.map {biota-> biotaMapper.mapFromEntity(biota) }
            }.toMap()
        }
    }
//    init {
//        viewModelScope.launch {
//            repository.refreshKeramba()
//        }
//    }
}