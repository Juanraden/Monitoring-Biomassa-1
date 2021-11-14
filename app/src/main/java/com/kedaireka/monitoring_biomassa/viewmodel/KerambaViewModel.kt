package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.*
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.database.dao.KerambaDAO
import com.kedaireka.monitoring_biomassa.database.entity.Keramba
import com.kedaireka.monitoring_biomassa.util.KerambaMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class KerambaViewModel @Inject constructor(
    private val kerambaDAO: KerambaDAO,
    private val kerambaMapper: KerambaMapper,
): ViewModel() {
    fun getAllKeramba(): LiveData<List<KerambaDomain>> = Transformations.map(kerambaDAO.getAll().asLiveData()){
        kerambaMapper.mapFromList(it)
    }

    private val _loadedKerambaid = MutableLiveData<Int>()
    val loadedKerambaid: LiveData<Int> = _loadedKerambaid

    fun setKerambaId(id: Int){
        _loadedKerambaid.value = id
    }

    private val _tanggalInstall = MutableLiveData<Long>()
    val tanggalInstall: LiveData<Long> = _tanggalInstall

    private val _querySearch = MutableLiveData<String>()
    val querySearch: LiveData<String> = _querySearch

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
}