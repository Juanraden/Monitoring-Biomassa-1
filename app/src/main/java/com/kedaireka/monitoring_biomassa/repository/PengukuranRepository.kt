package com.kedaireka.monitoring_biomassa.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.kedaireka.monitoring_biomassa.data.domain.PengukuranDomain
import com.kedaireka.monitoring_biomassa.data.network.PengukuranNetwork
import com.kedaireka.monitoring_biomassa.data.network.container.PengukuranContainer
import com.kedaireka.monitoring_biomassa.database.dao.PengukuranDAO
import com.kedaireka.monitoring_biomassa.database.entity.Pengukuran
import com.kedaireka.monitoring_biomassa.service.MonitoringService
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import com.kedaireka.monitoring_biomassa.util.convertStringToDateLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PengukuranRepository @Inject constructor(
    private val pengukuranDAO: PengukuranDAO,
    private val monitoringService: MonitoringService,
    private val sharedPreferences: SharedPreferences,
    private val pengukuranMapper: EntityMapper<Pengukuran, PengukuranDomain>,
    private val pengukuranNetworkMapper: EntityMapper<PengukuranNetwork, PengukuranDomain>
) {
    fun getAllBiotaData(biota_id: Int): LiveData<List<PengukuranDomain>> =
        Transformations.map(pengukuranDAO.getAll(biota_id).asLiveData()) { list ->
            list.map { pengukuranMapper.mapFromEntity(it) }
        }

    suspend fun deleteLocalPengukuran(pengukuranId: Int){
        withContext(Dispatchers.IO){ pengukuranDAO.deleteOne(pengukuranId) }
    }

    suspend fun refreshPengukuran(biotaId: Int) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        withContext(Dispatchers.IO) {
            val response: Response<PengukuranContainer> =
                monitoringService.getPengukuranListAsync(token, userId, biotaId).await()

            if (response.code() == 200) {
                val listPengukuranNetwork: List<PengukuranNetwork> = response.body()!!.data

                val listPengukuran = listPengukuranNetwork.map { target ->
                    Pengukuran(
                        pengukuran_id = target.pengukuran_id.toInt(),
                        panjang = target.panjang.toDouble(),
                        bobot = target.bobot.toDouble(),
                        tanggal_ukur = convertStringToDateLong(target.tanggal_ukur, "yyyy-MM-dd"),
                        biota_id = target.biota_id.toInt()
                    )
                }.toList()

                if (pengukuranDAO.getPengukuranCountFromBiota(biotaId) > listPengukuran.size) {
                    pengukuranDAO.deletePengukuranFromBiota(biotaId)
                }

                pengukuranDAO.insertAll(listPengukuran)
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

    suspend fun addPengukuran(pengukuran: PengukuranDomain) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val pengukuranNetwork: PengukuranNetwork = pengukuranNetworkMapper.mapToEntity(pengukuran)

        val data = mutableMapOf<String, String>()

        data["panjang"] = pengukuranNetwork.panjang

        data["bobot"] = pengukuranNetwork.bobot

        data["tanggal_ukur"] = pengukuranNetwork.tanggal_ukur

        data["biota_id"] = pengukuranNetwork.biota_id

        data["user_id"] = userId.toString()

        val response: Response<PengukuranContainer> =
            monitoringService.addPengukuranAsync(token, data).await()

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
        }
    }

    suspend fun deletePengukuran(pengukuranId: Int) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val data = mutableMapOf<String, String>()

        data["user_id"] = userId.toString()

        data["pengukuran_id"] = pengukuranId.toString()

        val response: Response<PengukuranContainer> =
            monitoringService.deletePengukuranAsync(token, data).await()

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