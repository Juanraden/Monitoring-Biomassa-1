package com.kedaireka.monitoring_biomassa.repository

import android.content.SharedPreferences
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

class PengukuranRepository @Inject constructor(
    private val pengukuranDAO: PengukuranDAO,
    private val monitoringService: MonitoringService,
    private val sharedPreferences: SharedPreferences,
    private val pengukuranNetworkMapper: EntityMapper<PengukuranNetwork, PengukuranDomain>
) {
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
                if (response.body() != null) {
                    throw  Exception(response.body()!!.message)
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
            throw Exception(response.body()!!.message)
        }
    }

    suspend fun deletePengukuran(pengukuranId: Int) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val response: Response<PengukuranContainer> =
            monitoringService.deletePengukuranAsync(token, userId, pengukuranId).await()

        if (response.code() != 200) {
            throw Exception(response.body()!!.message)
        }
    }
}