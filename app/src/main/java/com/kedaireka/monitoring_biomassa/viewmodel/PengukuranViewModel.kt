package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.*
import com.kedaireka.monitoring_biomassa.data.domain.PengukuranDomain
import com.kedaireka.monitoring_biomassa.data.network.container.PengukuranContainer
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.repository.PengukuranRepository
import com.kedaireka.monitoring_biomassa.util.convertStringToDateLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PengukuranViewModel @Inject constructor(
    private val repository: PengukuranRepository
) : ViewModel() {
    private val _requestGetResult = MutableLiveData<NetworkResult>()
    val requestGetResult: LiveData<NetworkResult> = _requestGetResult

    private val _requestPostAddResult = MutableLiveData<NetworkResult>()
    val requestPostAddResult: LiveData<NetworkResult> = _requestPostAddResult

    private val _requestDeleteResult = MutableLiveData<NetworkResult>()
    val requestDeleteResult: LiveData<NetworkResult> = _requestDeleteResult

    fun doneToastException() {
        _requestGetResult.value = NetworkResult.Error("")
    }

    fun donePostAddRequest() {
        _requestPostAddResult.value = NetworkResult.Initial()
    }

    fun doneDeleteRequest() {
        _requestDeleteResult.value = NetworkResult.Initial()
    }

    private val _selectedBiotaId = MutableLiveData<Int>()

    fun getAllBiotaData(biota_id: Int): LiveData<List<PengukuranDomain>> = repository.getAllBiotaData(biota_id)

    fun selectBiotaId(biota_id: Int) {
        _selectedBiotaId.value = biota_id
    }

    fun isEntryValid(panjang: String, bobot: String, tanggal: String): Boolean {
        return !(panjang.isBlank() || bobot.isBlank() || _selectedBiotaId.value == null || tanggal.isBlank())
    }

    fun insertPengukuran(panjang: String, bobot: String, tanggal: String) {
        _requestPostAddResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                val container: PengukuranContainer = repository.addPengukuran(
                    PengukuranDomain(
                        pengukuran_id = 0,
                        panjang = panjang.toDouble(),
                        bobot = bobot.toDouble(),
                        tanggal_ukur = convertStringToDateLong(tanggal,"EEEE dd-MMM-yyyy"),
                        biota_id = _selectedBiotaId.value!!
                    )
                )

                _requestPostAddResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestPostAddResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun deletePengukuran(pengukuranId: Int) {
        _requestDeleteResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                val container: PengukuranContainer = repository.deletePengukuran(pengukuranId)

                _requestDeleteResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestDeleteResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun fetchPengukuran(biotaId: Int) {
        _requestGetResult.value = NetworkResult.Loading()
        viewModelScope.launch {
            try {
                repository.refreshPengukuran(biotaId)

                _requestGetResult.value = NetworkResult.Loaded()
            } catch (e: Exception) {
                _requestGetResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun deleteLocalPengukuran(pengukuranId: Int){
        viewModelScope.launch { repository.deleteLocalPengukuran(pengukuranId) }
    }
}