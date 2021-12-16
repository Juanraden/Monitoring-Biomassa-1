package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.*
import com.kedaireka.monitoring_biomassa.data.domain.BiotaDomain
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.database.dao.BiotaDAO
import com.kedaireka.monitoring_biomassa.database.entity.Biota
import com.kedaireka.monitoring_biomassa.repository.BiotaRepository
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BiotaViewModel @Inject constructor(
    private val biotaDao: BiotaDAO,
    private val biotaMapper: EntityMapper<Biota, BiotaDomain>,
    private val repository: BiotaRepository
) : ViewModel() {

    private val _loadedBiotaId = MutableLiveData<Int>()
    val loadedBiotaId: LiveData<Int> = _loadedBiotaId

    private val _selectedKerambaId = MutableLiveData<Int>()
    val selectedKerambaId: LiveData<Int> = _selectedKerambaId

    private val _requestGetResult = MutableLiveData<NetworkResult>()
    val requestGetResult: LiveData<NetworkResult> = _requestGetResult

    private val _requestGetHistoryResult = MutableLiveData<NetworkResult>()
    val requestGetHistoryResult: LiveData<NetworkResult> = _requestGetHistoryResult

    private val _requestPostAddResult = MutableLiveData<NetworkResult>()
    val requestPostAddResult: LiveData<NetworkResult> = _requestPostAddResult

    private val _requestPutUpdateResult = MutableLiveData<NetworkResult>()
    val requestPutUpdateResult: LiveData<NetworkResult> = _requestPutUpdateResult

    private val _requestDeleteResult = MutableLiveData<NetworkResult>()
    val requestDeleteResult: LiveData<NetworkResult> = _requestDeleteResult

    fun doneToastException() {
        _requestGetResult.value = NetworkResult.Error("")
    }

    fun donePostAddRequest() {
        _requestPostAddResult.value = NetworkResult.Loading()
    }

    fun donePutUpdateRequest() {
        _requestPutUpdateResult.value = NetworkResult.Loading()
    }

    fun doneDeleteRequest() {
        _requestDeleteResult.value = NetworkResult.Loading()
    }

    fun loadBiotaData(id: Int): LiveData<BiotaDomain> {
        return Transformations.map(
            biotaDao.getById(id).asLiveData()
        ) { biotaMapper.mapFromEntity(it) }
    }


    fun setBiotaId(id: Int) {
        _loadedBiotaId.value = id
    }

    fun getAllBiota(id: Int): LiveData<List<BiotaDomain>> = repository.getAllBiota(id)

    fun getAllBiotaHistory(id: Int): LiveData<List<BiotaDomain>> =
        Transformations.map(biotaDao.getAllHistory(id).asLiveData()) { list ->
            list.map { biotaMapper.mapFromEntity(it) }
        }

    fun selectkerambaId(id: Int) {
        _selectedKerambaId.value = id
    }

    fun isEntryValid(
        jenis: String,
        bobot: String,
        panjang: String,
        jumlah: String,
        tanggal: Long
    ): Boolean {
        return !(jenis.isBlank() || bobot.isBlank() || panjang.isBlank() || jumlah.isBlank() || tanggal == 0L || _selectedKerambaId.value == null)
    }

    fun insertBiota(jenis: String, bobot: String, panjang: String, jumlah: String, tanggal: Long) {
        viewModelScope.launch {
            _requestPostAddResult.value = NetworkResult.Loading()

            try {
                repository.addBiota(
                    BiotaDomain(
                        biota_id = 0,
                        keramba_id = _selectedKerambaId.value!!,
                        jenis_biota = jenis,
                        bobot = bobot.toDouble(),
                        panjang = panjang.toDouble(),
                        jumlah_bibit = jumlah.toInt(),
                        tanggal_tebar = tanggal,
                        tanggal_panen = 0L
                    )
                )

                _requestPostAddResult.value = NetworkResult.Loaded()

            } catch (e: Exception) {
                _requestPostAddResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun updateBiota(
        biota_id: Int,
        jenis: String,
        bobot: String,
        panjang: String,
        jumlah: String,
        tanggal: Long
    ) {
        _requestPutUpdateResult.value = NetworkResult.Loading()
        viewModelScope.launch {
            try {
                repository.updateBiota(
                    BiotaDomain(
                        biota_id = biota_id,
                        jenis_biota = jenis,
                        bobot = bobot.toDouble(),
                        panjang = panjang.toDouble(),
                        jumlah_bibit = jumlah.toInt(),
                        tanggal_tebar = tanggal,
                        keramba_id = _selectedKerambaId.value!!,
                        tanggal_panen = 0L
                    )
                )

                _requestPutUpdateResult.value = NetworkResult.Loaded()
            } catch (e: Exception) {
                _requestPutUpdateResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun updateLocalBiota(
        biota_id: Int,
        jenis: String,
        bobot: String,
        panjang: String,
        jumlah: String,
        tanggal: Long
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                biotaDao.updateOne(
                    Biota(
                        biota_id = biota_id,
                        jenis_biota = jenis,
                        bobot = bobot.toDouble(),
                        panjang = panjang.toDouble(),
                        jumlah_bibit = jumlah.toInt(),
                        tanggal_tebar = tanggal,
                        keramba_id = _selectedKerambaId.value!!,
                        tanggal_panen = 0L
                    )
                )
            }
        }
    }

    fun deleteBiota(biotaId: Int) {
        _requestDeleteResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.deleteBiota(biotaId)

                _requestDeleteResult.value = NetworkResult.Loaded()
            } catch (e: Exception) {
                _requestPutUpdateResult.value = NetworkResult.Error(e.message.toString())
            }
        }

    }

    fun deleteLocalBiota(biotaId: Int) {
        viewModelScope.launch { withContext(Dispatchers.IO) { biotaDao.deleteOne(biotaId) } }
    }

    fun fetchBiota(kerambaId: Int) {
        _requestGetResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.refreshBiota(kerambaId)

                _requestGetResult.value = NetworkResult.Loaded()
            } catch (e: Exception) {
                _requestGetResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun fetchBiotaHistory(kerambaId: Int) {
        _requestGetHistoryResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.refreshBiotaHistory(kerambaId)

                _requestGetHistoryResult.value = NetworkResult.Loaded()
            } catch (e: Exception) {
                _requestGetHistoryResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }
}