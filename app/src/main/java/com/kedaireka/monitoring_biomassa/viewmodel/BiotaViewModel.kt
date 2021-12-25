package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kedaireka.monitoring_biomassa.data.domain.BiotaDomain
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.repository.BiotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiotaViewModel @Inject constructor(
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

    fun loadBiotaData(id: Int): LiveData<BiotaDomain> = repository.loadBiotaData(id)


    fun setBiotaId(id: Int) {
        _loadedBiotaId.value = id
    }

    fun getAllBiota(id: Int): LiveData<List<BiotaDomain>> = repository.getAllBiota(id)

    fun getAllBiotaHistory(id: Int): LiveData<List<BiotaDomain>> = repository.getAllBiotaHistory(id)

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
        biotaId: Int,
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
                        biota_id = biotaId,
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
        biotaId: Int,
        jenis: String,
        bobot: String,
        panjang: String,
        jumlah: String,
        tanggal: Long
    ) {
        viewModelScope.launch {
            repository.updateLocalBiota(
                biotaId,
                jenis,
                bobot,
                panjang,
                jumlah,
                selectedKerambaId.value!!,
                tanggal
            )
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
        viewModelScope.launch { repository.deleteLocalBiota(biotaId) }
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