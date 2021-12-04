package com.kedaireka.monitoring_biomassa.repository

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.data.network.container.KerambaContainer
import com.kedaireka.monitoring_biomassa.data.network.KerambaNetwork
import com.kedaireka.monitoring_biomassa.database.dao.KerambaDAO
import com.kedaireka.monitoring_biomassa.database.entity.Keramba
import com.kedaireka.monitoring_biomassa.service.MonitoringService
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.text.SimpleDateFormat
import javax.inject.Inject

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

    @SuppressLint("SimpleDateFormat")
    suspend fun refreshKeramba() {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        withContext(Dispatchers.IO) {
            val response: Response<KerambaContainer> =
                monitoringService.getKerambaListAsync(token, userId).await()

            if (response.code() == 200) {

                val listKerambaNetwork: List<KerambaNetwork> = response.body()!!.data
//
                val listKeramba: List<Keramba> = listKerambaNetwork.map { target ->
                    Keramba(
                        keramba_id = target.keramba_id.toInt(),
                        nama_keramba = target.nama,
                        ukuran = target.ukuran.toDouble(),
                        tanggal_install = SimpleDateFormat("yyyy-MM-dd").parse(target.tanggal_install)!!.time
                    )
                }.toList()
                kerambaDAO.insertAll(listKeramba)
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }

    suspend fun addKeramba(keramba: KerambaDomain) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val kerambaNetwork: KerambaNetwork = kerambaNetworkMapper.mapToEntity(keramba)

        val data = mutableMapOf<String, String>()

        data["nama"] = kerambaNetwork.nama

        data["ukuran"] = kerambaNetwork.ukuran

        data["tanggal_install"] = kerambaNetwork.tanggal_install

        data["user_id"] = userId.toString()

        val response: Response<KerambaContainer> = monitoringService.addKerambaAsync(token,data).await()

        if (response.code() != 201){
            throw Exception(response.body()!!.message)
        }
    }
}