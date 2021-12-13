package com.kedaireka.monitoring_biomassa.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.kedaireka.monitoring_biomassa.data.domain.BiotaDomain
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
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

    private val _querySearch = MutableLiveData<String>()
    val querySearch: LiveData<String> = _querySearch

    private val _requestGetResult = MutableLiveData<NetworkResult>()
    val requestGetResult: LiveData<NetworkResult> = _requestGetResult

    private val _requestPostAddResult = MutableLiveData<NetworkResult>()
    val requestPostAddResult: LiveData<NetworkResult> = _requestPostAddResult

    fun setKerambaId(id: Int){
        _loadedKerambaId.value = id
    }

    fun getAllKeramba(): LiveData<List<KerambaDomain>> = repository.kerambaList

    fun setQuerySearch(query: String){
        _querySearch.value = query
    }

    fun doneToastException() {
        _requestGetResult.value = NetworkResult.Error("")
    }

    fun loadKerambaData(id: Int): LiveData<KerambaDomain>{
        return Transformations.map(kerambaDAO.getById(id).asLiveData()){
            kerambaMapper.mapFromEntity(it)
        }
    }

    fun isEntryValid(jenis: String, ukuran: String, tanggal: Long): Boolean {
        return !(jenis.isBlank() || ukuran.isBlank() || tanggal == 0L)
    }

    fun insertKeramba(nama: String, ukuran: String, tanggal: Long){
        viewModelScope.launch {
            _requestPostAddResult.value = NetworkResult.Loading()

            try {
                repository.addKeramba(
                    KerambaDomain(keramba_id = 0, nama_keramba = nama, ukuran = ukuran.toDouble(), tanggal_install = tanggal))

                _requestPostAddResult.value = NetworkResult.Loaded()
            }catch (e: Exception){
                _requestPostAddResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun updateKeramba(id: Int, nama: String, ukuran: String, tanggal: Long){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                kerambaDAO.updateOne(
                    Keramba(
                        keramba_id = id,
                        nama_keramba = nama,
                        ukuran = ukuran.toDouble(),
                        tanggal_install = tanggal
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

    fun fetchKeramba(){
        _requestGetResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.refreshKeramba()

                _requestGetResult.value = NetworkResult.Loaded()

            } catch (e: Exception){
                _requestGetResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    init {
        fetchKeramba()
        Log.i("KerambaViewModel", "fetch keramba called")
    }
}