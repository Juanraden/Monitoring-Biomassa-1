package com.kedaireka.monitoring_biomassa.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.kedaireka.monitoring_biomassa.data.domain.FeedingDetailDomain
import com.kedaireka.monitoring_biomassa.data.domain.FeedingDomain
import com.kedaireka.monitoring_biomassa.data.network.FeedingDetailNetwork
import com.kedaireka.monitoring_biomassa.data.network.FeedingNetwork
import com.kedaireka.monitoring_biomassa.data.network.container.FeedingContainer
import com.kedaireka.monitoring_biomassa.data.network.container.FeedingDetailContainer
import com.kedaireka.monitoring_biomassa.database.dao.FeedingDAO
import com.kedaireka.monitoring_biomassa.database.dao.FeedingDetailDAO
import com.kedaireka.monitoring_biomassa.database.entity.Feeding
import com.kedaireka.monitoring_biomassa.database.entity.FeedingDetail
import com.kedaireka.monitoring_biomassa.database.relation.FeedingDetailAndPakan
import com.kedaireka.monitoring_biomassa.service.MonitoringService
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import com.kedaireka.monitoring_biomassa.util.convertStringToDateLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedingRepository @Inject constructor(
    private val feedingDAO: FeedingDAO,
    private val feedingDetailDAO: FeedingDetailDAO,
    private val monitoringService: MonitoringService,
    private val sharedPreferences: SharedPreferences,
    private val feedingMapper: EntityMapper<Feeding, FeedingDomain>,
    private val feedingNetworkMapper: EntityMapper<FeedingNetwork, FeedingDomain>,
    private val feedingDetailNetworkMapper: EntityMapper<FeedingDetailNetwork, FeedingDetailDomain>
) {
    fun getAllFeeding(id: Int): LiveData<List<FeedingDomain>> =
        Transformations.map(feedingDAO.getAll(id).asLiveData()) { list ->
            list.map { feedingMapper.mapFromEntity(it) }
        }

    fun getAllFeedingDetailAndPakan(feedingId: Int): LiveData<List<FeedingDetailAndPakan>> =
        feedingDetailDAO.getAllDetailAndPakan(feedingId).asLiveData()

    fun loadFeedingDataByFeedingId(id: Int): LiveData<FeedingDomain> {
        return Transformations.map(
            feedingDAO.getByFeedingId(id).asLiveData()
        ) { feedingMapper.mapFromEntity(it) }
    }

    suspend fun updateLocalFeeding(feedingId: Int, kerambaId: Int, tanggal: Long) {
        withContext(Dispatchers.IO) {
            feedingDAO.updateOne(
                Feeding(
                    feeding_id = feedingId,
                    keramba_id = kerambaId,
                    tanggal_feeding = tanggal
                )
            )
        }
    }

    suspend fun deleteLocalFeeding(feedingId: Int) {
        withContext(Dispatchers.IO) { feedingDAO.deleteOne(feedingId) }
    }

    suspend fun deleteLocalFeedingDetail(detailId: Int){
        withContext(Dispatchers.IO) { feedingDetailDAO.deleteOne(detailId)}
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
                        tanggal_feeding = convertStringToDateLong(
                            target.tanggal_feeding,
                            "yyyy-MM-dd"
                        )
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


    suspend fun refreshFeedingDetail(activityId: Int) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        withContext(Dispatchers.IO) {
            val response: Response<FeedingDetailContainer> =
                monitoringService.getFeedingDetailListAsync(token, userId, activityId).await()

            if (response.code() == 200) {
                val listFeedingDetailNetwork: List<FeedingDetailNetwork> = response.body()!!.data

                val listFeeding = listFeedingDetailNetwork.map { target ->
                    FeedingDetail(
                        detail_id = target.detail_id.toInt(),
                        feeding_id = target.feeding_id.toInt(),
                        pakan_id = target.pakan_id.toInt(),
                        ukuran_tebar = target.ukuran_tebar.toDouble(),
                        waktu_feeding = convertStringToDateLong(target.jam_feeding, "H:m:s")
                    )
                }.toList()

                if (feedingDetailDAO.getDetailCountFromFeeding(activityId) > listFeeding.size) {
                    feedingDetailDAO.deleteDetailFromFeeding(activityId)
                }

                feedingDetailDAO.insertAll(listFeeding)
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

    suspend fun addFeedingDetail(feedingDetail: FeedingDetailDomain): FeedingDetailContainer {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val feedingDetailNetwork: FeedingDetailNetwork =
            feedingDetailNetworkMapper.mapToEntity(feedingDetail)

        val data = mutableMapOf<String, String>()

        data["activity_id"] = feedingDetailNetwork.feeding_id

        data["ukuran_tebar"] = feedingDetailNetwork.ukuran_tebar

        data["jam_feeding"] = feedingDetailNetwork.jam_feeding

        data["pakan_id"] = feedingDetailNetwork.pakan_id

        data["user_id"] = userId.toString()

        val response: Response<FeedingDetailContainer> =
            monitoringService.addFeedingDetailAsync(token, data).await()

        if (response.code() != 201) {
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
        } else {
            return response.body()!!
        }
    }

    suspend fun deleteFeedingDetail(detailId: Int): FeedingDetailContainer {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val data = mutableMapOf<String, String>()

        data["detail_id"] = detailId.toString()

        data["user_id"] = userId.toString()

        val response: Response<FeedingDetailContainer> =
            monitoringService.deleteFeedingDetailAsync(token, data).await()

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
        } else {
            return response.body()!!
        }
    }

    suspend fun addFeeding(feeding: FeedingDomain): FeedingContainer {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val feedingNetwork: FeedingNetwork = feedingNetworkMapper.mapToEntity(feeding)

        val data = mutableMapOf<String, String>()

        data["keramba_id"] = feedingNetwork.keramba_id

        data["tanggal_feeding"] = feedingNetwork.tanggal_feeding

        data["user_id"] = userId.toString()

        val response: Response<FeedingContainer> =
            monitoringService.addFeedingAsync(token, data).await()

        if (response.code() != 201) {
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
        } else {
            return response.body()!!
        }
    }

    suspend fun updateFeeding(feeding: FeedingDomain): FeedingContainer {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val feedingNetwork: FeedingNetwork = feedingNetworkMapper.mapToEntity(feeding)

        val data = mutableMapOf<String, String>()

        data["activity_id"] = feedingNetwork.feeding_id

        data["keramba_id"] = feedingNetwork.keramba_id

        data["tanggal_feeding"] = feedingNetwork.tanggal_feeding

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
        } else {
            return response.body()!!
        }
    }

    suspend fun deleteFeeding(feedingId: Int): FeedingContainer {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val data = mutableMapOf<String, String>()

        data["activity_id"] = feedingId.toString()

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
        } else {
            return response.body()!!
        }
    }
}