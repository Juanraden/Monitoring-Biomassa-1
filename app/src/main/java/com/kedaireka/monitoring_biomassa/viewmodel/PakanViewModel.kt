package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.*
import com.kedaireka.monitoring_biomassa.data.domain.PakanDomain
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.repository.PakanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PakanViewModel @Inject constructor(
    private val repository: PakanRepository
) : ViewModel() {
    private val _init = MutableLiveData(true)
    val init: LiveData<Boolean> = _init

    private val _requestGetResult = MutableLiveData<NetworkResult>()
    val requestGetResult: LiveData<NetworkResult> = _requestGetResult

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

    fun loadPakanData(pakanId: Int): LiveData<PakanDomain> = repository.loadPakanData(pakanId)

    fun getAll(): LiveData<List<PakanDomain>> = repository.pakanList

    fun insertPakan(jenis_pakan: String, deskripsi: String = "") {
        _requestPostAddResult.value = NetworkResult.Loading()


        viewModelScope.launch {
            try {
                repository.addPakan(
                    PakanDomain(
                        pakan_id = 0,
                        jenis_pakan = jenis_pakan,
                        deskripsi = deskripsi
                    )
                )

                _requestPostAddResult.value = NetworkResult.Loaded()
            } catch (e: Exception) {
                _requestPostAddResult.value = NetworkResult.Error(e.message.toString())
            }

        }
    }

    fun updatePakan(pakanId: Int, jenis_pakan: String, deskripsi: String = "") {
        _requestPutUpdateResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.updatePakan(
                    PakanDomain(
                        pakan_id = pakanId,
                        jenis_pakan = jenis_pakan,
                        deskripsi = deskripsi
                    )
                )

                _requestPutUpdateResult.value = NetworkResult.Loaded()
            } catch (e: Exception) {
                _requestPutUpdateResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun deletePakan(pakanId: Int) {
        _requestDeleteResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.deletePakan(pakanId)

                _requestDeleteResult.value = NetworkResult.Loaded()
            } catch (e: Exception) {
                _requestDeleteResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun updateLocalPakan(pakanId: Int, jenis_pakan: String, deskripsi: String = "") {
        viewModelScope.launch { repository.updateLocalPakan(pakanId, jenis_pakan, deskripsi)}
    }

    fun deleteLocalPakan(pakanId: Int) {
        viewModelScope.launch { repository.deleteLocalPakan(pakanId) }
    }

    fun isEntryValid(jenis_pakan: String): Boolean = jenis_pakan.isNotBlank()

    fun fetchPakan() {
        _requestGetResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.refreshPakan()

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
        fetchPakan()

        doneInit()
    }

    fun restartInit() {
        _init.value = false
    }


    init {
        startInit()
    }
}