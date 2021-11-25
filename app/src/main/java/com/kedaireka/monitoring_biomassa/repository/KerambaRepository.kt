package com.kedaireka.monitoring_biomassa.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.data.network.KerambaContainer
import com.kedaireka.monitoring_biomassa.data.network.KerambaNetwork
import com.kedaireka.monitoring_biomassa.database.dao.KerambaDAO
import com.kedaireka.monitoring_biomassa.database.entity.Keramba
import com.kedaireka.monitoring_biomassa.service.MonitoringService
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class KerambaRepository @Inject constructor(
    private val kerambaDAO: KerambaDAO,
    private val monitoringService: MonitoringService,
    private val kerambaMapper: EntityMapper<Keramba, KerambaDomain>,
    private val kerambaNetworkMapper: EntityMapper<KerambaNetwork, Keramba>
) {
    val kerambaList: LiveData<List<KerambaDomain>> = Transformations.map(kerambaDAO.getAll().asLiveData()) { list ->
        list.map { kerambaMapper.mapFromEntity(it) }
    }

    suspend fun refreshKeramba() {
        withContext(Dispatchers.IO){
            try{
                val kerambaContainer: KerambaContainer = monitoringService.getKerambaListAsync().await()

                val kerambaList: List<Keramba> = kerambaContainer.data.map { kerambaNetworkMapper.mapFromEntity(it) }.toList()

                kerambaDAO.insertAll(kerambaList)
            } catch (e: Exception){
                Log.e("REFRESH_KERAMBA", "refreshKeramba() error = ${e.message}")
            }
        }
    }
}