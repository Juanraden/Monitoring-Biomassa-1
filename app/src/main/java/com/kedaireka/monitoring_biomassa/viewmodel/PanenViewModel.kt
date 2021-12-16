package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.*
import com.kedaireka.monitoring_biomassa.data.domain.PanenDomain
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.database.dao.PanenDAO
import com.kedaireka.monitoring_biomassa.database.relation.PanenAndBiota
import com.kedaireka.monitoring_biomassa.repository.PanenRepository
import com.kedaireka.monitoring_biomassa.util.convertStringToDateLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PanenViewModel @Inject constructor(
    private val panenDAO: PanenDAO,
    private val repository: PanenRepository
) : ViewModel() {
    private val _requestGetResult = MutableLiveData<NetworkResult>()
    val requestGetResult: LiveData<NetworkResult> = _requestGetResult

    private val _requestPostAddResult = MutableLiveData<NetworkResult>()
    val requestPostAddResult: LiveData<NetworkResult> = _requestPostAddResult

    fun doneToastException() {
        _requestGetResult.value = NetworkResult.Error("")
    }

    fun donePostAddRequest() {
        _requestPostAddResult.value = NetworkResult.Loading()
    }

    fun getlistPanen(kerambaId: Int): LiveData<List<PanenAndBiota>> =
        panenDAO.getAllPanenAndBiota(kerambaId).asLiveData()

    private val _inputKerambaId = MutableLiveData<Int>()
    val inputKerambaId: LiveData<Int> = _inputKerambaId

    private val _inputBiotaId = MutableLiveData<Int>()
    val inputBiotaId: LiveData<Int> = _inputBiotaId

    fun selectKeramba(id: Int) {
        _inputKerambaId.value = id
    }

    fun selectBiota(id: Int) {
        _inputBiotaId.value = id
    }

    fun fetchPanen(kerambaId: Int) {
        _requestGetResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.refreshPanen(kerambaId)

                _requestGetResult.value = NetworkResult.Loaded()
            } catch (e: Exception) {
                _requestGetResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun insertPanen(
        panjang: String,
        bobot: String,
        jumlahHidup: String,
        jumlahMati: String,
        tanggal: String
    ) {
        _requestPostAddResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.addPanen(
                    PanenDomain(
                        activity_id = 0,
                        keramba_id = _inputKerambaId.value!!,
                        biota_id = _inputBiotaId.value!!,
                        panjang = panjang.toDouble(),
                        bobot = bobot.toDouble(),
                        jumlah_hidup = jumlahHidup.toInt(),
                        jumlah_mati = jumlahMati.toInt(),
                        tanggal_panen = convertStringToDateLong(tanggal, "EEEE dd-MMM-yyyy")
                    )
                )
                _requestPostAddResult.value = NetworkResult.Loaded()
            } catch (e: Exception) {
                _requestPostAddResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun isEntryValid(
        panjang: String, bobot: String, jumlahHidup: String, jumlahMati: String, tanggal: String
    ): Boolean {
        return !(_inputBiotaId.value == null || _inputKerambaId.value == null || panjang.isBlank() || bobot.isBlank() || jumlahHidup.isBlank() || jumlahMati.isBlank() || tanggal.isBlank())
    }
}