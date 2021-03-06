package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.*
import com.kedaireka.monitoring_biomassa.data.domain.BiotaDomain
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.data.network.container.KerambaContainer
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.database.entity.Biota
import com.kedaireka.monitoring_biomassa.database.entity.Keramba
import com.kedaireka.monitoring_biomassa.repository.KerambaRepository
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KerambaViewModel @Inject constructor(
    private val kerambaMapper: EntityMapper<Keramba, KerambaDomain>,
    private val biotaMapper: EntityMapper<Biota, BiotaDomain>,
    private val repository: KerambaRepository
) : ViewModel() {
    private val _init = MutableLiveData(true)
    val init: LiveData<Boolean> = _init

    private val _loadedKerambaId = MutableLiveData<Int>()
    val loadedKerambaId: LiveData<Int> = _loadedKerambaId

    private val _querySearch = MutableLiveData<String>()
    val querySearch: LiveData<String> = _querySearch

    private val _requestGetResult = MutableLiveData<NetworkResult>()
    val requestGetResult: LiveData<NetworkResult> = _requestGetResult

    private val _requestPostAddResult = MutableLiveData<NetworkResult>()
    val requestPostAddResult: LiveData<NetworkResult> = _requestPostAddResult

    private val _requestPutUpdateResult = MutableLiveData<NetworkResult>()
    val requestPutUpdateResult: LiveData<NetworkResult> = _requestPutUpdateResult

    private val _requestDeleteResult = MutableLiveData<NetworkResult>()
    val requestDeleteResult: LiveData<NetworkResult> = _requestDeleteResult

    fun setKerambaId(id: Int) {
        _loadedKerambaId.value = id
    }

    fun getAllKeramba(): LiveData<List<KerambaDomain>> = repository.kerambaList

    fun setQuerySearch(query: String) {
        _querySearch.value = query
    }

    fun doneToastException() {
        _requestGetResult.value = NetworkResult.Error("")
    }

    fun donePostAddRequest() {
        _requestPostAddResult.value = NetworkResult.Initial()
    }

    fun donePutUpdateRequest() {
        _requestPutUpdateResult.value = NetworkResult.Initial()
    }

    fun doneDeleteRequest() {
        _requestDeleteResult.value = NetworkResult.Initial()
    }

    fun loadKerambaData(id: Int): LiveData<KerambaDomain> {
        return repository.getKerambaById(id)
    }

    fun isEntryValid(jenis: String, ukuran: String, tanggal: Long): Boolean {
        return !(jenis.isBlank() || ukuran.isBlank() || tanggal == 0L)
    }

    fun insertKeramba(nama: String, ukuran: String, tanggal: Long) {
        _requestPostAddResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                val container: KerambaContainer = repository.addKeramba(
                    KerambaDomain(
                        keramba_id = 0,
                        nama_keramba = nama,
                        ukuran = ukuran.toDouble(),
                        tanggal_install = tanggal
                    )
                )

                _requestPostAddResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestPostAddResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun deleteKeramba(kerambaId: Int) {
        _requestDeleteResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                val container: KerambaContainer = repository.deleteKeramba(kerambaId)

                _requestDeleteResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestDeleteResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun updateKeramba(id: Int, nama: String, ukuran: String, tanggal: Long) {
        _requestPutUpdateResult.value = NetworkResult.Loading()

        viewModelScope.launch {

            try {
                val container: KerambaContainer = repository.updateKeramba(
                    KerambaDomain(
                        keramba_id = id,
                        nama_keramba = nama,
                        ukuran = ukuran.toDouble(),
                        tanggal_install = tanggal
                    )
                )

                _requestPutUpdateResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestPutUpdateResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun updateLocalKeramba(id: Int, nama: String, ukuran: String, tanggal: Long) {
        viewModelScope.launch { repository.updateLocalKeramba(id, nama, ukuran, tanggal) }
    }

    fun deleteLocalKeramba(kerambaId: Int) {
        viewModelScope.launch { repository.deleteLocalKeramba(kerambaId) }
    }

    fun loadKerambaAndBiota(): LiveData<Map<KerambaDomain, List<BiotaDomain>>> {
        return Transformations.map(
            repository.kerambaAndBiotaList
        ) { listKerambaAndBiota ->
            listKerambaAndBiota.map {
                kerambaMapper.mapFromEntity(it.keramba) to it.biotaList.map { biota ->
                    biotaMapper.mapFromEntity(
                        biota
                    )
                }.filter { biotaDomain ->
                    biotaDomain.tanggal_panen == 0L
                }
            }.toMap()
        }
    }

    fun fetchKeramba() {
        _requestGetResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.refreshKeramba()

                _requestGetResult.value = NetworkResult.Loaded()

            } catch (e: Exception) {
                _requestGetResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    private fun doneInit() {
        _init.value = true
    }

    fun startInit() {
        fetchKeramba()

        doneInit()
    }

    fun restartInit() {
        _init.value = false
    }

    init {
        startInit()
    }
}