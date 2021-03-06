package com.kedaireka.monitoring_biomassa.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.kedaireka.monitoring_biomassa.data.domain.PakanDomain
import com.kedaireka.monitoring_biomassa.data.network.PakanNetwork
import com.kedaireka.monitoring_biomassa.data.network.container.PakanContainer
import com.kedaireka.monitoring_biomassa.database.dao.PakanDAO
import com.kedaireka.monitoring_biomassa.database.entity.Pakan
import com.kedaireka.monitoring_biomassa.service.MonitoringService
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PakanRepository @Inject constructor(
    private val pakanDAO: PakanDAO,
    private val sharedPreferences: SharedPreferences,
    private val monitoringService: MonitoringService,
    private val pakanMapper: EntityMapper<Pakan, PakanDomain>,
    private val pakanNetworkMapper: EntityMapper<PakanNetwork, PakanDomain>
) {
    val pakanList: LiveData<List<PakanDomain>> = Transformations.map(pakanDAO.getAll().asLiveData()){ list->
        list.map { pakanMapper.mapFromEntity(it) }}

    fun loadPakanData(pakanId: Int): LiveData<PakanDomain> = Transformations.map(
        pakanDAO.getById(pakanId).asLiveData()
    ) { pakanMapper.mapFromEntity(it) }

    suspend fun updateLocalPakan(pakanId: Int, jenis_pakan: String, deskripsi: String){
        withContext(Dispatchers.IO) {
            pakanDAO.updateOne(
                Pakan(
                    pakan_id = pakanId,
                    jenis_pakan = jenis_pakan,
                    deskripsi = deskripsi
                )
            )
        }
    }

    suspend fun deleteLocalPakan(pakanId: Int){
        withContext(Dispatchers.IO) {
            pakanDAO.deleteOne(pakanId)
        }
    }

    suspend fun deleteAllLocalPakan(){
        withContext(Dispatchers.IO) {
            pakanDAO.deleteAllPakan()
        }
    }

    suspend fun refreshPakan() {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        withContext(Dispatchers.IO) {
            val response: Response<PakanContainer> =
                monitoringService.getPakanListAsync(token, userId).await()

            if (response.code() == 200) {
                val listPakanNetwork: List<PakanNetwork> = response.body()!!.data

                val listPakan = listPakanNetwork.map { target ->
                    Pakan(
                        pakan_id = target.pakan_id.toInt(),
                        jenis_pakan = target.jenis_pakan,
                        deskripsi = target.deskripsi
                    )
                }.toList()

                if (pakanDAO.getPakanCount() > listPakan.size) {
                    pakanDAO.deleteAllPakan()
                }

                pakanDAO.insertAll(listPakan)

            } else {
                when {
                    response.code() == 500 -> {
                        throw Exception("Internal Server Error")
                    }
                    response.code() == 401 -> {
                        throw Exception("Unauthorized")
                    }
                    response.code() == 400 -> {
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        throw Exception(jsonObj.getString("message"))
                    }
                    else -> {
                        throw Exception("HTTP Request Failed")
                    }
                }
            }
        }
    }

    suspend fun addPakan(pakan: PakanDomain): PakanContainer {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val pakanNetwork: PakanNetwork = pakanNetworkMapper.mapToEntity(pakan)

        val data = mutableMapOf<String, String>()

        data["jenis_pakan"] = pakanNetwork.jenis_pakan

        data["deskripsi"] = pakanNetwork.deskripsi

        data["user_id"] = userId.toString()

        val response: Response<PakanContainer> =
            monitoringService.addPakanAsync(token, data).await()

        if (response.code() != 201) {
            when {
                response.code() == 500 -> {
                    throw Exception("Internal Server Error")
                }
                response.code() == 401 -> {
                    throw Exception("Unauthorized")
                }
                response.code() == 400 -> {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    throw Exception(jsonObj.getString("message"))
                }
                else -> {
                    throw Exception("HTTP Request Failed")
                }
            }
        } else {
            return response.body()!!
        }
    }

    suspend fun updatePakan(pakan: PakanDomain): PakanContainer {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val pakanNetwork: PakanNetwork = pakanNetworkMapper.mapToEntity(pakan)

        val data = mutableMapOf<String, String>()

        data["pakan_id"] = pakanNetwork.pakan_id

        data["jenis_pakan"] = pakanNetwork.jenis_pakan

        data["deskripsi"] = pakanNetwork.deskripsi

        data["user_id"] = userId.toString()

        val response: Response<PakanContainer> =
            monitoringService.updatePakanAsync(token, data).await()

        if (response.code() != 200) {
            when {
                response.code() == 500 -> {
                    throw Exception("Internal Server Error")
                }
                response.code() == 401 -> {
                    throw Exception("Unauthorized")
                }
                response.code() == 400 -> {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    throw Exception(jsonObj.getString("message"))
                }
                else -> {
                    throw Exception("HTTP Request Failed")
                }
            }
        } else {
            return response.body()!!
        }
    }

    suspend fun deletePakan(pakanId: Int): PakanContainer{
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val data = mutableMapOf<String, String>()

        data["user_id"] = userId.toString()

        data["pakan_id"] = pakanId.toString()

        val response: Response<PakanContainer> =
            monitoringService.deletePakanAsync(token, data).await()

        if (response.code() != 200) {
            when {
                response.code() == 500 -> {
                    throw Exception("Internal Server Error")
                }
                response.code() == 401 -> {
                    throw Exception("Unauthorized")
                }
                response.code() == 400 -> {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    throw Exception(jsonObj.getString("message"))
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