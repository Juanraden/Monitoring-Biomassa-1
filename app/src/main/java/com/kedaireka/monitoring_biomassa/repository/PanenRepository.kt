package com.kedaireka.monitoring_biomassa.repository

import android.content.SharedPreferences
import com.kedaireka.monitoring_biomassa.data.domain.PanenDomain
import com.kedaireka.monitoring_biomassa.data.network.PanenNetwork
import com.kedaireka.monitoring_biomassa.data.network.container.PanenContainer
import com.kedaireka.monitoring_biomassa.database.dao.PanenDAO
import com.kedaireka.monitoring_biomassa.database.entity.Panen
import com.kedaireka.monitoring_biomassa.service.MonitoringService
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import com.kedaireka.monitoring_biomassa.util.convertStringToDateLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class PanenRepository @Inject constructor(
    private val panenDAO: PanenDAO,
    private val monitoringService: MonitoringService,
    private val sharedPreferences: SharedPreferences,
    private val panenNetworkMapper: EntityMapper<PanenNetwork, PanenDomain>
) {
    suspend fun refreshPanen(kerambaId: Int) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        withContext(Dispatchers.IO) {
            val response: Response<PanenContainer> =
                monitoringService.getPanenListAsync(token, userId, kerambaId).await()

            if (response.code() == 200) {
                val listPanenNetwork: List<PanenNetwork> = response.body()!!.data

                val listPanen = listPanenNetwork.map { target ->
                    Panen(
                        activity_id = target.activity_id.toInt(),
                        tanggal_panen = convertStringToDateLong(target.tanggal_panen, "yyyy-MM-dd"),
                        panjang = target.panjang.toDouble(),
                        bobot = target.bobot.toDouble(),
                        jumlah_hidup = target.jumlah_hidup.toInt(),
                        jumlah_mati = target.jumlah_mati.toInt(),
                        biota_id = target.biota_id.toInt(),
                        keramba_id = target.keramba_id.toInt()
                    )
                }.toList()

                if (panenDAO.getPanenCountFromKeramba(kerambaId) > listPanen.size) {
                    panenDAO.deletePanenFromKeramba(kerambaId)
                }

                panenDAO.insertAll(listPanen)
            } else {
                if (response.body() != null) {
                    throw Exception(response.body()!!.message)
                }
            }
        }
    }

    suspend fun addPanen(panen: PanenDomain){
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val panenNetwork: PanenNetwork = panenNetworkMapper.mapToEntity(panen)

        val data = mutableMapOf<String, String>()

        data["tanggal_panen"] = panenNetwork.tanggal_panen

        data["panjang"] = panenNetwork.panjang

        data["bobot"] = panenNetwork.bobot

        data["jumlah_hidup"] = panenNetwork.jumlah_hidup

        data["jumlah_mati"] = panenNetwork.jumlah_mati

        data["biota_id"] = panenNetwork.biota_id

        data["keramba_id"] = panenNetwork.keramba_id

        data["user_id"] = userId.toString()

        val response: Response<PanenContainer> =
            monitoringService.addPanenAsync(token, data).await()

        if (response.code() != 201){
            throw Exception(response.body()!!.message)
        }
    }
}