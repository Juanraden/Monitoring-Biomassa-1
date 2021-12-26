package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kedaireka.monitoring_biomassa.data.domain.FeedingDetailDomain
import com.kedaireka.monitoring_biomassa.data.domain.FeedingDomain
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.data.network.container.FeedingDetailContainer
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.database.relation.FeedingDetailAndPakan
import com.kedaireka.monitoring_biomassa.repository.FeedingRepository
import com.kedaireka.monitoring_biomassa.repository.KerambaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedingDetailViewModel @Inject constructor(
    private val kerambaRepository: KerambaRepository,
    private val feedingRepository: FeedingRepository
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

    private val _inputPakanId = MutableLiveData<Int>()

    fun selectPakanId(pakanId: Int) {
        _inputPakanId.value = pakanId
    }

    fun isEntryValid(ukuran: String, tanggal: String): Boolean {
        return !(ukuran.isBlank() || tanggal.isBlank())
    }

    fun fetchFeedingDetail(feedingId: Int) {
        _requestGetResult.value = NetworkResult.Loading()
        viewModelScope.launch {
            try {
                feedingRepository.refreshFeedingDetail(feedingId)

                _requestGetResult.value = NetworkResult.Loaded()
            }catch (e: Exception){
                _requestGetResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun loadKerambaData(kerambaId: Int): LiveData<KerambaDomain> =
        kerambaRepository.getKerambaById(kerambaId)

    fun getAllFeedingDetailAndPakan(feedingId: Int): LiveData<List<FeedingDetailAndPakan>> =
        feedingRepository.getAllFeedingDetailAndPakan(feedingId)

    fun loadFeedingData(feedingId: Int): LiveData<FeedingDomain> =
        feedingRepository.loadFeedingDataByFeedingId(feedingId)

    fun insertFeedingDetail(feedingId: Int, ukuran: String, tanggal: Long) {
        _requestPostAddResult.value = NetworkResult.Loading()

        val feedingDetailDomain = FeedingDetailDomain(
            detail_id = 0,
            pakan_id = _inputPakanId.value!!,
            ukuran_tebar = ukuran.toDouble(),
            waktu_feeding = tanggal,
            feeding_id = feedingId
            )

        viewModelScope.launch {
            try {
                val container: FeedingDetailContainer =
                    feedingRepository.addFeedingDetail(feedingDetailDomain)

                _requestPostAddResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {

                _requestPostAddResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun deleteFeedingDetail(detailId: Int) {
        _requestDeleteResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                val container: FeedingDetailContainer =
                    feedingRepository.deleteFeedingDetail(detailId)

                _requestDeleteResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestDeleteResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun deleteLocalFeedingDetail(detailId: Int) {
        viewModelScope.launch { feedingRepository.deleteLocalFeedingDetail(detailId) }
    }
}