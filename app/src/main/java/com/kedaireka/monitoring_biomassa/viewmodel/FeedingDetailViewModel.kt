package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kedaireka.monitoring_biomassa.data.domain.FeedingDomain
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.database.relation.FeedingDetailAndPakan
import com.kedaireka.monitoring_biomassa.repository.FeedingRepository
import com.kedaireka.monitoring_biomassa.repository.KerambaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedingDetailViewModel @Inject constructor(
    private val kerambaRepository: KerambaRepository,
    private val feedingRepository: FeedingRepository
): ViewModel() {
    fun loadKerambaData(kerambaId: Int): LiveData<KerambaDomain> = kerambaRepository.getKerambaById(kerambaId)

    fun loadFeedingData(feedingId: Int): LiveData<FeedingDomain> = feedingRepository.loadFeedingData(feedingId)

    fun getAllFeedingDetailAndPakan(feedingId: Int): LiveData<List<FeedingDetailAndPakan>> = feedingRepository.getAllFeedingDetailAndPakan(feedingId)
}