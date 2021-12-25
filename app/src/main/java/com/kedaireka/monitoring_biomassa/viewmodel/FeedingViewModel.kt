package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.*
import com.kedaireka.monitoring_biomassa.data.domain.FeedingDomain
import com.kedaireka.monitoring_biomassa.data.network.enums.NetworkResult
import com.kedaireka.monitoring_biomassa.database.dao.FeedingDAO
import com.kedaireka.monitoring_biomassa.database.entity.Feeding
import com.kedaireka.monitoring_biomassa.repository.FeedingRepository
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FeedingViewModel @Inject constructor(
    private val feedingDao: FeedingDAO,
    private val feedingMapper: EntityMapper<Feeding, FeedingDomain>,
    private val repository: FeedingRepository
) : ViewModel() {

    private val _loadedFeedingId = MutableLiveData<Int>()
    val loadedFeedingId: LiveData<Int> = _loadedFeedingId

    private val _selectedKerambaId = MutableLiveData<Int>()
    val selectedKerambaId: LiveData<Int> = _selectedKerambaId

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

    fun loadFeedingData(id: Int): LiveData<FeedingDomain> {
        return Transformations.map(
            feedingDao.getById(id).asLiveData()
        ) { feedingMapper.mapFromEntity(it) }
    }

    fun setFeedingId(id: Int) {
        _loadedFeedingId.value = id
    }

    fun getAllFeeding(id: Int): LiveData<List<FeedingDomain>> = repository.getAllFeeding(id)

    fun selectKerambaId(id: Int) {
        _selectedKerambaId.value = id
    }

    fun isEntryValid(
        tanggal: Long
    ): Boolean {
        return !(_selectedKerambaId.value == null || tanggal == 0L)
    }

    fun insertFeeding(tanggal: Long) {
        viewModelScope.launch {
            _requestPostAddResult.value = NetworkResult.Loading()

            try {
                repository.addFeeding(
                    FeedingDomain(
                        feeding_id = 0,
                        keramba_id = _selectedKerambaId.value!!,
                        tanggal_feeding = tanggal
                    )
                )

                _requestPostAddResult.value = NetworkResult.Loaded()

            } catch (e: Exception) {
                _requestPostAddResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun updateFeeding(
        feeding_id: Int,
        tanggal: Long
    ) {
        _requestPutUpdateResult.value = NetworkResult.Loading()
        viewModelScope.launch {
            try {
                repository.updateFeeding(
                    FeedingDomain(
                        feeding_id = feeding_id,
                        keramba_id = _selectedKerambaId.value!!,
                        tanggal_feeding = tanggal
                    )
                )

                _requestPutUpdateResult.value = NetworkResult.Loaded()
            } catch (e: Exception) {
                _requestPutUpdateResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun updateLocalFeeding(
        feeding_id: Int,
        tanggal: Long
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                feedingDao.updateOne(
                    Feeding(
                        feeding_id = feeding_id,
                        keramba_id = _selectedKerambaId.value!!,
                        tanggal_feeding = tanggal
                    )
                )
            }
        }
    }

    fun deleteFeeding(feedingId: Int) {
        _requestDeleteResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.deleteFeeding(feedingId)

                _requestDeleteResult.value = NetworkResult.Loaded()
            } catch (e: Exception) {
                _requestPutUpdateResult.value = NetworkResult.Error(e.message.toString())
            }
        }

    }

    fun deleteLocalFeeding(feedingId: Int) {
        viewModelScope.launch { withContext(Dispatchers.IO) { feedingDao.deleteOne(feedingId) } }
    }

    fun fetchFeeding(kerambaId: Int) {
        _requestGetResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.refreshFeeding(kerambaId)

                _requestGetResult.value = NetworkResult.Loaded()
            } catch (e: Exception) {
                _requestGetResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }
}