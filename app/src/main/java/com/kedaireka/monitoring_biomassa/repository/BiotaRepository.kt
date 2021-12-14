package com.kedaireka.monitoring_biomassa.repository

import android.content.SharedPreferences
import com.kedaireka.monitoring_biomassa.data.domain.BiotaDomain
import com.kedaireka.monitoring_biomassa.data.network.BiotaNetwork
import com.kedaireka.monitoring_biomassa.data.network.container.BiotaContainer
import com.kedaireka.monitoring_biomassa.database.dao.BiotaDAO
import com.kedaireka.monitoring_biomassa.database.entity.Biota
import com.kedaireka.monitoring_biomassa.service.MonitoringService
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import com.kedaireka.monitoring_biomassa.util.convertStringToDateLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class BiotaRepository @Inject constructor(
    private val biotaDAO: BiotaDAO,
    private val monitoringService: MonitoringService,
    private val sharedPreferences: SharedPreferences,
    private val biotaNetworkMapper: EntityMapper<BiotaNetwork, BiotaDomain>
) {
    suspend fun refreshBiota(kerambaId: Int) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        withContext(Dispatchers.IO) {
            val response: Response<BiotaContainer> =
                monitoringService.getBiotaListAsync(token, userId, kerambaId).await()

            if (response.code() == 200) {
                val listBiotaNetwork: List<BiotaNetwork> = response.body()!!.data

                val listBiota = listBiotaNetwork.map { target ->
                    Biota(
                        biota_id = target.biota_id.toInt(),
                        jenis_biota = target.jenis_biota,
                        bobot = target.bobot.toDouble(),
                        panjang = target.panjang.toDouble(),
                        jumlah_bibit = target.jumlah_bibit.toInt(),
                        tanggal_tebar = convertStringToDateLong(target.tanggal_tebar, "yyyy-MM-dd"),
                        tanggal_panen = if (target.tanggal_panen != "null") {
                            convertStringToDateLong(target.tanggal_panen, "yyyy-MM-dd")
                        } else {
                            0L
                        },
                        keramba_id = target.keramba_id.toInt()
                    )
                }.toList()

                if (biotaDAO.getBiotaCountFromKeramba(kerambaId) > listBiota.size) {
                    biotaDAO.deleteBiotaFromKeramba(kerambaId)
                }

                biotaDAO.insertAll(listBiota)

            } else {
                if (response.body() != null) {
                    throw  Exception(response.body()!!.message)
                }
            }
        }
    }

    suspend fun refreshBiotaHistory(kerambaId: Int) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        withContext(Dispatchers.IO) {
            val response: Response<BiotaContainer> =
                monitoringService.getHistoryBiotaListAsync(token, userId, kerambaId).await()

            if (response.code() == 200) {
                val listBiotaNetwork: List<BiotaNetwork> = response.body()!!.data

                val listBiota = listBiotaNetwork.map { target ->
                    Biota(
                        biota_id = target.biota_id.toInt(),
                        jenis_biota = target.jenis_biota,
                        bobot = target.bobot.toDouble(),
                        panjang = target.panjang.toDouble(),
                        jumlah_bibit = target.jumlah_bibit.toInt(),
                        tanggal_tebar = convertStringToDateLong(target.tanggal_tebar, "yyyy-MM-dd"),
                        tanggal_panen = if (target.tanggal_panen != "null") {
                            convertStringToDateLong(target.tanggal_panen, "yyyy-MM-dd")
                        } else {
                            0L
                        },
                        keramba_id = target.keramba_id.toInt()
                    )
                }.toList()

                if (biotaDAO.getBiotaCountFromKeramba(kerambaId) > listBiota.size) {
                    biotaDAO.deleteBiotaFromKeramba(kerambaId)
                }

                biotaDAO.insertAll(listBiota)

            } else {
                if (response.body() != null) {
                    throw  Exception(response.body()!!.message)
                }
            }
        }
    }

    suspend fun addBiota(biota: BiotaDomain) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val biotaNetwork: BiotaNetwork = biotaNetworkMapper.mapToEntity(biota)

        val data = mutableMapOf<String, String>()

        data["jenis_biota"] = biotaNetwork.jenis_biota

        data["bobot"] = biotaNetwork.bobot

        data["panjang"] = biotaNetwork.panjang

        data["jumlah_bibit"] = biotaNetwork.jumlah_bibit

        data["tanggal_tebar"] = biotaNetwork.tanggal_tebar

        data["tanggal_panen"] = biotaNetwork.tanggal_panen

        data["keramba_id"] = biotaNetwork.keramba_id

        data["user_id"] = userId.toString()

        val response: Response<BiotaContainer> =
            monitoringService.addBiotaAsync(token, data).await()

        if (response.code() != 201) {
            throw Exception(response.body()!!.message)
        }
    }

    suspend fun updateBiota(biota: BiotaDomain) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val biotaNetwork: BiotaNetwork = biotaNetworkMapper.mapToEntity(biota)

        val data = mutableMapOf<String, String>()

        data["biota_id"] = biotaNetwork.biota_id

        data["jenis_biota"] = biotaNetwork.jenis_biota

        data["bobot"] = biotaNetwork.bobot

        data["panjang"] = biotaNetwork.panjang

        data["jumlah_bibit"] = biotaNetwork.jumlah_bibit

        data["tanggal_tebar"] = biotaNetwork.tanggal_tebar

        data["tanggal_panen"] = biotaNetwork.tanggal_panen

        data["keramba_id"] = biotaNetwork.keramba_id

        data["user_id"] = userId.toString()

        val response: Response<BiotaContainer> =
            monitoringService.updateBiotaAsync(token, data).await()

        if (response.code() != 200) {
            throw Exception(response.body()!!.message)
        }
    }

    suspend fun deleteBiota(biotaId: Int) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val response: Response<BiotaContainer> =
            monitoringService.deleteBiotaAsync(token, userId, biotaId).await()

        if (response.code() != 200) {
            throw Exception(response.body()!!.message)
        }
    }
}