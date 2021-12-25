package com.kedaireka.monitoring_biomassa.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.kedaireka.monitoring_biomassa.data.domain.FeedingDomain
import com.kedaireka.monitoring_biomassa.data.network.FeedingNetwork
import com.kedaireka.monitoring_biomassa.data.network.container.FeedingContainer
import com.kedaireka.monitoring_biomassa.database.dao.FeedingDAO
import com.kedaireka.monitoring_biomassa.database.entity.Feeding
import com.kedaireka.monitoring_biomassa.service.MonitoringService
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import com.kedaireka.monitoring_biomassa.util.convertStringToDateLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class FeedingRepository @Inject constructor(
    private val feedingDAO: FeedingDAO,
    private val monitoringService: MonitoringService,
    private val sharedPreferences: SharedPreferences,
    private val feedingMapper: EntityMapper<Feeding, FeedingDomain>,
    private val feedingNetworkMapper: EntityMapper<FeedingNetwork, FeedingDomain>
) {
    fun getAllFeeding(id: Int): LiveData<List<FeedingDomain>> =
        Transformations.map(feedingDAO.getAll(id).asLiveData()) { list ->
            list.map { feedingMapper.mapFromEntity(it) }
        }

    suspend fun refreshFeeding(kerambaId: Int) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        withContext(Dispatchers.IO) {
            val response: Response<FeedingContainer> =
                monitoringService.getFeedingListAsync(token, userId, kerambaId).await()

            if (response.code() == 200) {
                val listFeedingNetwork: List<FeedingNetwork> = response.body()!!.data

                val listFeeding = listFeedingNetwork.map { target ->
                    Feeding(
                        feeding_id = target.feeding_id.toInt(),
                        keramba_id = target.keramba_id.toInt(),
                        tanggal_feeding = convertStringToDateLong(target.tanggal_feeding, "yyyy-MM-dd")
                    )
                }.toList()

                if (feedingDAO.getFeedingCountFromKeramba(kerambaId) > listFeeding.size) {
                    feedingDAO.deleteFeedingFromKeramba(kerambaId)
                }

                feedingDAO.insertAll(listFeeding)
            } else {
                when {
                    response.code() == 500 -> {
                        throw Exception("Internal Server Error")
                    }
                    response.code() == 401 -> {
                        throw Exception("Unauthorized")
                    }
                    else -> {
                        throw Exception("HTTP Request Failed")
                    }
                }
            }
        }
    }

    suspend fun addFeeding(feeding: FeedingDomain) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val feedingNetwork: FeedingNetwork = feedingNetworkMapper.mapToEntity(feeding)

        val data = mutableMapOf<String, String>()

        data["keramba_id"] = feedingNetwork.keramba_id

        data["tanggal_feeding"] = feedingNetwork.tanggal_feeding

        data["user_id"] = userId.toString()

        val response: Response<FeedingContainer> =
            monitoringService.addFeedingAsync(token, data).await()

        if (response.code() != 201){
            when {
                response.code() == 500 -> {
                    throw Exception("Internal Server Error")
                }
                response.code() == 401 -> {
                    throw Exception("Unauthorized")
                }
                else -> {
                    throw Exception("HTTP Request Failed")
                }
            }
        }
    }

    suspend fun updateFeeding(feeding: FeedingDomain) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val feedingNetwork: FeedingNetwork = feedingNetworkMapper.mapToEntity(feeding)

        val data = mutableMapOf<String, String>()

        data["feeding_id"] = feedingNetwork.feeding_id

        data["keramba_id"] = feedingNetwork.keramba_id

        data["tanggal_tebar"] = feedingNetwork.tanggal_feeding

        data["user_id"] = userId.toString()

        val response: Response<FeedingContainer> =
            monitoringService.updateFeedingAsync(token, data).await()

        if (response.code() != 200) {
            when {
                response.code() == 500 -> {
                    throw Exception("Internal Server Error")
                }
                response.code() == 401 -> {
                    throw Exception("Unauthorized")
                }
                else -> {
                    throw Exception("HTTP Request Failed")
                }
            }
        }
    }

    suspend fun deleteFeeding(feedingId: Int) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val data = mutableMapOf<String, String>()

        data["feeding_id"] = feedingId.toString()

        data["user_id"] = userId.toString()

        val response: Response<FeedingContainer> =
            monitoringService.deleteFeedingAsync(token, data).await()

        if (response.code() != 200) {
            when {
                response.code() == 500 -> {
                    throw Exception("Internal Server Error")
                }
                response.code() == 401 -> {
                    throw Exception("Unauthorized")
                }
                else -> {
                    throw Exception("HTTP Request Failed")
                }
            }
        }
    }
}