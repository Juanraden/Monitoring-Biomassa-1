package com.kedaireka.monitoring_biomassa.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.data.network.KerambaNetwork
import com.kedaireka.monitoring_biomassa.data.network.container.KerambaContainer
import com.kedaireka.monitoring_biomassa.database.dao.KerambaDAO
import com.kedaireka.monitoring_biomassa.database.entity.Keramba
import com.kedaireka.monitoring_biomassa.database.relation.KerambaAndBiota
import com.kedaireka.monitoring_biomassa.service.MonitoringService
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import com.kedaireka.monitoring_biomassa.util.convertStringToDateLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KerambaRepository @Inject constructor(
    private val kerambaDAO: KerambaDAO,
    private val sharedPreferences: SharedPreferences,
    private val monitoringService: MonitoringService,
    private val kerambaMapper: EntityMapper<Keramba, KerambaDomain>,
    private val kerambaNetworkMapper: EntityMapper<KerambaNetwork, KerambaDomain>
) {
    val kerambaList: LiveData<List<KerambaDomain>> =
        Transformations.map(kerambaDAO.getAll().asLiveData()) { list ->
            list.map { kerambaMapper.mapFromEntity(it) }
        }

    val kerambaAndBiotaList: LiveData<List<KerambaAndBiota>> = kerambaDAO.getKerambaAndBiota().asLiveData()

    suspend fun updateLocalKeramba(id: Int, nama: String, ukuran: String, tanggal: Long){
        withContext(Dispatchers.IO) {
            kerambaDAO.updateOne(
                Keramba(
                    keramba_id = id,
                    nama_keramba = nama,
                    ukuran = ukuran.toDouble(),
                    tanggal_install = tanggal
                )
            )
        }
    }

    suspend fun deleteLocalKeramba(kerambaId: Int) {
        withContext(Dispatchers.IO) { kerambaDAO.deleteOne(kerambaId) }
    }

    suspend fun deleteAllLocalKeramba(){
        withContext(Dispatchers.IO){
            kerambaDAO.deleteAllKeramba()
        }
    }

    fun getKerambaById(kerambaId: Int): LiveData<KerambaDomain>{
        return Transformations.map(kerambaDAO.getById(kerambaId).asLiveData()){ kerambaMapper.mapFromEntity(it) }
    }

    suspend fun refreshKeramba() {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        withContext(Dispatchers.IO) {
            val response: Response<KerambaContainer> =
                monitoringService.getKerambaListAsync(token, userId).await()

            if (response.code() == 200) {
                Log.i("refreshKeramba","refesh completed")
                val listKerambaNetwork: List<KerambaNetwork> = response.body()!!.data
//
                val listKeramba: List<Keramba> = listKerambaNetwork.map { target ->
                    Keramba(
                        keramba_id = target.keramba_id.toInt(),
                        nama_keramba = target.nama,
                        ukuran = target.ukuran.toDouble(),
                        tanggal_install = convertStringToDateLong(
                            target.tanggal_install,
                            "yyyy-MM-dd"
                        )
                    )
                }.toList()

                if (kerambaDAO.getKerambaCount() > listKeramba.size){
                    kerambaDAO.deleteAllKeramba()
                }

                kerambaDAO.insertAll(listKeramba)

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

    suspend fun addKeramba(keramba: KerambaDomain): KerambaContainer {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val kerambaNetwork: KerambaNetwork = kerambaNetworkMapper.mapToEntity(keramba)

        val data = mutableMapOf<String, String>()

        data["nama"] = kerambaNetwork.nama

        data["ukuran"] = kerambaNetwork.ukuran

        data["tanggal_install"] = kerambaNetwork.tanggal_install

        data["user_id"] = userId.toString()

        val response: Response<KerambaContainer> =
            monitoringService.addKerambaAsync(token, data).await()

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

    suspend fun updateKeramba(keramba: KerambaDomain): KerambaContainer{
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val kerambaNetwork: KerambaNetwork = kerambaNetworkMapper.mapToEntity(keramba)

        val data = mutableMapOf<String, String>()

        data["keramba_id"] = kerambaNetwork.keramba_id

        data["nama"] = kerambaNetwork.nama

        data["ukuran"] = kerambaNetwork.ukuran

        data["tanggal_install"] = kerambaNetwork.tanggal_install

        data["user_id"] = userId.toString()

        val response: Response<KerambaContainer> =
            monitoringService.updateKerambaAsync(token, data).await()

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

    suspend fun deleteKeramba(kerambaId: Int): KerambaContainer{
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val data = mutableMapOf<String, String>()

        data["user_id"] = userId.toString()

        data["keramba_id"] = kerambaId.toString()

        val response: Response<KerambaContainer> =
            monitoringService.deleteKerambaAsync(token, data).await()

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