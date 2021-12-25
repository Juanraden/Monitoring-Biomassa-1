package com.kedaireka.monitoring_biomassa.repository

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.kedaireka.monitoring_biomassa.data.domain.PanenDomain
import com.kedaireka.monitoring_biomassa.data.network.PanenNetwork
import com.kedaireka.monitoring_biomassa.data.network.container.PanenContainer
import com.kedaireka.monitoring_biomassa.database.dao.PanenDAO
import com.kedaireka.monitoring_biomassa.database.entity.Panen
import com.kedaireka.monitoring_biomassa.database.relation.PanenAndBiota
import com.kedaireka.monitoring_biomassa.service.MonitoringService
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import com.kedaireka.monitoring_biomassa.util.convertStringToDateLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PanenRepository @Inject constructor(
    private val panenDAO: PanenDAO,
    private val monitoringService: MonitoringService,
    private val sharedPreferences: SharedPreferences,
    private val downloadManager: DownloadManager,
    private val panenNetworkMapper: EntityMapper<PanenNetwork, PanenDomain>
) {

    fun getlistPanen(kerambaId: Int): LiveData<List<PanenAndBiota>> =
        panenDAO.getAllPanenAndBiota(kerambaId).asLiveData()

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

    suspend fun addPanen(panen: PanenDomain) {
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

    @SuppressLint("Range")
    fun downloadExporedData(kerambaId: Int, name: String) {

        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val directory = File(Environment.DIRECTORY_DOCUMENTS)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val downloadUri =
            Uri.parse("https://web-biomassa.my.id/api/v1/export?keramba_id=${kerambaId}&user_id=${userId}")

        val request =
            DownloadManager.Request(downloadUri).addRequestHeader("Authorization", token).apply {
                setNotificationVisibility((DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED))

                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setDescription("")
                    .setDestinationInExternalPublicDir(
                        directory.toString(),
                        "Digital-Report-${name.replace("[^a-zA-Z]+".toRegex(), "-")}.xlsx"
                    )
            }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        Thread {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                cursor.close()
            }
        }.start()
    }
}